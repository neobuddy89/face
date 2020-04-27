package co.aospa.facesense.camera.callables;

import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.util.Log;
import co.aospa.facesense.camera.CameraHandlerThread.CameraData;
import co.aospa.facesense.camera.CameraUtil;
import co.aospa.facesense.camera.listeners.CameraListener;
import co.aospa.facesense.camera.listeners.ErrorCallbackListener;

public class OpenCameraCallable extends CameraCallable {
    private final int mCameraId;
    public final ErrorCallbackListener mErrorListener;

    public String getTag() {
        return "OpenCameraCallable";
    }

    public OpenCameraCallable(int i, ErrorCallbackListener errorCallbackListener, CameraListener cameraListener) {
        super(cameraListener);
        this.mCameraId = i;
        this.mErrorListener = errorCallbackListener;
    }

    public CallableReturn call() {
        if (CameraUtil.DEBUG) {
            Log.d("OpenCameraCallable", "device: connect device async task: start");
        }
        if (getCameraData().mCamera != null && getCameraData().mCameraId == this.mCameraId) {
            if (CameraUtil.DEBUG) {
                Log.d("OpenCameraCallable", "Camera is already opened");
            }
            setErrorCallback(getCameraData().mCamera);
            return new CallableReturn((Object) null);
        } else if (getCameraData().mCamera != null) {
            return new CallableReturn(new Exception("Other camera is all ready opened"));
        } else {
            try {
                openCamera();
                if (CameraUtil.DEBUG) {
                    Log.d("OpenCameraCallable", "device: connect device async task:open camera complete");
                }
                return new CallableReturn((Object) null);
            } catch (Exception e) {
                return new CallableReturn(e);
            }
        }
    }

    private void openCamera() {
        CameraData cameraData = getCameraData();
        try {
            if (CameraUtil.DEBUG) {
                Log.d("OpenCameraCallable", "open camera " + this.mCameraId);
            }
            if (cameraData.mCameraId != this.mCameraId) {
                cameraData.mCamera = openCamera(this.mCameraId);
                cameraData.mCameraId = this.mCameraId;
            }
            if (CameraUtil.DEBUG) {
                Log.d("OpenCameraCallable", "open camera success, id: " + getCameraData().mCameraId);
            }
            setErrorCallback(cameraData.mCamera);
        } catch (RuntimeException e) {
            if (CameraUtil.DEBUG) {
                Log.e("OpenCameraCallable", "fail to connect Camera", e);
            }
        }
    }

    private void setErrorCallback(Camera camera) {
        if (CameraUtil.DEBUG) {
            Log.d("OpenCameraCallable", "set error callback");
        }
        camera.setErrorCallback(new ErrorCallback() {
            public void onError(int i, Camera camera) {
                ErrorCallbackListener errorCallbackListener = OpenCameraCallable.this.mErrorListener;
                if (errorCallbackListener != null) {
                    errorCallbackListener.onEventCallback(i, null);
                }
            }
        });
    }

    private static Camera openCamera(int i) {
        Class cls = Integer.TYPE;
        Class[] clsArr = {cls, cls};
        return Camera.open(i);
    }
}
