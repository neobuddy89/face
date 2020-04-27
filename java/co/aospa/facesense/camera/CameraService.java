package co.aospa.facesense.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.SurfaceHolder;
import co.aospa.facesense.camera.callables.AddCallbackBufferCallable;
import co.aospa.facesense.camera.callables.CameraCallable;
import co.aospa.facesense.camera.callables.CloseCameraCallable;
import co.aospa.facesense.camera.callables.OpenCameraCallable;
import co.aospa.facesense.camera.callables.ReadParamsCallable;
import co.aospa.facesense.camera.callables.SetDisplayOrientationCallback;
import co.aospa.facesense.camera.callables.SetFaceDetectionCallback;
import co.aospa.facesense.camera.callables.SetPreviewCallbackCallable;
import co.aospa.facesense.camera.callables.StartPreviewCallable;
import co.aospa.facesense.camera.callables.StopPreviewCallable;
import co.aospa.facesense.camera.callables.WriteParamsCallable;
import co.aospa.facesense.camera.listeners.ByteBufferCallbackListener;
import co.aospa.facesense.camera.listeners.CameraListener;
import co.aospa.facesense.camera.listeners.ErrorCallbackListener;
import co.aospa.facesense.camera.listeners.ReadParametersListener;

public class CameraService {
    private volatile Handler mServiceHandler;

    private static final class LazyLoader {
        public static final CameraService INSTANCE = new CameraService();
    }

    private static CameraService getInstance() {
        return LazyLoader.INSTANCE;
    }

    private CameraService() {
        CameraHandlerThread cameraHandlerThread = new CameraHandlerThread();
        cameraHandlerThread.start();
        this.mServiceHandler = new Handler(cameraHandlerThread.getLooper(), new Callback() {
            public boolean handleMessage(Message message) {
                ((CameraCallable) message.obj).run();
                return true;
            }
        });
    }

    public static void openCamera(int i, ErrorCallbackListener errorCallbackListener, CameraListener cameraListener) {
        getInstance().addCallable(new OpenCameraCallable(i, errorCallbackListener, cameraListener));
    }

    public static void closeCamera(CameraListener cameraListener) {
        CameraService instance = getInstance();
        clearQueue();
        instance.addCallable(new CloseCameraCallable(cameraListener));
    }

    public static void readParameters(ReadParametersListener readParametersListener, CameraListener cameraListener) {
        getInstance().addCallable(new ReadParamsCallable(readParametersListener, cameraListener));
    }

    public static void writeParameters(CameraListener cameraListener) {
        getInstance().addCallable(new WriteParamsCallable(cameraListener));
    }

    public static void startPreview(SurfaceTexture surfaceTexture, CameraListener cameraListener) {
        getInstance().addCallable(new StartPreviewCallable(surfaceTexture, cameraListener));
    }

    public static void startPreview(SurfaceHolder surfaceHolder, CameraListener cameraListener) {
        getInstance().addCallable(new StartPreviewCallable(surfaceHolder, cameraListener));
    }

    public static void stopPreview(CameraListener cameraListener) {
        getInstance().addCallable(new StopPreviewCallable(cameraListener));
    }

    public static void addCallbackBuffer(byte[] bArr, CameraListener cameraListener) {
        getInstance().addCallable(new AddCallbackBufferCallable(bArr, cameraListener));
    }

    public static void setPreviewCallback(ByteBufferCallbackListener byteBufferCallbackListener, boolean z, CameraListener cameraListener) {
        getInstance().addCallable(new SetPreviewCallbackCallable(byteBufferCallbackListener, z, cameraListener));
    }

    public static void setFaceDetectionCallback(FaceDetectionListener faceDetectionListener, CameraListener cameraListener) {
        getInstance().addCallable(new SetFaceDetectionCallback(faceDetectionListener, cameraListener));
    }

    public static void setDisplayOrientationCallback(int i, CameraListener cameraListener) {
        getInstance().addCallable(new SetDisplayOrientationCallback(i, cameraListener));
    }

    public static void clearQueue() {
        getInstance().mServiceHandler.removeMessages(1);
    }

    private void addCallable(CameraCallable cameraCallable) {
        this.mServiceHandler.sendMessage(this.mServiceHandler.obtainMessage(1, cameraCallable));
    }
}
