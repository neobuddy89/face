package co.aospa.facesense.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import co.aospa.facesense.FaceApplication;
import co.aospa.facesense.camera.listeners.ByteBufferCallbackListener;
import co.aospa.facesense.camera.listeners.CameraListener;
import co.aospa.facesense.camera.listeners.ErrorCallbackListener;
import co.aospa.facesense.camera.listeners.ReadParametersListener;
import co.aospa.facesense.util.Util;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

public class CameraEnrollController {
    private static HandlerThread mThread;
    private static CameraEnrollController sInstance;
    private ByteBufferCallbackListener mByteBufferListener = new ByteBufferCallbackListener() {
        public void onEventCallback(int i, Object obj) {
            CameraEnrollController cameraEnrollController = CameraEnrollController.this;
            if (!cameraEnrollController.mHandling) {
                cameraEnrollController.mHandling = true;
                if (Util.DEBUG_INFO) {
                    Log.d("CameraEnrollController", "Camera frame arrival");
                }
                Message obtain = Message.obtain(CameraEnrollController.this.mFaceUnlockHandler, 1003);
                obtain.obj = obj;
                CameraEnrollController.this.mFaceUnlockHandler.sendMessage(obtain);
            }
        }
    };
    private int mCamID;
    public ArrayList<CameraCallback> mCameraCallbacks = new ArrayList<>();
    public CameraListener mCameraListener = new CameraListener() {
        public void onComplete(Object obj) {
            CameraEnrollController.this.mHandler.sendEmptyMessage(102);
        }

        public void onError(Exception exc) {
            CameraEnrollController.this.mHandler.sendEmptyMessage(101);
        }
    };
    public Parameters mCameraParam;
    public CameraState mCameraState = CameraState.CAMERA_IDLE;
    private Context mContext;
    protected ErrorCallbackListener mErrorCallbackListener = new ErrorCallbackListener() {
        public void onEventCallback(int i, Object obj) {
            CameraEnrollController.this.mHandler.sendEmptyMessage(101);
        }
    };
    public Handler mFaceUnlockHandler;
    public ByteBuffer mFrame;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (Util.DEBUG_INFO) {
                Log.i("CameraEnrollController", "handleMessage : " + message);
            }
            switch (message.what) {
                case 101:
                    Iterator<CameraCallback> it = CameraEnrollController.this.mCameraCallbacks.iterator();
                    while (it.hasNext()) {
                        it.next().onCameraError();
                    }
                    return;
                case 102:
                    CameraEnrollController.this.handleCameraStateUpdate();
                    return;
                case 103:
                    CameraState cameraState = CameraState.CAMERA_PREVIEW_STARTED;
                    CameraEnrollController cameraEnrollController = CameraEnrollController.this;
                    if (cameraState == cameraEnrollController.mCameraState && !cameraEnrollController.mPreviewStarted) {
                        CameraService.startPreview(cameraEnrollController.mHolder, cameraEnrollController.mCameraListener);
                    }
                    return;
                default:
                    return;
            }
        }
    };
    public boolean mHandling = false;
    public SurfaceHolder mHolder;
    public Size mPreviewSize;
    public boolean mPreviewStarted = false;
    private ReadParametersListener mReadParamListener = new ReadParametersListener() {
        public void onEventCallback(int i, Object obj) {
            CameraEnrollController.this.mCameraParam = (Parameters) obj;
        }
    };
    private boolean mStop = false;
    public boolean mSurfaceCreated = false;
    private SurfaceTexture mTexture;

    public interface CameraCallback {
        int handleSaveFeature(byte[] bArr, int i, int i2, int i3);

        void handleSaveFeatureResult(int i);

        void onCameraError();

        void onFaceDetected();

        void setDetectArea(Size size);
    }

    private enum CameraState {
        CAMERA_IDLE,
        CAMERA_OPENED,
        CAMERA_PARAM_READ,
        CAMERA_PARAM_SET,
        CAMERA_PREVIEW_STARTED,
        CAMERA_PREVIEW_STOPPING
    }

    static class CameraStateOrdinal {
        static final int[] STATE = new int[CameraState.values().length];

        static {
            STATE[CameraState.CAMERA_IDLE.ordinal()] = 1;
            STATE[CameraState.CAMERA_OPENED.ordinal()] = 2;
            STATE[CameraState.CAMERA_PARAM_READ.ordinal()] = 3;
            STATE[CameraState.CAMERA_PARAM_SET.ordinal()] = 4;
            STATE[CameraState.CAMERA_PREVIEW_STARTED.ordinal()] = 5;
            try {
                STATE[CameraState.CAMERA_PREVIEW_STOPPING.ordinal()] = 6;
            } catch (NoSuchFieldError unused) {
            }
        }
    }

    private class FaceHandler extends Handler {
        public FaceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            if (Util.DEBUG_INFO) {
                Log.i("CameraEnrollController", "FaceHandler handle msg : " + message);
            }
            int i = message.what;
            if (i == 1003) {
                ByteBuffer byteBuffer = (ByteBuffer) message.obj;
                Iterator<CameraCallback> it = CameraEnrollController.this.mCameraCallbacks.iterator();
                int i2 = -1;
                while (it.hasNext()) {
                    byte[] array = byteBuffer.array();
                    Size size = CameraEnrollController.this.mPreviewSize;
                    int handleSaveFeature = it.next().handleSaveFeature(array, size.width, size.height, 0);
                    if (handleSaveFeature != -1) {
                        i2 = handleSaveFeature;
                    }
                }
                Iterator<CameraCallback> it2 = CameraEnrollController.this.mCameraCallbacks.iterator();
                while (it2.hasNext()) {
                    it2.next().handleSaveFeatureResult(i2);
                }
                ByteBuffer byteBuffer2 = CameraEnrollController.this.mFrame;
                if (byteBuffer2 != null) {
                    CameraService.addCallbackBuffer(byteBuffer2.array(), null);
                    CameraEnrollController.this.mHandling = false;
                }
            } else if (i == 1004) {
                Iterator<CameraCallback> it3 = CameraEnrollController.this.mCameraCallbacks.iterator();
                while (it3.hasNext()) {
                    it3.next().setDetectArea(CameraEnrollController.this.mPreviewSize);
                }
            }
        }
    }

    public static CameraEnrollController getInstance() {
        if (sInstance == null) {
            sInstance = new CameraEnrollController(FaceApplication.getApp());
        }
        return sInstance;
    }

    private CameraEnrollController(Context context) {
        this.mContext = context;
        initWorkHandler();
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            synchronized (this.mCameraState) {
                if (this.mCameraState == CameraState.CAMERA_PREVIEW_STARTED) {
                    CameraService.clearQueue();
                    this.mCameraState = CameraState.CAMERA_PREVIEW_STOPPING;
                    CameraService.setFaceDetectionCallback(null, null);
                    CameraService.stopPreview(null);
                    CameraService.closeCamera(null);
                }
            }
        }
        this.mHolder = surfaceHolder;
        SurfaceHolder surfaceHolder2 = this.mHolder;
        if (surfaceHolder2 != null) {
            surfaceHolder2.setKeepScreenOn(true);
            this.mHolder.addCallback(new Callback() {
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                }

                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                }

                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    CameraEnrollController cameraEnrollController = CameraEnrollController.this;
                    cameraEnrollController.mSurfaceCreated = true;
                    cameraEnrollController.mHandler.sendEmptyMessage(103);
                }
            });
            if (this.mHolder.getSurface() != null) {
                this.mSurfaceCreated = true;
            }
        }
    }

    public boolean start(CameraCallback cameraCallback, int i) {
        Log.i("CameraEnrollController", "new start : " + cameraCallback);
        this.mStop = false;
        synchronized (this.mCameraCallbacks) {
            if (!this.mCameraCallbacks.contains(cameraCallback)) {
                this.mCameraCallbacks.add(cameraCallback);
            }
            this.mHandler.sendEmptyMessage(102);
        }
        return true;
    }

    public boolean stop(CameraCallback cameraCallback) {
        Log.i("CameraEnrollController", "stop : " + cameraCallback);
        this.mStop = true;
        synchronized (this.mCameraCallbacks) {
            if (!this.mCameraCallbacks.contains(cameraCallback)) {
                Log.e("CameraEnrollController", "callback has been released!");
                return false;
            }
            this.mCameraCallbacks.remove(cameraCallback);
            this.mFaceUnlockHandler.removeMessages(1004);
            CameraService.clearQueue();
            this.mCameraState = CameraState.CAMERA_PREVIEW_STOPPING;
            CameraService.setFaceDetectionCallback(null, null);
            CameraService.stopPreview(null);
            CameraService.closeCamera(null);
            this.mHolder = null;
            this.mCameraState = CameraState.CAMERA_IDLE;
            this.mCameraParam = null;
            this.mPreviewSize = null;
            if (this.mCameraCallbacks.size() > 0) {
                return true;
            }
            return false;
        }
    }

    public void handleCameraStateUpdate() {
        if (!this.mStop) {
            synchronized (this.mCameraState) {
                switch (CameraStateOrdinal.STATE[this.mCameraState.ordinal()]) {
                    case 1:
                        CameraInfo cameraInfo = new CameraInfo();
                        int numberOfCameras = Camera.getNumberOfCameras();
                        this.mCamID = -1;
                        int i = 0;
                        while (true) {
                            if (i < numberOfCameras) {
                                Camera.getCameraInfo(i, cameraInfo);
                                if (cameraInfo.facing == 0 || cameraInfo.facing != 1) {
                                    i++;
                                } else {
                                    Log.d("CameraEnrollController", "Front camera found");
                                    this.mCamID = i;
                                }
                            }
                        }
                        if (this.mCamID == -1) {
                            Log.d("CameraEnrollController", "No front camera, stop face unlock");
                            this.mHandler.sendEmptyMessage(101);
                            break;
                        } else {
                            CameraService.openCamera(Util.getCustomCameraId(this.mCamID), this.mErrorCallbackListener, this.mCameraListener);
                            this.mCameraState = CameraState.CAMERA_OPENED;
                            break;
                        }
                    case 2:
                        this.mCameraState = CameraState.CAMERA_PARAM_READ;
                        CameraService.readParameters(this.mReadParamListener, this.mCameraListener);
                        break;
                    case 3:
                        this.mCameraState = CameraState.CAMERA_PARAM_SET;
                        this.mPreviewSize = CameraUtil.calBestPreviewSize(this.mCameraParam, 480, 640);
                        int i2 = this.mPreviewSize.width;
                        int i3 = this.mPreviewSize.height;
                        this.mCameraParam.setPreviewSize(i2, i3);
                        this.mCameraParam.setPreviewFormat(17);
                        this.mFrame = ByteBuffer.allocateDirect(getPreviewBufferSize(i2, i3, 17));
                        CameraService.writeParameters(this.mCameraListener);
                        Log.d("CameraEnrollController", "preview size " + this.mPreviewSize.height + " " + this.mPreviewSize.width);
                        break;
                    case 4:
                        this.mCameraState = CameraState.CAMERA_PREVIEW_STARTED;
                        CameraService.addCallbackBuffer(this.mFrame.array(), null);
                        CameraService.setDisplayOrientationCallback(getCameraAngle(), null);
                        CameraService.setPreviewCallback(this.mByteBufferListener, true, null);
                        if (this.mHolder != null) {
                            if (this.mSurfaceCreated) {
                                CameraService.startPreview(this.mHolder, this.mCameraListener);
                                this.mPreviewStarted = true;
                                break;
                            }
                        } else {
                            this.mTexture = new SurfaceTexture(10);
                            CameraService.startPreview(this.mTexture, this.mCameraListener);
                            break;
                        }
                        break;
                    case 5:
                        CameraService.setFaceDetectionCallback(new FaceDetectionListener() {
                            public void onFaceDetection(Face[] faceArr, Camera camera) {
                                if (faceArr.length > 0) {
                                    Iterator<CameraCallback> it = CameraEnrollController.this.mCameraCallbacks.iterator();
                                    while (it.hasNext()) {
                                        it.next().onFaceDetected();
                                    }
                                }
                            }
                        }, null);
                        break;
                    case 6:
                        CameraService.closeCamera(null);
                        break;
                }
            }
        }
    }

    private void initWorkHandler() {
        if (mThread == null) {
            mThread = new HandlerThread("Camera Face unlock");
            mThread.setPriority(10);
            mThread.start();
        }
        this.mFaceUnlockHandler = new FaceHandler(mThread.getLooper());
    }

    private int getPreviewBufferSize(int i, int i2, int i3) {
        if (i3 != 20) {
            return (((i2 * i) * ImageFormat.getBitsPerPixel(i3)) / 8) + 32;
        }
        int ceil = ((int) Math.ceil(((double) i) / 16.0d)) * 16;
        return (ceil * i2) + ((((((int) Math.ceil((((double) ceil) / 2.0d) / 16.0d)) * 16) * i2) / 2) * 2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0035  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x003f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getCameraAngle() {
        /*
            r3 = this;
            android.hardware.Camera$CameraInfo r0 = new android.hardware.Camera$CameraInfo
            r0.<init>()
            int r1 = r3.mCamID
            android.hardware.Camera.getCameraInfo(r1, r0)
            android.content.Context r3 = r3.mContext
            java.lang.Class<android.view.WindowManager> r1 = android.view.WindowManager.class
            java.lang.Object r3 = r3.getSystemService(r1)
            android.view.WindowManager r3 = (android.view.WindowManager) r3
            android.view.Display r3 = r3.getDefaultDisplay()
            int r3 = r3.getRotation()
            r1 = 1
            if (r3 == 0) goto L_0x0030
            if (r3 != r1) goto L_0x0024
            r3 = 90
            goto L_0x0031
        L_0x0024:
            r2 = 2
            if (r3 != r2) goto L_0x002a
            r3 = 180(0xb4, float:2.52E-43)
            goto L_0x0031
        L_0x002a:
            r2 = 3
            if (r3 != r2) goto L_0x0030
            r3 = 270(0x10e, float:3.78E-43)
            goto L_0x0031
        L_0x0030:
            r3 = 0
        L_0x0031:
            int r2 = r0.facing
            if (r2 != r1) goto L_0x003f
            int r0 = r0.orientation
            int r0 = r0 + r3
            int r0 = r0 % 360
            int r3 = 360 - r0
            int r3 = r3 % 360
            return r3
        L_0x003f:
            int r0 = r0.orientation
            int r0 = r0 - r3
            int r0 = r0 + 360
            int r0 = r0 % 360
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: co.aospa.facesense.camera.CameraEnrollController.getCameraAngle():int");
    }
}
