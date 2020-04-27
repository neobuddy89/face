package co.aospa.facesense.util;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.util.Log;

public class Util {
    public static boolean DEBUG_INFO = true;

    public static int getCustomCameraId(int i) {
        String str = SystemPropertiesProxy.get("ro.face.ext_service.cam_id");
        return (str == null || str.equals("") || str.equals("0")) ? i : Integer.parseInt(str);
    }

    public static boolean isFaceUnlockAvailable(Context context) {
        return !isFaceUnlockDisabledByDPM(context);
    }

    public static boolean isFaceUnlockEnrolled(Context context) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        return preferenceHelper.getIntValueByKey("name").intValue() > 0 && preferenceHelper.getByteArrayValueByKey("token") != null;
    }

    public static boolean isFaceUnlockDisabledByDPM(Context context) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        try {
            if (devicePolicyManager.getPasswordQuality(null) > 32768) {
                return true;
            }
        } catch (SecurityException e) {
            Log.e("Util", "isFaceUnlockDisabledByDPM error:", e);
        }
        if ((devicePolicyManager.getKeyguardDisabledFeatures(null) & 128) != 0) {
            return true;
        }
        return false;
    }
}
