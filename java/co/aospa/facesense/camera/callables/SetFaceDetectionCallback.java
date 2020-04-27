package co.aospa.facesense.camera.callables;

import android.hardware.Camera;
import android.hardware.Camera.FaceDetectionListener;
import co.aospa.facesense.camera.listeners.CameraListener;

public class SetFaceDetectionCallback extends CameraCallable<Void> {
    FaceDetectionListener mListener;

    public String getTag() {
        return "SetFaceDetectionCallback";
    }

    public SetFaceDetectionCallback(FaceDetectionListener faceDetectionListener, CameraListener cameraListener) {
        super(cameraListener);
        this.mListener = faceDetectionListener;
    }

    public CallableReturn<Void> call() {
        Camera camera = getCameraData().mCamera;
        if (camera == null) {
            return new CallableReturn<>(new Exception("Camera isn't opened"));
        }
        camera.setFaceDetectionListener(this.mListener);
        if (this.mListener != null) {
            camera.startFaceDetection();
        } else {
            camera.stopFaceDetection();
        }
        return new CallableReturn<>((Object) null);
    }
}
