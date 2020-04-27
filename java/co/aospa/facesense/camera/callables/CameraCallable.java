package co.aospa.facesense.camera.callables;

import android.hardware.Camera.Parameters;
import android.os.SystemClock;
import android.util.Log;
import co.aospa.facesense.FaceApplication;
import co.aospa.facesense.camera.CameraHandlerThread;
import co.aospa.facesense.camera.CameraHandlerThread.CameraData;
import co.aospa.facesense.camera.CameraUtil;
import co.aospa.facesense.camera.listeners.CallableListener;
import java.lang.ref.WeakReference;

public abstract class CameraCallable<T> {
    private long mBegin;
    protected final WeakReference<CallableListener> mCameraListener;

    public abstract CallableReturn<T> call();

    public abstract String getTag();

    public CameraCallable(CallableListener callableListener) {
        this.mCameraListener = new WeakReference<>(callableListener);
    }

    public CameraData getCameraData() {
        return ((CameraHandlerThread) Thread.currentThread()).getCameraData();
    }

    public Parameters getCameraParameters() {
        return ((CameraHandlerThread) Thread.currentThread()).getCameraData().mParameters;
    }

    public void run() {
        if (CameraUtil.DEBUG) {
            Log.d(getTag(), "Begin");
        }
        this.mBegin = SystemClock.elapsedRealtime();
        final CallableReturn call = call();
        if (CameraUtil.DEBUG) {
            String tag = getTag();
            Log.d(tag, "End (dur:" + (SystemClock.elapsedRealtime() - this.mBegin) + ")");
        }
        runOnUiThread(new Runnable() {
            public void run() {
                CameraCallable.this.callback(call);
            }
        });
    }

    public void callback(CallableReturn<T> callableReturn) {
        long elapsedRealtime = SystemClock.elapsedRealtime() - this.mBegin;
        CallableListener callableListener = (CallableListener) this.mCameraListener.get();
        if (callableReturn.exception != null) {
            String tag = getTag();
            Log.w(tag, "Exception in result (dur:" + elapsedRealtime + ")", callableReturn.exception);
            if (callableListener != null) {
                callableListener.onError(callableReturn.exception);
            }
            return;
        }
        String tag2 = getTag();
        Log.d(tag2, "Result success (dur:" + elapsedRealtime + ")");
        if (callableListener != null) {
            callableListener.onComplete(callableReturn.value);
        }
    }

    protected static void runOnUiThread(Runnable runnable) {
        FaceApplication.getApp().postRunnable(runnable);
    }
}
