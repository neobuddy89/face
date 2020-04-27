package co.aospa.facesense.camera.callables;

import android.hardware.Camera;
import co.aospa.facesense.camera.listeners.CameraListener;
import co.aospa.facesense.camera.listeners.ReadParametersListener;

public class ReadParamsCallable extends CameraCallable {
    ReadParametersListener mReadListener;

    public String getTag() {
        return "ReadParamsCallable";
    }

    public ReadParamsCallable(ReadParametersListener readParametersListener, CameraListener cameraListener) {
        super(cameraListener);
        this.mReadListener = readParametersListener;
    }

    public CallableReturn call() {
        Camera camera = getCameraData().mCamera;
        if (camera == null) {
            return new CallableReturn(new Exception("Camera isn't opened"));
        }
        try {
            getCameraData().mParameters = camera.getParameters();
            this.mReadListener.onEventCallback(0, getCameraData().mParameters);
            return new CallableReturn((Object) null);
        } catch (Exception e) {
            return new CallableReturn(e);
        }
    }
}
