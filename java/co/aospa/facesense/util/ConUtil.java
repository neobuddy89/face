package co.aospa.facesense.util;

public class ConUtil {
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r3.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0032, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0058, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0059, code lost:
        r3.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x005c, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x005f, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0060, code lost:
        r3.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0063, code lost:
        return null;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getRaw(android.content.Context r3, int r4, java.lang.String r5, java.lang.String r6, boolean r7) throws java.io.IOException {
        /*
            java.io.File r0 = new java.io.File
            r1 = 0
            java.lang.String r2 = "megvii"
            java.io.File r2 = r3.getDir(r2, r1)
            r0.<init>(r2, r5)
            boolean r5 = r0.exists()
            r2 = 0
            if (r5 != 0) goto L_0x001a
            boolean r5 = r0.mkdirs()
            if (r5 != 0) goto L_0x001a
            return r2
        L_0x001a:
            java.io.File r5 = new java.io.File
            r5.<init>(r0, r6)
            if (r7 != 0) goto L_0x0035
            boolean r6 = r5.exists()     // Catch:{ Exception -> 0x002e, all -> 0x002c }
            if (r6 == 0) goto L_0x0035
            java.lang.String r3 = r5.getAbsolutePath()     // Catch:{ Exception -> 0x002e, all -> 0x002c }
            return r3
        L_0x002c:
            r3 = move-exception
            throw r3
        L_0x002e:
            r3 = move-exception
            r3.printStackTrace()     // Catch:{ all -> 0x0033 }
            return r2
        L_0x0033:
            r3 = move-exception
            throw r3
        L_0x0035:
            r6 = 1024(0x400, float:1.435E-42)
            byte[] r6 = new byte[r6]
            java.io.FileOutputStream r7 = new java.io.FileOutputStream
            r7.<init>(r5)
            android.content.res.Resources r3 = r3.getResources()     // Catch:{ Exception -> 0x005f, all -> 0x005d }
            java.io.InputStream r3 = r3.openRawResource(r4)     // Catch:{ Exception -> 0x005f, all -> 0x005d }
        L_0x0046:
            int r4 = r3.read(r6)     // Catch:{ Exception -> 0x0058, all -> 0x0056 }
            r0 = -1
            if (r4 != r0) goto L_0x0052
            java.lang.String r3 = r5.getAbsolutePath()     // Catch:{ Exception -> 0x005f, all -> 0x005d }
            return r3
        L_0x0052:
            r7.write(r6, r1, r4)     // Catch:{ Exception -> 0x0058, all -> 0x0056 }
            goto L_0x0046
        L_0x0056:
            r3 = move-exception
            throw r3     // Catch:{ Exception -> 0x005f, all -> 0x005d }
        L_0x0058:
            r3 = move-exception
            r3.printStackTrace()     // Catch:{ Exception -> 0x005f, all -> 0x005d }
            return r2
        L_0x005d:
            r3 = move-exception
            throw r3
        L_0x005f:
            r3 = move-exception
            r3.printStackTrace()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: co.aospa.facesense.util.ConUtil.getRaw(android.content.Context, int, java.lang.String, java.lang.String, boolean):java.lang.String");
    }
}
