package co.aospa.facesense.camera.callables;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import co.aospa.facesense.camera.CameraUtil;
import co.aospa.facesense.camera.listeners.CallableListener;
import co.aospa.facesense.camera.listeners.CameraListener;
import java.io.IOException;

public class StartPreviewCallable extends CameraCallable<Object> {
    private final PreviewCallback mPreviewCallback;
    private final SurfaceHolder mSurfaceHolder;
    private final SurfaceTexture mSurfaceTexture;

    public String getTag() {
        return "StartPreviewCallable";
    }

    public StartPreviewCallable(SurfaceTexture surfaceTexture, CameraListener cameraListener) {
        super(cameraListener);
        this.mPreviewCallback = new PreviewCallback() {
            public void onPreviewFrame(byte[] bArr, Camera camera) {
                if (CameraUtil.DEBUG) {
                    Log.d("StartPreviewCallable", "Start preview callback rx");
                }
            }
        };
        this.mSurfaceTexture = surfaceTexture;
        this.mSurfaceHolder = null;
    }

    public StartPreviewCallable(SurfaceHolder surfaceHolder, CameraListener cameraListener) {
        super(cameraListener);
        this.mPreviewCallback = new PreviewCallback() {
            public void onPreviewFrame(byte[] bArr, Camera camera) {
                if (CameraUtil.DEBUG) {
                    Log.d("StartPreviewCallable", "Start preview callback rx");
                }
            }
        };
        this.mSurfaceTexture = null;
        this.mSurfaceHolder = surfaceHolder;
    }

    public CallableReturn<Object> call() {
        Camera camera = getCameraData().mCamera;
        if (camera == null) {
            return new CallableReturn<>(new Exception("Camera isn't opened"));
        }
        try {
            if (this.mSurfaceTexture != null) {
                camera.setPreviewTexture(this.mSurfaceTexture);
            } else if (this.mSurfaceHolder != null) {
                camera.setPreviewDisplay(this.mSurfaceHolder);
            }
            try {
                startPreview(camera, this.mPreviewCallback);
                return new CallableReturn<>((Object) null);
            } catch (RuntimeException e) {
                return new CallableReturn<>((Exception) e);
            }
        } catch (IOException e2) {
            Log.e("StartPreviewCallable", "setPreviewDisplay failed.");
            return new CallableReturn<>((Exception) e2);
        }
    }

    public void callback(CallableReturn<Object> callableReturn) {
        if (callableReturn.exception != null) {
            CallableListener callableListener = (CallableListener) this.mCameraListener.get();
            if (callableListener != null) {
                callableListener.onError(callableReturn.exception);
            }
        }
    }

    private static void startPreview(Camera camera, PreviewCallback previewCallback) {
        camera.startPreview();
    }
}
