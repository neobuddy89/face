package co.aospa.facesense.camera.callables;

import android.hardware.Camera;
import co.aospa.facesense.camera.listeners.CameraListener;

public class AddCallbackBufferCallable extends CameraCallable<Void> {
    private final byte[] mBuffer;

    public String getTag() {
        return "AddCallbackBufferCallable";
    }

    public AddCallbackBufferCallable(byte[] bArr, CameraListener cameraListener) {
        super(cameraListener);
        this.mBuffer = bArr;
    }

    public CallableReturn<Void> call() {
        Camera camera = getCameraData().mCamera;
        if (camera == null) {
            return new CallableReturn<>(new Exception("Camera isn't opened"));
        }
        camera.addCallbackBuffer(this.mBuffer);
        return new CallableReturn<>((Object) null);
    }
}
