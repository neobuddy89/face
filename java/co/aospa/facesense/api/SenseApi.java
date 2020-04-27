package co.aospa.facesense.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.appcompat.R$styleable;
import co.aospa.facesense.FaceApplication;
import co.aospa.facesense.R;
import co.aospa.facesense.util.ConUtil;
import co.aospa.facesense.util.PreferenceHelper;
import co.aospa.facesense.util.SenseDataEncryptor;
import co.aospa.facesense.util.Util;
import com.megvii.facepp.sdk.Lite;
import java.io.File;
import java.io.IOException;

public class SenseApi {
    private static String TAG = "SenseApi";
    private Context mContext;
    private SERVICE_STATE mCurrentState = SERVICE_STATE.INITING;
    private PreferenceHelper mPrefHelper;

    private enum SERVICE_STATE {
        INITING,
        IDLE,
        ENROLLING,
        UNLOCKING,
        ERROR
    }

    public static int convertErrorCode(int i) {
        switch (i) {
            case 3:
                return R.string.unlock_failed;
            case 4:
                return R.string.unlock_failed_quality;
            case 5:
                return R.string.unlock_failed_face_not_found;
            case 6:
                return R.string.unlock_failed_face_small;
            case 7:
                return R.string.unlock_failed_face_large;
            case 8:
                return R.string.unlock_failed_offset_left;
            case 9:
                return R.string.unlock_failed_offset_top;
            case 10:
                return R.string.unlock_failed_offset_right;
            case 11:
                return R.string.unlock_failed_offset_bottom;
            case 13:
                return R.string.unlock_failed_warning;
            case 15:
                return R.string.txt_camera_success_left;
            case 16:
                return R.string.txt_camera_success_top;
            case 17:
                return R.string.txt_camera_success_right;
            case 18:
                return R.string.txt_camera_success_down;
            case 20:
                return R.string.attr_blur;
            case 21:
                return R.string.attr_eye_occlusion;
            case 22:
                return R.string.attr_eye_close;
            case R$styleable.Toolbar_titleMarginBottom /*23*/:
                return R.string.attr_mouth_occlusion;
            case 27:
                return R.string.unlock_failed_face_multi;
            case 28:
                return R.string.unlock_failed_face_blur;
            case R$styleable.Toolbar_titleTextColor /*29*/:
                return R.string.unlock_failed_face_not_complete;
            case 30:
                return R.string.attr_light_dark;
            case 31:
                return R.string.attr_light_high;
            case 32:
                return R.string.attr_light_shadow;
            default:
                return 0;
        }
    }

    public SenseApi(Context context) {
        this.mContext = context;
        this.mPrefHelper = new PreferenceHelper(this.mContext);
    }

    public boolean init() {
        String str;
        synchronized (this) {
            if (this.mCurrentState != SERVICE_STATE.INITING) {
                Log.d(TAG, " Has been init, ignore");
                return false;
            }
            if (Util.DEBUG_INFO) {
                Log.i(TAG, "init start");
            }
            boolean z = !"2.0.72.544".equals(this.mPrefHelper.getStringValueByKey("sdk_version"));
            File dir = this.mContext.getDir("megvii", 0);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String str2 = null;
            try {
                str = ConUtil.getRaw(this.mContext, R.raw.megvii_model_file, "model", "megvii_model_file", z);
            } catch (IOException unused) {
                str = null;
            }
            if (str == null) {
                Log.e(TAG, "Unavalibale memory, init failed, stop self");
                return false;
            }
            try {
                str2 = ConUtil.getRaw(this.mContext, R.raw.panorama_mgb, "model", "panorama_mgb", z);
            } catch (IOException unused2) {
            }
            if (str2 == null) {
                Log.e(TAG, "Unavalibale memory, init failed, stop self");
                return false;
            }
            Lite.getInstance().initHandle(dir.getAbsolutePath(), new SenseDataEncryptor());
            long initAllWithPath = (long) Lite.getInstance().initAllWithPath(str2, "", str);
            if (Util.DEBUG_INFO) {
                Log.i(TAG, "init stop");
            }
            if (initAllWithPath != 0) {
                Log.e(TAG, "init failed, stop self");
                return false;
            }
            if (z) {
                restoreFeature();
                this.mPrefHelper.saveStringValue("sdk_version", "2.0.72.544");
            }
            this.mCurrentState = SERVICE_STATE.IDLE;
            return true;
        }
    }

    public void restoreFeature() {
        if (Util.DEBUG_INFO) {
            Log.i(TAG, "RestoreFeature");
        }
        synchronized (this) {
            Lite.getInstance().prepare();
            Lite.getInstance().restoreFeature();
            Lite.getInstance().reset();
        }
    }

    public boolean compareStart() {
        synchronized (this) {
            if (this.mCurrentState == SERVICE_STATE.INITING) {
                init();
            }
            if (this.mCurrentState == SERVICE_STATE.UNLOCKING) {
                return true;
            }
            if (this.mCurrentState != SERVICE_STATE.IDLE) {
                String str = TAG;
                Log.e(str, "unlock start failed: current state: " + this.mCurrentState);
                return false;
            }
            if (Util.DEBUG_INFO) {
                Log.i(TAG, "compareStart");
            }
            Lite.getInstance().prepare();
            this.mCurrentState = SERVICE_STATE.UNLOCKING;
            return true;
        }
    }

    public int compare(byte[] bArr, int i, int i2, int i3, boolean z, boolean z2, int[] iArr) {
        synchronized (this) {
            if (this.mCurrentState != SERVICE_STATE.UNLOCKING) {
                String str = TAG;
                Log.e(str, "compare failed: current state: " + this.mCurrentState);
                return -1;
            }
            int compare = Lite.getInstance().compare(bArr, i, i2, i3, z, z2, iArr);
            String str2 = TAG;
            Log.d(str2, "compare finish: " + compare);
            return compare;
        }
    }

    public void compareStop() {
        synchronized (this) {
            if (this.mCurrentState != SERVICE_STATE.UNLOCKING) {
                String str = TAG;
                Log.e(str, "compareStop failed: current state: " + this.mCurrentState);
            } else {
                if (Util.DEBUG_INFO) {
                    Log.i(TAG, "compareStop");
                }
                Lite.getInstance().reset();
                this.mCurrentState = SERVICE_STATE.IDLE;
            }
        }
    }

    public boolean saveFeatureStart() {
        synchronized (this) {
            if (this.mCurrentState == SERVICE_STATE.INITING) {
                init();
            } else if (this.mCurrentState == SERVICE_STATE.UNLOCKING) {
                Log.e(TAG, "save feature, stop unlock");
                compareStop();
            }
            if (this.mCurrentState != SERVICE_STATE.IDLE) {
                String str = TAG;
                Log.e(str, "saveFeatureStart failed: current state: " + this.mCurrentState);
            }
            if (Util.DEBUG_INFO) {
                Log.i(TAG, "saveFeatureStart");
            }
            Lite.getInstance().prepare();
            this.mCurrentState = SERVICE_STATE.ENROLLING;
        }
        return true;
    }

    public int saveFeature(byte[] bArr, int i, int i2, int i3, boolean z, byte[] bArr2, byte[] bArr3, int[] iArr) {
        synchronized (this) {
            if (this.mCurrentState != SERVICE_STATE.ENROLLING) {
                String str = TAG;
                Log.e(str, "save feature failed , current state : " + this.mCurrentState);
                return -1;
            }
            if (Util.DEBUG_INFO) {
                Log.i(TAG, "saveFeature");
            }
            int saveFeature = Lite.getInstance().saveFeature(bArr, i, i2, i3, z, bArr2, bArr3, iArr);
            setFacesEnrolled(true);
            return saveFeature;
        }
    }

    public void saveFeatureStop() {
        synchronized (this) {
            if (this.mCurrentState != SERVICE_STATE.ENROLLING) {
                String str = TAG;
                Log.d(str, "saveFeatureStop failed: current state: " + this.mCurrentState);
            }
            if (Util.DEBUG_INFO) {
                Log.i(TAG, "saveFeatureStop");
            }
            Lite.getInstance().reset();
            this.mCurrentState = SERVICE_STATE.IDLE;
        }
    }

    protected static SharedPreferences getSharedPreferences() {
        return FaceApplication.getApp().getSharedPreferences("ParanoidFaceSenseApi", 0);
    }

    public void setFacesEnrolled(boolean z) {
        getSharedPreferences().edit().putBoolean("key_has_feature", z).apply();
    }

    public int setDetectArea(int i, int i2, int i3, int i4) {
        int detectArea;
        synchronized (this) {
            if (Util.DEBUG_INFO) {
                Log.i(TAG, "setDetectArea start");
            }
            detectArea = Lite.getInstance().setDetectArea(i, i2, i3, i4);
        }
        return detectArea;
    }

    public int deleteFeature(int i) {
        synchronized (this) {
            if (Util.DEBUG_INFO) {
                Log.i(TAG, "deleteFeature start");
            }
            Lite.getInstance().deleteFeature(i);
            if (Util.DEBUG_INFO) {
                Log.i(TAG, "deleteFeature stop");
            }
            setFacesEnrolled(false);
            release();
        }
        return 0;
    }

    public void release() {
        synchronized (this) {
            if (this.mCurrentState != SERVICE_STATE.INITING) {
                if (Util.DEBUG_INFO) {
                    Log.i(TAG, "release start");
                }
                Lite.getInstance().release();
                this.mCurrentState = SERVICE_STATE.INITING;
                if (Util.DEBUG_INFO) {
                    Log.i(TAG, "release stop");
                }
            } else if (Util.DEBUG_INFO) {
                Log.i(TAG, "has been released, ignore");
            }
        }
    }
}
