package co.aospa.facesense.camera.listeners;

public interface CallableListener<T> {
    void onComplete(Object obj);

    void onError(Exception exc);
}
