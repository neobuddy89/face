package co.aospa.facesense;

import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.hardware.Camera.Size;
import android.hardware.face.FaceManager;
import android.hardware.face.FaceManager.EnrollmentCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.R$styleable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import co.aospa.facesense.api.SenseApi;
import co.aospa.facesense.camera.CameraEnrollController;
import co.aospa.facesense.camera.CameraEnrollController.CameraCallback;
import co.aospa.facesense.util.PreferenceHelper;
import co.aospa.facesense.util.Util;
import co.aospa.facesense.view.EnrollCameraView;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeResolver;

public class FaceEnrollEnrolling extends FragmentActivity {
    private CameraCallback mCameraCallback = new CameraCallback() {
        public int handleSaveFeature(byte[] bArr, int i, int i2, int i3) {
            return -1;
        }

        public void setDetectArea(Size size) {
        }

        public void handleSaveFeatureResult(int i) {
            final boolean z = false;
            FaceEnrollEnrolling.this.mEnrollMsgRes = 0;
            if (i != 0) {
                if (!(i == 1 || i == 2 || i == 3 || i == 4)) {
                    if (i == 6) {
                        FaceEnrollEnrolling.this.mEnrollMsgRes = SenseApi.convertErrorCode(6);
                    } else if (i != 7) {
                        if (!(i == 12 || i == 14)) {
                            switch (i) {
                                case 18:
                                case 19:
                                    FaceEnrollEnrolling faceEnrollEnrolling = FaceEnrollEnrolling.this;
                                    faceEnrollEnrolling.mProgress = 50.0f;
                                    faceEnrollEnrolling.mIsFaceDetected = true;
                                    break;
                                case 20:
                                case 21:
                                    FaceEnrollEnrolling.this.mEnrollMsgRes = SenseApi.convertErrorCode(21);
                                    break;
                                case 22:
                                    FaceEnrollEnrolling.this.mEnrollMsgRes = SenseApi.convertErrorCode(22);
                                    break;
                                case R$styleable.Toolbar_titleMarginBottom /*23*/:
                                    FaceEnrollEnrolling.this.mEnrollMsgRes = SenseApi.convertErrorCode(23);
                                    break;
                                case R$styleable.Toolbar_titleMarginEnd /*24*/:
                                case 25:
                                    break;
                                default:
                                    switch (i) {
                                        case 27:
                                            FaceEnrollEnrolling.this.mEnrollMsgRes = SenseApi.convertErrorCode(27);
                                            break;
                                        case 28:
                                            FaceEnrollEnrolling.this.mEnrollMsgRes = SenseApi.convertErrorCode(28);
                                            break;
                                        case R$styleable.Toolbar_titleTextColor /*29*/:
                                            FaceEnrollEnrolling.this.mEnrollMsgRes = SenseApi.convertErrorCode(29);
                                            break;
                                        case 30:
                                            FaceEnrollEnrolling.this.mEnrollMsgRes = SenseApi.convertErrorCode(30);
                                            break;
                                        case 31:
                                            FaceEnrollEnrolling.this.mEnrollMsgRes = SenseApi.convertErrorCode(31);
                                            break;
                                        case 32:
                                            FaceEnrollEnrolling faceEnrollEnrolling2 = FaceEnrollEnrolling.this;
                                            faceEnrollEnrolling2.mProgress = 30.0f;
                                            faceEnrollEnrolling2.mEnrollMsgRes = SenseApi.convertErrorCode(32);
                                            break;
                                    }
                            }
                        }
                    } else {
                        FaceEnrollEnrolling.this.mEnrollMsgRes = SenseApi.convertErrorCode(7);
                    }
                }
                FaceEnrollEnrolling.this.mEnrollMsgRes = SenseApi.convertErrorCode(4);
            } else {
                FaceEnrollEnrolling.this.mProgress = 100.0f;
            }
            if (FaceEnrollEnrolling.this.mEnrollMsgRes != 0 && FaceEnrollEnrolling.this.mProgress < 100.0f) {
                z = true;
            }
            FaceEnrollEnrolling.this.runOnUiThread(new Runnable() {
                public void run() {
                    FaceEnrollEnrolling faceEnrollEnrolling = FaceEnrollEnrolling.this;
                    if (faceEnrollEnrolling.mIsFaceDetected) {
                        faceEnrollEnrolling.mCameraView.setProgress(faceEnrollEnrolling.mProgress);
                    }
                    FaceEnrollEnrolling.this.mEnrollMsg.setVisibility(z ? 0 : 4);
                    if (FaceEnrollEnrolling.this.mEnrollMsgRes != 0) {
                        FaceEnrollEnrolling faceEnrollEnrolling2 = FaceEnrollEnrolling.this;
                        faceEnrollEnrolling2.mEnrollMsg.setText(faceEnrollEnrolling2.mEnrollMsgRes);
                    }
                }
            });
        }

        public void onCameraError() {
            if (!FaceEnrollEnrolling.this.isFinishing() && !FaceEnrollEnrolling.this.isDestroyed()) {
                if (!FaceEnrollEnrolling.this.mIsActivityPaused) {
                    Intent intent = new Intent();
                    intent.setClass(FaceEnrollEnrolling.this, FaceEnrollTryAgain.class);
                    FaceEnrollEnrolling.this.parseIntent(intent);
                    FaceEnrollEnrolling.this.startActivity(intent);
                }
                FaceEnrollEnrolling.this.finish();
            }
        }

        public void onFaceDetected() {
            FaceEnrollEnrolling.this.mIsFaceDetected = true;
        }
    };
    private CameraEnrollController mCameraEnrollController;
    public EnrollCameraView mCameraView;
    public TextView mEnrollMsg;
    /* access modifiers changed from: private */
    public int mEnrollMsgRes = 0;
    private EnrollmentCallback mEnrollmentCallback = new EnrollmentCallback() {
        public void onEnrollmentProgress(int i) {
            if (i == 0) {
                FaceEnrollEnrolling.this.runOnUiThread(new Runnable() {
                    public void run() {
                        FaceEnrollEnrolling faceEnrollEnrolling = FaceEnrollEnrolling.this;
                        faceEnrollEnrolling.mProgress = 100.0f;
                        faceEnrollEnrolling.mCameraView.setProgress(faceEnrollEnrolling.mProgress);
                        try {
                            if (FaceEnrollEnrolling.this.mEnrollMsg != null) {
                                FaceEnrollEnrolling.this.mEnrollMsg.setText("");
                            }
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    if (!FaceEnrollEnrolling.this.isDestroyed()) {
                                        Intent intent = new Intent();
                                        byte[] bArr = FaceEnrollEnrolling.this.mToken;
                                        if (bArr != null) {
                                            intent.putExtra("hw_auth_token", bArr);
                                        }
                                        int i = FaceEnrollEnrolling.this.mUserId;
                                        if (i != -10000) {
                                            intent.putExtra("android.intent.extra.USER_ID", i);
                                        }
                                        intent.setComponent(ComponentName.unflattenFromString("com.android.settings/com.android.settings.biometrics.face.FaceEnrollFinish"));
                                        FaceEnrollEnrolling.this.startActivityForResult(intent, 1);
                                    }
                                }
                            }, 2000);
                        } catch (Exception unused) {
                        }
                    }
                });
            }
        }

        public void onEnrollmentHelp(int i, final CharSequence charSequence) {
            FaceEnrollEnrolling.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (!TextUtils.isEmpty(charSequence)) {
                        FaceEnrollEnrolling.this.mEnrollMsg.setText(charSequence);
                    }
                }
            });
        }

        public void onEnrollmentError(int i, CharSequence charSequence) {
            if (!FaceEnrollEnrolling.this.mIsActivityPaused) {
                Intent intent = new Intent();
                intent.setClass(FaceEnrollEnrolling.this, FaceEnrollTryAgain.class);
                FaceEnrollEnrolling.this.parseIntent(intent);
                FaceEnrollEnrolling.this.startActivity(intent);
            }
            FaceEnrollEnrolling.this.finish();
        }
    };
    protected CancellationSignal mEnrollmentCancel = new CancellationSignal();
    private FaceManager mFM;
    private SurfaceHolder mHolder;
    public boolean mIsActivityPaused = false;
    public boolean mIsFaceDetected = false;
    protected boolean mLaunchedConfirmLock;
    protected PreferenceHelper mPrefHelper;
    public float mProgress = 0.0f;
    protected byte[] mToken;
    protected int mUserId;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ThemeResolver.getDefault().applyTheme(this);
        this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
        if (bundle != null && this.mToken == null) {
            this.mLaunchedConfirmLock = bundle.getBoolean("launched_confirm_lock");
            this.mToken = bundle.getByteArray("hw_auth_token");
        }
        this.mUserId = getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
        initData();
        this.mFM = (FaceManager) getSystemService("face");
        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
            boolean shouldShowRequestPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.CAMERA");
            if (Util.DEBUG_INFO) {
                Log.i("FaceEnrollEnrolling", "shouldShowRequestPermissionRationale: " + shouldShowRequestPermissionRationale);
            }
            requestPermissions(new String[]{"android.permission.CAMERA"}, 0);
        }
    }

    public void onApplyThemeResource(Theme theme, int i, boolean z) {
        theme.applyStyle(R.style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, i, z);
    }

    private void initData() {
        this.mPrefHelper = new PreferenceHelper(this);
    }

    private void init() {
        FaceManager faceManager = (FaceManager) getSystemService("face");
        int i = this.mUserId;
        if (i != -10000) {
            faceManager.setActiveUser(i);
        }
        setContentView(R.layout.face_enroll_enrolling);
        setHeaderText(R.string.face_enrolling_title);
        this.mCameraView = (EnrollCameraView) findViewById(R.id.camera_surface);
        this.mProgress = 0.0f;
        this.mCameraView.setProgress(this.mProgress);
        this.mHolder = this.mCameraView.getHolder();
        if (this.mCameraEnrollController == null) {
            this.mCameraEnrollController = CameraEnrollController.getInstance();
            this.mCameraEnrollController.setSurfaceHolder(this.mHolder);
        }
        this.mFM.enroll(0, this.mToken, this.mEnrollmentCancel, this.mEnrollmentCallback, new int[]{1});
        this.mCameraEnrollController.start(this.mCameraCallback, 15000);
        this.mEnrollMsg = (TextView) findViewById(R.id.face_msg);
        ((ViewGroup) findViewById(R.id.face_description)).getLayoutTransition().enableTransitionType(4);
    }

    private GlifLayout getLayout() {
        return (GlifLayout) findViewById(R.id.setup_wizard_layout);
    }

    private void setHeaderText(int i) {
        CharSequence text = getLayout().getHeaderTextView().getText();
        CharSequence text2 = getText(i);
        if (text != text2) {
            getLayout().setHeaderText(text2);
            setTitle(text2);
        }
    }

    public void onPause() {
        super.onPause();
        this.mIsActivityPaused = true;
        CameraEnrollController cameraEnrollController = this.mCameraEnrollController;
        if (cameraEnrollController != null) {
            cameraEnrollController.setSurfaceHolder(null);
            this.mCameraEnrollController.stop(this.mCameraCallback);
            this.mCameraEnrollController = null;
        }
        this.mEnrollmentCancel.cancel();
    }

    public void onResume() {
        super.onResume();
        this.mIsActivityPaused = false;
        init();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("launched_confirm_lock", this.mLaunchedConfirmLock);
        bundle.putByteArray("hw_auth_token", this.mToken);
    }

    /* access modifiers changed from: private */
    public void parseIntent(Intent intent) {
        intent.putExtra("hw_auth_token", this.mToken);
    }

    private void showPermissionRequiredDialog() {
        new Builder(this, 5).setTitle(R.string.perm_required_alert_title).setMessage(R.string.perm_required_alert_msg).setPositiveButton(R.string.perm_required_alert_button_app_info, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                FaceEnrollEnrolling.this.jumpToAppInfo();
            }
        }).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialogInterface) {
                FaceEnrollEnrolling.this.finish();
            }
        }).show();
    }

    public void jumpToAppInfo() {
        startActivityForResult(new Intent().setAction("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", getPackageName(), null)), 2);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1) {
            setResult(i2);
            finish();
        } else if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
            if (Util.DEBUG_INFO) {
                Log.i("FaceEnrollEnrolling", "REQUEST_CAMERA finish");
            }
            finish();
        } else if (Util.DEBUG_INFO) {
            Log.i("FaceEnrollEnrolling", "REQUEST_CAMERA init");
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.CAMERA")) {
            showPermissionRequiredDialog();
        } else {
            finish();
        }
    }
}
