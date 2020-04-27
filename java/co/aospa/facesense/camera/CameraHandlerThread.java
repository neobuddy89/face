package co.aospa.facesense.camera;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.HandlerThread;

public class CameraHandlerThread extends HandlerThread {
    private CameraData mCameraData = new CameraData();

    public static final class CameraData {
        public Camera mCamera;
        public int mCameraId;
        public Parameters mParameters;

        private CameraData() {
            this.mCamera = null;
            this.mCameraId = -1;
        }
    }

    public CameraHandlerThread() {
        super("CameraHandlerThread", -2);
    }

    public CameraData getCameraData() {
        return this.mCameraData;
    }
}
