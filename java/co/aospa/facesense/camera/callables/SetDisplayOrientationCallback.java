package co.aospa.facesense.camera.callables;

import android.hardware.Camera;
import co.aospa.facesense.camera.listeners.CameraListener;

public class SetDisplayOrientationCallback extends CameraCallable {
    private static final String TAG = AddCallbackBufferCallable.class.getSimpleName();
    private final int mAngle;

    public SetDisplayOrientationCallback(int i, CameraListener cameraListener) {
        super(cameraListener);
        this.mAngle = i;
    }

    public CallableReturn<Void> call() {
        Camera camera = getCameraData().mCamera;
        if (camera == null) {
            return new CallableReturn<>(new Exception("Camera isn't opened"));
        }
        camera.setDisplayOrientation(this.mAngle);
        return new CallableReturn<>((Object) null);
    }

    public String getTag() {
        return TAG;
    }
}
