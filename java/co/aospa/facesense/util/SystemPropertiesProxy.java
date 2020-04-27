package co.aospa.facesense.util;

import android.util.Log;
import java.lang.reflect.Method;

public class SystemPropertiesProxy {
    private static Class<?> sClassSystemProperties;
    private static boolean sIsInitialized;
    private static Method sMethodGet;
    private static Method sMethodGetBoolean;

    /* JADX WARNING: Can't wrap try/catch for region: R(3:7|8|9) */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        sMethodGet = null;
        sMethodGetBoolean = null;
        sClassSystemProperties = r2;
        sIsInitialized = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001f, code lost:
        r4 = null;
        r5 = false;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0017 */
    static {
        /*
            r0 = 0
            r1 = 0
            java.lang.String r2 = "android.os.SystemProperties"
            java.lang.Class r2 = java.lang.Class.forName(r2)     // Catch:{ all -> 0x004a }
            r3 = 1
            java.lang.String r4 = "get"
            java.lang.Class[] r5 = new java.lang.Class[r3]     // Catch:{ all -> 0x0017 }
            java.lang.Class<java.lang.String> r6 = java.lang.String.class
            r5[r1] = r6     // Catch:{ all -> 0x0017 }
            java.lang.reflect.Method r4 = r2.getMethod(r4, r5)     // Catch:{ all -> 0x0017 }
            r5 = r3
            goto L_0x0021
        L_0x0017:
            sMethodGet = r0     // Catch:{ all -> 0x004a }
            sMethodGetBoolean = r0     // Catch:{ all -> 0x004a }
            sClassSystemProperties = r2     // Catch:{ all -> 0x004a }
            sIsInitialized = r1     // Catch:{ all -> 0x004a }
            r4 = r0
            r5 = r1
        L_0x0021:
            java.lang.String r6 = "getBoolean"
            r7 = 2
            java.lang.Class[] r7 = new java.lang.Class[r7]     // Catch:{ all -> 0x0034 }
            java.lang.Class<java.lang.String> r8 = java.lang.String.class
            r7[r1] = r8     // Catch:{ all -> 0x0034 }
            java.lang.Class r8 = java.lang.Boolean.TYPE     // Catch:{ all -> 0x0034 }
            r7[r3] = r8     // Catch:{ all -> 0x0034 }
            java.lang.reflect.Method r0 = r2.getMethod(r6, r7)     // Catch:{ all -> 0x0034 }
            r1 = r5
            goto L_0x0046
        L_0x0034:
            r3 = move-exception
            boolean r5 = co.aospa.facesense.util.Util.DEBUG_INFO     // Catch:{ all -> 0x004a }
            if (r5 == 0) goto L_0x003e
            java.lang.String r5 = "SystemPropertiesProxy"
            android.util.Log.w(r5, r3)     // Catch:{ all -> 0x004a }
        L_0x003e:
            sMethodGet = r4     // Catch:{ all -> 0x004a }
            sMethodGetBoolean = r0     // Catch:{ all -> 0x004a }
            sClassSystemProperties = r2     // Catch:{ all -> 0x004a }
            sIsInitialized = r1     // Catch:{ all -> 0x004a }
        L_0x0046:
            r3 = r1
            r1 = r0
            r0 = r4
            goto L_0x0055
        L_0x004a:
            sMethodGet = r0
            sMethodGetBoolean = r0
            sClassSystemProperties = r0
            sIsInitialized = r1
            r2 = r0
            r3 = r1
            r1 = r2
        L_0x0055:
            sMethodGet = r0
            sMethodGetBoolean = r1
            sClassSystemProperties = r2
            sIsInitialized = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: co.aospa.facesense.util.SystemPropertiesProxy.<clinit>():void");
    }

    public static String get(String str) {
        if (!sIsInitialized) {
            return null;
        }
        try {
            return (String) sMethodGet.invoke(sClassSystemProperties, new Object[]{str});
        } catch (Throwable th) {
            if (!Util.DEBUG_INFO) {
                return null;
            }
            Log.w("SystemPropertiesProxy", th);
            return null;
        }
    }
}
