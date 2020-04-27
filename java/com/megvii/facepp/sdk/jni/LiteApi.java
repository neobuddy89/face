package com.megvii.facepp.sdk.jni;

public class LiteApi {
    public static native int nativeCompare(long j, byte[] bArr, int i, int i2, int i3, boolean z, boolean z2, int[] iArr);

    public static native int nativeDeleteFeature(long j, int i);

    public static native long nativeInitAllWithPath(long j, String str, String str2, String str3);

    public static native long nativeInitHandle(String str);

    public static native int nativePrepare(long j);

    public static native long nativeRelease(long j);

    public static native int nativeReset(long j);

    public static native int nativeSaveFeature(long j, byte[] bArr, int i, int i2, int i3, int i4, byte[] bArr2, byte[] bArr3, int[] iArr);

    public static native int nativeSetDetectArea(long j, int i, int i2, int i3, int i4);

    public static native int nativeUpdateFeature(long j, byte[] bArr, int i, int i2, int i3, int i4, byte[] bArr2, byte[] bArr3, int i5);

    static {
        System.loadLibrary("MegviiUnlock-jni-1.2");
    }
}
