package co.aospa.facesense.camera.callables;

import android.hardware.Camera;
import android.util.Log;
import co.aospa.facesense.camera.CameraHandlerThread.CameraData;
import co.aospa.facesense.camera.CameraUtil;
import co.aospa.facesense.camera.listeners.CameraListener;
import java.io.IOException;

public class CloseCameraCallable extends CameraCallable {
    public String getTag() {
        return "CloseCameraCallable";
    }

    public CloseCameraCallable(CameraListener cameraListener) {
        super(cameraListener);
    }

    public CallableReturn call() {
        CameraData cameraData = getCameraData();
        Camera camera = cameraData.mCamera;
        if (camera == null) {
            return new CallableReturn(new Exception("Camera isn't opened"));
        }
        try {
            camera.reconnect();
            if (CameraUtil.DEBUG) {
                Log.d("CloseCameraCallable", "releasing camera");
            }
            camera.setErrorCallback(null);
            camera.release();
            cameraData.mCamera = null;
            cameraData.mCameraId = -1;
            cameraData.mParameters = null;
            return new CallableReturn((Object) null);
        } catch (IOException e) {
            if (CameraUtil.DEBUG) {
                Log.e("CloseCameraCallable", "reconnect failed.");
            }
            return new CallableReturn((Exception) e);
        }
    }
}
