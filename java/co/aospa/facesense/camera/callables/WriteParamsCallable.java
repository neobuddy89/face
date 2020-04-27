package co.aospa.facesense.camera.callables;

import android.hardware.Camera;
import co.aospa.facesense.camera.listeners.CameraListener;

public class WriteParamsCallable extends CameraCallable {
    public String getTag() {
        return "WriteParamsCallable";
    }

    public WriteParamsCallable(CameraListener cameraListener) {
        super(cameraListener);
    }

    public CallableReturn call() {
        Camera camera = getCameraData().mCamera;
        if (camera == null) {
            return new CallableReturn(new Exception("Camera isn't opened"));
        }
        try {
            camera.setParameters(getCameraParameters());
            return new CallableReturn((Object) null);
        } catch (Exception e) {
            return new CallableReturn(e);
        }
    }
}
