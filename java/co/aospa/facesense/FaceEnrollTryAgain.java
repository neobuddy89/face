package co.aospa.facesense;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;

public class FaceEnrollTryAgain extends FragmentActivity {
    private Button mCancel;
    protected boolean mLaunchedConfirmLock;
    protected byte[] mToken;
    private Button mTryAgain;
    protected int mUserId;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
        if (bundle != null && this.mToken == null) {
            this.mLaunchedConfirmLock = bundle.getBoolean("launched_confirm_lock");
            this.mToken = bundle.getByteArray("hw_auth_token");
        }
        this.mUserId = getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
        setContentView(R.layout.face_enroll_try_again);
        this.mTryAgain = (Button) findViewById(R.id.face_try);
        this.mTryAgain.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(FaceEnrollTryAgain.this, FaceEnrollEnrolling.class);
                intent.putExtra("hw_auth_token", FaceEnrollTryAgain.this.mToken);
                FaceEnrollTryAgain.this.startActivity(intent);
                FaceEnrollTryAgain.this.finish();
            }
        });
        this.mCancel = (Button) findViewById(R.id.face_cancel);
        this.mCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FaceEnrollTryAgain.this.finish();
            }
        });
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("launched_confirm_lock", this.mLaunchedConfirmLock);
        bundle.putByteArray("hw_auth_token", this.mToken);
    }

    public void onPause() {
        super.onPause();
        finish();
    }
}
