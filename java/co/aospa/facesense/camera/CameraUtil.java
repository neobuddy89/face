package co.aospa.facesense.camera;

import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtil {
    public static final boolean DEBUG = (!USER_BUILD);
    public static final boolean ENG_BUILD = "eng".equals(Build.TYPE);
    public static final boolean USERDEBUG_BUILD = "userdebug".equals(Build.TYPE);
    public static final boolean USER_BUILD = (!ENG_BUILD && !USERDEBUG_BUILD);

    public static Size calBestPreviewSize(Parameters parameters, final int i, final int i2) {
        List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        ArrayList arrayList = new ArrayList();
        for (Size next : supportedPreviewSizes) {
            if (next.width > next.height) {
                arrayList.add(next);
            }
        }
        Collections.sort(arrayList, new Comparator<Size>() {
            public int compare(Size size, Size size2) {
                return Math.abs((size.width * size.height) - (i * i2)) - Math.abs((size2.width * size2.height) - (i * i2));
            }
        });
        return (Size) arrayList.get(0);
    }
}
