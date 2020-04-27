package co.aospa.facesense.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class PreferenceHelper {
    private Context mContext;

    public PreferenceHelper(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void saveIntValue(String str, int i) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.putInt(str, i);
        edit.commit();
    }

    public void saveBooleanValue(String str, boolean z) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.putBoolean(str, z);
        edit.commit();
    }

    public void removeSharePreferences(String str) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.remove(str);
        edit.commit();
    }

    public Integer getIntValueByKey(String str) {
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this.mContext).getInt(str, -1));
    }

    public void saveStringValue(String str, String str2) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.putString(str, str2);
        edit.commit();
    }

    public String getStringValueByKey(String str) {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(str, null);
    }

    public void saveByteArrayValue(String str, byte[] bArr) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.putString(str, new String(bArr));
        edit.commit();
    }

    public byte[] getByteArrayValueByKey(String str) {
        String string = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(str, null);
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return string.getBytes();
    }

    public Boolean getBooleanValueByKey(String str) {
        return Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean(str, false));
    }
}
