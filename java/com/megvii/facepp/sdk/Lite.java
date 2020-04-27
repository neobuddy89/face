package com.megvii.facepp.sdk;

import android.os.Environment;
import android.os.StatFs;
import com.megvii.facepp.sdk.jni.LiteApi;

public class Lite {
    private static Lite sInstance;
    private long handle = 0;
    private UnlockEncryptor mEncryptor;
    private FeatureRestoreHelper mFeatureRestoreHelper = new FeatureRestoreHelper();
    private String mPath;

    public enum MGULKPowerMode {
        MG_UNLOCK_POWER_NONE,
        MG_UNLOCK_POWER_LOW,
        MG_UNLOCK_POWER_HIGH
    }

    public static final Lite getInstance() {
        if (sInstance == null) {
            sInstance = new Lite();
        }
        return sInstance;
    }

    private Lite() {
    }

    public void initHandle(String str, UnlockEncryptor unlockEncryptor) {
        initHandle(str);
        this.mEncryptor = unlockEncryptor;
        this.mFeatureRestoreHelper.setUnlockEncryptor(this.mEncryptor);
    }

    public void initHandle(String str) {
        if (this.handle == 0) {
            this.handle = LiteApi.nativeInitHandle(str);
            this.mPath = str;
        }
    }

    public int initAllWithPath(String str, String str2, String str3) {
        return (int) LiteApi.nativeInitAllWithPath(this.handle, str, str2, str3);
    }

    public void release() {
        LiteApi.nativeRelease(this.handle);
        this.handle = 0;
    }

    public int compare(byte[] bArr, int i, int i2, int i3, boolean z, boolean z2, int[] iArr) {
        if (iArr.length < 20) {
            return 1;
        }
        return LiteApi.nativeCompare(this.handle, bArr, i, i2, i3, z, z2, iArr);
    }

    public int saveFeature(byte[] bArr, int i, int i2, int i3, boolean z, byte[] bArr2, byte[] bArr3, int[] iArr) {
        byte[] bArr4 = bArr3;
        if (((long) new StatFs(Environment.getExternalStorageDirectory().getPath()).getAvailableBlocks()) < 256) {
            return 33;
        }
        if (bArr4.length < 40000 || bArr2.length < 10000) {
            return 1;
        }
        int nativeSaveFeature = LiteApi.nativeSaveFeature(this.handle, bArr, i, i2, i3, z ? 1 : 0, bArr2, bArr3, iArr);
        if (nativeSaveFeature == 0) {
            this.mFeatureRestoreHelper.saveRestoreImage(bArr4, this.mPath, iArr[0]);
        }
        return nativeSaveFeature;
    }

    public int updateFeature(byte[] bArr, int i, int i2, int i3, boolean z, byte[] bArr2, byte[] bArr3, int i4) {
        byte[] bArr4 = bArr3;
        if (bArr4.length < 40000 || bArr2.length < 10000) {
            return 1;
        }
        int nativeUpdateFeature = LiteApi.nativeUpdateFeature(this.handle, bArr, i, i2, i3, z ? 1 : 0, bArr2, bArr3, i4);
        if (nativeUpdateFeature == 0) {
            this.mFeatureRestoreHelper.saveRestoreImage(bArr4, this.mPath, i4);
        }
        return nativeUpdateFeature;
    }

    public int deleteFeature(int i) {
        int nativeDeleteFeature = LiteApi.nativeDeleteFeature(this.handle, i);
        this.mFeatureRestoreHelper.deleteRestoreImage(this.mPath, i);
        return nativeDeleteFeature;
    }

    public int restoreFeature() {
        return this.mFeatureRestoreHelper.restoreAllFeature(this.mPath);
    }

    public int reset() {
        return LiteApi.nativeReset(this.handle);
    }

    public int prepare() {
        MGULKPowerMode.MG_UNLOCK_POWER_HIGH.ordinal();
        return LiteApi.nativePrepare(this.handle);
    }

    public int setDetectArea(int i, int i2, int i3, int i4) {
        return LiteApi.nativeSetDetectArea(this.handle, i, i2, i3, i4);
    }
}
