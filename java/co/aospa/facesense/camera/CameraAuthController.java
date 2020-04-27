package co.aospa.facesense.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import co.aospa.facesense.camera.listeners.ByteBufferCallbackListener;
import co.aospa.facesense.camera.listeners.CameraListener;
import co.aospa.facesense.camera.listeners.ErrorCallbackListener;
import co.aospa.facesense.camera.listeners.ReadParametersListener;
import co.aospa.facesense.util.Util;
import java.nio.ByteBuffer;

public class CameraAuthController {
    private static HandlerThread mThread;
    private ByteBufferCallbackListener mByteBufferListener = new ByteBufferCallbackListener() {
        public void onEventCallback(int i, Object obj) {
            CameraAuthController cameraAuthController = CameraAuthController.this;
            if (!cameraAuthController.mComparing) {
                cameraAuthController.mComparing = true;
                if (Util.DEBUG_INFO) {
                    Log.d("CameraAuthController", "Camera frame arrival");
                }
                Message obtain = Message.obtain(CameraAuthController.this.mFaceHandler, 1003);
                obtain.obj = obj;
                CameraAuthController.this.mFaceHandler.sendMessage(obtain);
            }
        }
    };
    public ServiceCallback mCallback;
    private int mCamID;
    private CameraListener mCameraListener = new CameraListener() {
        public void onComplete(Object obj) {
            CameraAuthController.this.mHandler.sendEmptyMessage(102);
        }

        public void onError(Exception exc) {
            CameraAuthController.this.mHandler.sendEmptyMessage(101);
        }
    };
    public Parameters mCameraParam;
    private CameraState mCameraState = CameraState.CAMERA_IDLE;
    public boolean mCompareSuccess = false;
    public boolean mComparing = false;
    public Context mContext;
    protected ErrorCallbackListener mErrorCallbackListener = new ErrorCallbackListener() {
        public void onEventCallback(int i, Object obj) {
            CameraAuthController.this.mHandler.sendEmptyMessage(101);
        }
    };
    public Handler mFaceHandler;
    public ByteBuffer mFrame;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (Util.DEBUG_INFO) {
                Log.i("CameraAuthController", "handleMessage : " + message);
            }
            int i = message.what;
            if (i == 1 || i == 2) {
                if (Util.DEBUG_INFO) {
                    Log.d("CameraAuthController", "timeout, sendBroadcast faceId stop");
                }
                CameraAuthController.this.stopSelf();
                ServiceCallback serviceCallback = CameraAuthController.this.mCallback;
                if (serviceCallback != null) {
                    serviceCallback.onTimeout();
                }
                CameraAuthController.this.mIsTimeout = true;
            } else if (i == 101) {
                CameraAuthController.this.stopSelf();
                ServiceCallback serviceCallback2 = CameraAuthController.this.mCallback;
                if (serviceCallback2 != null) {
                    serviceCallback2.onCameraError();
                }
            } else if (i == 102) {
                CameraAuthController.this.handleCameraStateUpdate();
            }
        }
    };
    public boolean mIsTimeout = false;
    public float mLux = 0.0f;
    public Size mPreviewSize;
    private ReadParametersListener mReadParamListener = new ReadParametersListener() {
        public void onEventCallback(int i, Object obj) {
            CameraAuthController.this.mCameraParam = (Parameters) obj;
        }
    };
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == 5) {
                CameraAuthController.this.mLux = sensorEvent.values[0];
            }
        }
    };
    private SensorManager mSensorManager;
    private boolean mStop = false;
    private SurfaceTexture mTexture = null;

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
            STATE[CameraState.CAMERA_OPENED.ordinal()] = 1;
            STATE[CameraState.CAMERA_PARAM_READ.ordinal()] = 2;
            STATE[CameraState.CAMERA_PARAM_SET.ordinal()] = 3;
            try {
                STATE[CameraState.CAMERA_PREVIEW_STOPPING.ordinal()] = 4;
            } catch (NoSuchFieldError unused) {
            }
        }
    }

    private class FaceHandler extends Handler {
        public FaceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            int i;
            if (Util.DEBUG_INFO) {
                Log.i("CameraAuthController", "FaceHandler handle msg : " + message);
            }
            int i2 = message.what;
            if (i2 == 1003) {
                ByteBuffer byteBuffer = (ByteBuffer) message.obj;
                ServiceCallback serviceCallback = CameraAuthController.this.mCallback;
                if (serviceCallback != null) {
                    byte[] array = byteBuffer.array();
                    Size size = CameraAuthController.this.mPreviewSize;
                    i = serviceCallback.handlePreviewData(array, size.width, size.height);
                } else {
                    i = 0;
                }
                CameraAuthController cameraAuthController = CameraAuthController.this;
                if (cameraAuthController.mIsTimeout) {
                    if (Util.DEBUG_INFO) {
                        Log.d("CameraAuthController", "mIsTimeout is true, ignore result");
                    }
                } else if (i == 0) {
                    cameraAuthController.mCompareSuccess = true;
                    cameraAuthController.stopSelf();
                } else {
                    if (i != 5) {
                        cameraAuthController.mHandler.removeMessages(1);
                    }
                    ByteBuffer byteBuffer2 = CameraAuthController.this.mFrame;
                    if (byteBuffer2 != null) {
                        CameraService.addCallbackBuffer(byteBuffer2.array(), null);
                        CameraAuthController.this.mComparing = false;
                    }
                }
            } else if (i2 == 1004) {
                CameraAuthController cameraAuthController2 = CameraAuthController.this;
                ServiceCallback serviceCallback2 = cameraAuthController2.mCallback;
                if (serviceCallback2 != null) {
                    serviceCallback2.setDetectArea(cameraAuthController2.mPreviewSize);
                }
            }
        }
    }

    public interface ServiceCallback {
        int handlePreviewData(byte[] bArr, int i, int i2);

        void onCameraError();

        void onTimeout();

        void setDetectArea(Size size);
    }

    public CameraAuthController(Context context, ServiceCallback serviceCallback) {
        this.mContext = context;
        this.mCallback = serviceCallback;
    }

    public boolean start() {
        Log.i("CameraAuthController", "start enter");
        CameraInfo cameraInfo = new CameraInfo();
        int numberOfCameras = Camera.getNumberOfCameras();
        this.mCamID = -1;
        int i = 0;
        while (true) {
            if (i >= numberOfCameras) {
                break;
            }
            Camera.getCameraInfo(i, cameraInfo);
            int i2 = cameraInfo.facing;
            if (i2 != 0 && i2 == 1) {
                Log.d("CameraAuthController", "Front camera found");
                this.mCamID = i;
                break;
            }
            i++;
        }
        if (this.mCamID == -1) {
            Log.d("CameraAuthController", "No front camera, stop face unlock");
            return false;
        }
        initWorkHandler();
        CameraService.openCamera(Util.getCustomCameraId(this.mCamID), this.mErrorCallbackListener, this.mCameraListener);
        this.mCameraState = CameraState.CAMERA_OPENED;
        this.mSensorManager = (SensorManager) this.mContext.getSystemService("sensor");
        SensorManager sensorManager = this.mSensorManager;
        sensorManager.registerListener(this.mSensorEventListener, sensorManager.getDefaultSensor(5), 3);
        this.mIsTimeout = false;
        resetTimeout(0);
        this.mStop = false;
        Log.i("CameraAuthController", "start exit");
        return true;
    }

    public void stopSelf() {
        this.mHandler.post(new Runnable() {
            public void run() {
                CameraAuthController.this.stop();
            }
        });
    }

    public void stop() {
        Log.i("CameraAuthController", "stop enter");
        this.mIsTimeout = true;
        Handler handler = this.mFaceHandler;
        if (handler != null) {
            handler.removeMessages(1003);
            this.mFaceHandler.removeMessages(1004);
        }
        Handler handler2 = this.mHandler;
        if (handler2 != null) {
            handler2.removeMessages(1);
            this.mHandler.removeMessages(2);
        }
        CameraService.clearQueue();
        CameraState cameraState = this.mCameraState;
        if (cameraState == CameraState.CAMERA_PREVIEW_STARTED) {
            CameraService.addCallbackBuffer(null, null);
            this.mFrame = null;
            this.mCameraState = CameraState.CAMERA_PREVIEW_STOPPING;
            CameraService.stopPreview(null);
            CameraService.closeCamera(null);
        } else if (cameraState != CameraState.CAMERA_IDLE) {
            CameraService.closeCamera(null);
        }
        SensorManager sensorManager = this.mSensorManager;
        if (sensorManager != null) {
            sensorManager.unregisterListener(this.mSensorEventListener);
            this.mSensorManager = null;
            this.mSensorEventListener = null;
        }
        this.mCallback = null;
        this.mStop = true;
        if (!this.mCompareSuccess) {
            this.mCompareSuccess = false;
        }
        Log.i("CameraAuthController", "stop exit");
    }

    public void resetTimeout(int i) {
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        this.mHandler.sendEmptyMessageDelayed(1, i > 0 ? (long) i : 3000);
        this.mHandler.sendEmptyMessageDelayed(2, i > 0 ? (long) i : 4800);
    }

    private void initWorkHandler() {
        if (mThread == null) {
            mThread = new HandlerThread("Camera Face unlock");
            mThread.setPriority(10);
            mThread.start();
        }
        this.mFaceHandler = new FaceHandler(mThread.getLooper());
    }

    public void handleCameraStateUpdate() {
        if (!this.mStop) {
            int i = CameraStateOrdinal.STATE[this.mCameraState.ordinal()];
            if (i == 1) {
                this.mCameraState = CameraState.CAMERA_PARAM_READ;
                CameraService.readParameters(this.mReadParamListener, this.mCameraListener);
            } else if (i == 2) {
                this.mCameraState = CameraState.CAMERA_PARAM_SET;
                this.mPreviewSize = CameraUtil.calBestPreviewSize(this.mCameraParam, 480, 640);
                Size size = this.mPreviewSize;
                int i2 = size.width;
                int i3 = size.height;
                this.mCameraParam.setPreviewSize(i2, i3);
                this.mCameraParam.setPreviewFormat(17);
                this.mFrame = ByteBuffer.allocateDirect(getPreviewBufferSize(i2, i3, 17));
                CameraService.writeParameters(this.mCameraListener);
                Log.d("CameraAuthController", "preview size " + this.mPreviewSize.height + " " + this.mPreviewSize.width);
                this.mFaceHandler.sendEmptyMessage(1004);
            } else if (i == 3) {
                this.mCameraState = CameraState.CAMERA_PREVIEW_STARTED;
                if (this.mTexture == null) {
                    this.mTexture = new SurfaceTexture(10);
                }
                CameraService.addCallbackBuffer(this.mFrame.array(), null);
                CameraService.setPreviewCallback(this.mByteBufferListener, true, null);
                CameraService.startPreview(this.mTexture, this.mCameraListener);
            } else if (i == 4) {
                CameraService.closeCamera(null);
            }
        }
    }

    private int getPreviewBufferSize(int i, int i2, int i3) {
        if (i3 != 20) {
            return (((i2 * i) * ImageFormat.getBitsPerPixel(i3)) / 8) + 32;
        }
        int ceil = ((int) Math.ceil(((double) i) / 16.0d)) * 16;
        return (ceil * i2) + ((((((int) Math.ceil((((double) ceil) / 2.0d) / 16.0d)) * 16) * i2) / 2) * 2);
    }
}
