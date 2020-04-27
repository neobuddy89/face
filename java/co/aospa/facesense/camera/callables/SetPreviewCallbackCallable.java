package co.aospa.facesense.camera.callables;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import co.aospa.facesense.camera.listeners.ByteBufferCallbackListener;
import co.aospa.facesense.camera.listeners.CameraListener;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class SetPreviewCallbackCallable extends CameraCallable {
    private final PreviewCallback mPreviewCallback = new PreviewCallback() {
        public void onPreviewFrame(byte[] bArr, Camera camera) {
            ByteBufferCallbackListener byteBufferCallbackListener = (ByteBufferCallbackListener) SetPreviewCallbackCallable.this.mPreviewCallbackListener.get();
            if (byteBufferCallbackListener != null) {
                byteBufferCallbackListener.onEventCallback(0, ByteBuffer.wrap(bArr));
            }
        }
    };
    public final WeakReference<ByteBufferCallbackListener> mPreviewCallbackListener;
    private boolean mWithBuffer;

    public String getTag() {
        return "SetPreviewCallbackCallable";
    }

    public SetPreviewCallbackCallable(ByteBufferCallbackListener byteBufferCallbackListener, boolean z, CameraListener cameraListener) {
        super(cameraListener);
        this.mWithBuffer = z;
        this.mPreviewCallbackListener = new WeakReference<>(byteBufferCallbackListener);
    }

    public CallableReturn<Void> call() {
        Camera camera = getCameraData().mCamera;
        if (camera == null) {
            return new CallableReturn<>(new Exception("Camera isn't opened"));
        }
        if (this.mWithBuffer) {
            camera.setPreviewCallbackWithBuffer(this.mPreviewCallback);
        } else {
            camera.setPreviewCallback(this.mPreviewCallback);
        }
        return new CallableReturn<>((Object) null);
    }
}
