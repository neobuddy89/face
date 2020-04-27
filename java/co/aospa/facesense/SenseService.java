package co.aospa.facesense;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import co.aospa.facesense.api.SenseApi;
import co.aospa.facesense.camera.CameraAuthController;
import co.aospa.facesense.camera.CameraAuthController.ServiceCallback;
import co.aospa.facesense.camera.CameraEnrollController;
import co.aospa.facesense.camera.CameraEnrollController.CameraCallback;
import co.aospa.facesense.util.PreferenceHelper;
import co.aospa.facesense.util.Util;
import java.util.Random;
import vendor.pa.biometrics.face.V1_0.ISenseService.Stub;
import vendor.pa.biometrics.face.V1_0.ISenseServiceReceiver;

public class SenseService extends Service {
    public String ALARM_TIMEOUT_FACE_FREEZED = "co.aospa.facesense.freezedtimeout";
    private long DEFAULT_TRUSTAGENT_TIMEOUT_MS = 14400000;
    private AlarmManager mAlarmManager;
    public CameraAuthController mCameraAuthController;
    public ServiceCallback mCameraAuthControllerCallback = new ServiceCallback() {
        public int handlePreviewData(byte[] bArr, int i, int i2) {
            int[] iArr = new int[20];
            if (Util.DEBUG_INFO) {
                Log.d("SenseService", "handleData start");
            }
            int compare = SenseService.this.mSenseApi.compare(bArr, i, i2, 0, true, true, iArr);
            if (Util.DEBUG_INFO) {
                Log.d("SenseService", "handleData result = " + compare + " run: fake = " + iArr[0] + ", low = " + iArr[1] + ", data score:" + iArr[2] + " live score:" + (((double) iArr[3]) / 100.0d));
            }
            try {
                synchronized (SenseService.this) {
                    if (SenseService.this.mCameraAuthController == null) {
                        return -1;
                    }
                    if (compare == 0) {
                        SenseService.this.mSenseReceiver.onAuthenticated(SenseService.this.mPrefHelper.getIntValueByKey("name").intValue(), SenseService.this.mUserId, SenseService.this.mPrefHelper.getByteArrayValueByKey("token"));
                        SenseService.this.stopAuthrate();
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return compare;
        }

        public void setDetectArea(Size size) {
            SenseService.this.mSenseApi.setDetectArea(0, 0, size.height, size.width);
        }

        public void onTimeout() {
            try {
                SenseService.this.mSenseReceiver.onAuthenticated(0, -1, SenseService.this.mPrefHelper.getByteArrayValueByKey("token"));
                SenseService.this.stopAuthrate();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            SenseService.this.stopAuthrate();
        }

        public void onCameraError() {
            try {
                SenseService.this.mSenseReceiver.onError(5, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            SenseService.this.stopAuthrate();
        }
    };
    public CameraEnrollController mCameraEnrollController;
    public CameraCallback mCameraEnrollServiceCallback = new CameraCallback() {
        byte[] mImage = new byte[40000];
        byte[] mSavedFeature = new byte[10000];

        public void handleSaveFeatureResult(int i) {
        }

        public void onFaceDetected() {
        }

        public int handleSaveFeature(byte[] bArr, int i, int i2, int i3) {
            int[] iArr = new int[1];
            int saveFeature = SenseService.this.mSenseApi.saveFeature(bArr, i, i2, i3, true, this.mSavedFeature, this.mImage, iArr);
            synchronized (SenseService.this) {
                if (SenseService.this.mCameraEnrollController == null) {
                    return -1;
                }
                try {
                    int i4 = iArr[0] + 1;
                    if (saveFeature == 0) {
                        int intValue = SenseService.this.mPrefHelper.getIntValueByKey("name").intValue();
                        if (intValue > 0) {
                            SenseService.this.mSenseApi.deleteFeature(intValue);
                        }
                        if (i4 > 0) {
                            SenseService.this.mPrefHelper.saveIntValue("name", i4);
                            SenseService.this.mPrefHelper.saveByteArrayValue("token", SenseService.this.mEnrollToken);
                        }
                        SenseService.this.stopEnroll();
                        SenseService.this.mSenseReceiver.onEnrollResult(i4, SenseService.this.mUserId, 0);
                    } else if (saveFeature == 14) {
                        SenseService.this.stopEnroll();
                        SenseService.this.mSenseReceiver.onError(5, 0);
                    } else if (saveFeature == 19) {
                        SenseService.this.mSenseReceiver.onEnrollResult(i4, SenseService.this.mUserId, 1);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return saveFeature;
        }

        public void setDetectArea(Size size) {
            SenseService.this.mSenseApi.setDetectArea(0, 0, size.height, size.width);
        }

        public void onCameraError() {
            try {
                SenseService.this.stopEnroll();
                SenseService.this.mSenseReceiver.onError(5, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
    public long mChallenge = 0;
    public int mChallengeCount = 0;
    public byte[] mEnrollToken;
    public boolean mOnTimer = false;
    public PreferenceHelper mPrefHelper;
    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Util.DEBUG_INFO) {
                Log.d("SenseService", "OnReceive intent = " + intent);
            }
            if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                SenseService senseService = SenseService.this;
                if (!senseService.mOnTimer) {
                    senseService.startTimer();
                }
            } else if (intent.getAction().equals("android.intent.action.USER_PRESENT")) {
                SenseService.this.cancelTimer();
            }
        }
    };
    public SenseApi mSenseApi;
    public ISenseServiceReceiver mSenseReceiver;
    private SenseServiceWrapper mService;
    private PendingIntent mTATimeoutIntent;
    private final BroadcastReceiver mTimeoutBroadcastReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SenseService.this.ALARM_TIMEOUT_FACE_FREEZED)) {
                Log.d("SenseService", "ALARM_TIMEOUT_FACE_FREEZED");
                try {
                    SenseService.this.mSenseReceiver.onLockoutChanged(Long.MAX_VALUE);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    public int mUserId;
    public FaceHandler mWorkHandler;

    private class FaceHandler extends Handler {
        public FaceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            if (message.what == 100) {
                SenseService senseService = SenseService.this;
                senseService.mChallenge = 0;
                senseService.mChallengeCount = 0;
                senseService.stopCurrentWork();
            }
        }
    }

    private final class SenseServiceWrapper extends Stub {
        private SenseServiceWrapper() {
        }

        public void setCallback(ISenseServiceReceiver iSenseServiceReceiver) throws RemoteException {
            SenseService.this.mSenseReceiver = iSenseServiceReceiver;
        }

        public void enroll(byte[] bArr, int i, int[] iArr) throws RemoteException {
            if (Util.DEBUG_INFO) {
                Log.d("SenseService", "enroll");
            }
            boolean z = true;
            if (Util.isFaceUnlockAvailable(SenseService.this)) {
                SenseService senseService = SenseService.this;
                if (!(senseService.mChallenge == 0 || bArr == null)) {
                    senseService.mEnrollToken = bArr;
                    int intValue = senseService.mPrefHelper.getIntValueByKey("name").intValue();
                    if (intValue > 0) {
                        SenseService.this.mSenseApi.deleteFeature(intValue - 1);
                        SenseService.this.mPrefHelper.removeSharePreferences("name");
                        SenseService.this.mPrefHelper.removeSharePreferences("token");
                    }
                    SenseService.this.mWorkHandler.post(new Runnable() {
                        public void run() {
                            SenseService.this.mSenseApi.saveFeatureStart();
                            synchronized (SenseService.this) {
                                if (SenseService.this.mCameraEnrollController == null) {
                                    SenseService.this.mCameraEnrollController = CameraEnrollController.getInstance();
                                }
                                SenseService.this.mCameraEnrollController.start(SenseService.this.mCameraEnrollServiceCallback, 15000);
                            }
                        }
                    });
                    return;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("enroll error ! hasChallenge = ");
            sb.append(SenseService.this.mChallenge != 0);
            sb.append(" hasCryptoToken = ");
            if (bArr == null) {
                z = false;
            }
            sb.append(z);
            Log.e("SenseService", sb.toString());
            try {
                SenseService.this.mSenseReceiver.onError(5, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void cancel() throws RemoteException {
            if (Util.DEBUG_INFO) {
                Log.d("SenseService", "cancel");
            }
            SenseService.this.mWorkHandler.post(new Runnable() {
                public void run() {
                    SenseService senseService = SenseService.this;
                    if (senseService.mCameraAuthController != null) {
                        senseService.stopAuthrate();
                    }
                    SenseService senseService2 = SenseService.this;
                    if (senseService2.mCameraEnrollController != null) {
                        senseService2.stopEnroll();
                    }
                    try {
                        SenseService.this.mSenseReceiver.onError(5, 0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public void authenticate(long j) throws RemoteException {
            if (Util.DEBUG_INFO) {
                Log.d("SenseService", "authenticate");
            }
            if (!Util.isFaceUnlockAvailable(SenseService.this)) {
                try {
                    SenseService.this.mSenseReceiver.onError(5, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                SenseService.this.mWorkHandler.post(new Runnable() {
                    public void run() {
                        SenseService.this.mSenseApi.compareStart();
                        synchronized (SenseService.this) {
                            if (SenseService.this.mCameraAuthController == null) {
                                SenseService.this.mCameraAuthController = new CameraAuthController(SenseService.this, SenseService.this.mCameraAuthControllerCallback);
                            } else {
                                SenseService.this.mCameraAuthController.stop();
                            }
                            SenseService.this.mCameraAuthController.start();
                        }
                    }
                });
            }
        }

        public void remove(final int i) throws RemoteException {
            if (Util.DEBUG_INFO) {
                Log.d("SenseService", "remove");
            }
            SenseService.this.mWorkHandler.post(new Runnable() {
                public void run() {
                    int intValue = SenseService.this.mPrefHelper.getIntValueByKey("name").intValue();
                    if (intValue != 0) {
                        Log.e("SenseService", "Remove unsaved feature! " + i);
                    }
                    byte[] byteArrayValueByKey = SenseService.this.mPrefHelper.getByteArrayValueByKey("token");
                    if (intValue <= 0 || byteArrayValueByKey != null) {
                        SenseService.this.mSenseApi.deleteFeature(intValue - 1);
                        SenseService.this.mPrefHelper.removeSharePreferences("name");
                        SenseService.this.mPrefHelper.removeSharePreferences("token");
                    } else {
                        Log.d("SenseService", "upgrade from p to Q, save data");
                    }
                    try {
                        SenseService.this.mSenseReceiver.onRemoved(new int[]{i}, SenseService.this.mUserId);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public int enumerate() throws RemoteException {
            if (Util.DEBUG_INFO) {
                Log.d("SenseService", "enumerate");
            }
            int intValue = SenseService.this.mPrefHelper.getIntValueByKey("name").intValue();
            final int[] iArr = intValue > -1 ? new int[]{intValue} : new int[0];
            SenseService.this.mWorkHandler.post(new Runnable() {
                public void run() {
                    try {
                        if (SenseService.this.mSenseReceiver != null) {
                            SenseService.this.mSenseReceiver.onEnumerate(iArr, SenseService.this.mUserId);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            return 0;
        }

        public long generateChallenge(int i) throws RemoteException {
            if (Util.DEBUG_INFO) {
                Log.d("SenseService", "generateChallenge + " + i);
            }
            SenseService senseService = SenseService.this;
            if (senseService.mChallengeCount <= 0 || senseService.mChallenge == 0) {
                SenseService.this.mChallenge = new Random().nextLong();
            }
            SenseService senseService2 = SenseService.this;
            senseService2.mChallengeCount++;
            senseService2.mWorkHandler.removeMessages(100);
            SenseService.this.mWorkHandler.sendEmptyMessageDelayed(100, (long) (i * 1000));
            return SenseService.this.mChallenge;
        }

        public int revokeChallenge() throws RemoteException {
            if (Util.DEBUG_INFO) {
                Log.d("SenseService", "revokeChallenge");
            }
            SenseService senseService = SenseService.this;
            senseService.mChallengeCount--;
            if (senseService.mChallengeCount <= 0 && senseService.mChallenge != 0) {
                senseService.mChallenge = 0;
                senseService.mChallengeCount = 0;
                senseService.mWorkHandler.removeMessages(100);
                SenseService.this.stopCurrentWork();
            }
            return 0;
        }

        public int getAuthenticatorId() throws RemoteException {
            return SenseService.this.mPrefHelper.getIntValueByKey("name").intValue();
        }

        public void resetLockout(byte[] bArr) throws RemoteException {
            SenseService.this.mSenseReceiver.onLockoutChanged(0);
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return 3;
    }

    public IBinder onBind(Intent intent) {
        if (Util.DEBUG_INFO) {
            Log.i("SenseService", "onBind");
        }
        return this.mService;
    }

    public void onCreate() {
        super.onCreate();
        if (Util.DEBUG_INFO) {
            Log.i("SenseService", "onCreate start");
        }
        this.mService = new SenseServiceWrapper();
        HandlerThread handlerThread = new HandlerThread("SenseService", -2);
        handlerThread.start();
        this.mWorkHandler = new FaceHandler(handlerThread.getLooper());
        this.mPrefHelper = new PreferenceHelper(this);
        this.mSenseApi = new SenseApi(this);
        this.mUserId = getUserId();
        if (Util.isFaceUnlockAvailable(this) && Util.isFaceUnlockEnrolled(this)) {
            this.mWorkHandler.post(new Runnable() {
                public void run() {
                    SenseService.this.mSenseApi.init();
                }
            });
        }
        this.mAlarmManager = (AlarmManager) getSystemService(AlarmManager.class);
        this.mTATimeoutIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(this.ALARM_TIMEOUT_FACE_FREEZED), 0);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(this.ALARM_TIMEOUT_FACE_FREEZED);
        registerReceiver(this.mTimeoutBroadcastReciever, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.SCREEN_OFF");
        intentFilter2.addAction("android.intent.action.USER_PRESENT");
        intentFilter2.setPriority(1000);
        registerReceiver(this.mScreenReceiver, intentFilter2);
        if (Util.DEBUG_INFO) {
            Log.d("SenseService", "OnCreate end");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (Util.DEBUG_INFO) {
            Log.d("SenseService", "onDestroy");
        }
        this.mSenseApi.release();
        unregisterReceiver(this.mTimeoutBroadcastReciever);
        unregisterReceiver(this.mScreenReceiver);
    }

    public void stopEnroll() {
        synchronized (this) {
            if (this.mCameraEnrollController != null) {
                this.mCameraEnrollController.stop(this.mCameraEnrollServiceCallback);
            }
            this.mCameraEnrollController = null;
        }
        this.mEnrollToken = null;
        this.mSenseApi.saveFeatureStop();
    }

    public void stopAuthrate() {
        synchronized (this) {
            if (this.mCameraAuthController != null) {
                this.mCameraAuthController.stop();
            }
            this.mCameraAuthController = null;
        }
        this.mSenseApi.compareStop();
    }

    public void stopCurrentWork() {
        if (this.mCameraAuthController != null) {
            try {
                this.mSenseReceiver.onError(10, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            stopAuthrate();
        }
        if (this.mCameraEnrollController != null) {
            try {
                this.mSenseReceiver.onError(10, 0);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
            stopEnroll();
        }
    }

    public void startTimer() {
        this.mOnTimer = true;
        this.mAlarmManager.set(3, SystemClock.elapsedRealtime() + this.DEFAULT_TRUSTAGENT_TIMEOUT_MS, this.mTATimeoutIntent);
    }

    public void cancelTimer() {
        this.mOnTimer = false;
        this.mAlarmManager.cancel(this.mTATimeoutIntent);
    }
}
