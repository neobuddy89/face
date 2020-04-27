package co.aospa.facesense;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import co.aospa.facesense.util.PreferenceHelper;
import co.aospa.facesense.util.Util;

public class FaceApplication extends Application {
    public static FaceApplication mApp;
    private Handler mHandler = new Handler();
    private BroadcastReceiver mShutdownReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Util.DEBUG_INFO) {
                Log.d("ParanoidFaceApplication", "mShutdownReceiver Received intent with action = " + action);
            }
            intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN");
        }
    };

    public void onCreate() {
        if (Util.DEBUG_INFO) {
            Log.d("ParanoidFaceApplication", "onCreate");
        }
        super.onCreate();
        mApp = this;
        registerReceiver(this.mShutdownReceiver, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"));
        PreferenceHelper preferenceHelper = new PreferenceHelper(getApplicationContext());
        if (!preferenceHelper.getBooleanValueByKey("name_plus_one").booleanValue()) {
            int intValue = preferenceHelper.getIntValueByKey("name").intValue();
            byte[] byteArrayValueByKey = preferenceHelper.getByteArrayValueByKey("token");
            if (intValue >= 0 && byteArrayValueByKey == null) {
                preferenceHelper.saveIntValue("name", intValue + 1);
            }
            preferenceHelper.saveBooleanValue("name_plus_one", true);
        }
        boolean hasRequirements = hasRequirements();
        getPackageManager().setComponentEnabledSetting(new ComponentName(this, FaceEnrollEnrolling.class), hasRequirements ? 1 : 2, 1);
        Log.d("ParanoidFaceApplication", "Paranoid Android detected and service enabled: " + hasRequirements);
    }

    private boolean hasRequirements() {
        boolean z = SystemProperties.getBoolean("ro.face.sense_service", false);
        String str = SystemProperties.get("ro.pa.version", "");
        if (!z || TextUtils.isEmpty(str)) {
            return false;
        }
        return true;
    }

    public void onTerminate() {
        if (Util.DEBUG_INFO) {
            Log.d("ParanoidFaceApplication", "onTerminate");
        }
        super.onTerminate();
        unregisterReceiver(this.mShutdownReceiver);
    }

    public static FaceApplication getApp() {
        return mApp;
    }

    public void postRunnable(Runnable runnable) {
        this.mHandler.post(runnable);
    }
}
