package co.aospa.facesense.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.animation.AccelerateDecelerateInterpolator;
import co.aospa.facesense.R;

public class EnrollCameraView extends SurfaceView {
    /* access modifiers changed from: private */
    public float mProgress = 0.0f;
    /* access modifiers changed from: private */
    public ValueAnimator mProgressAnimator;

    public EnrollCameraView(Context context) {
        super(context);
    }

    public EnrollCameraView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public EnrollCameraView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setProgress(float f) {
        ValueAnimator valueAnimator = this.mProgressAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mProgressAnimator = null;
        }
        this.mProgressAnimator = ValueAnimator.ofFloat(new float[]{this.mProgress, f});
        this.mProgressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mProgressAnimator.setDuration((long) Math.abs(((f - this.mProgress) / 100.0f) * 1000.0f));
        this.mProgressAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                EnrollCameraView.this.mProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                EnrollCameraView.this.invalidate();
            }
        });
        this.mProgressAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                EnrollCameraView.this.mProgressAnimator = null;
            }
        });
        this.mProgressAnimator.start();
    }

    public void draw(Canvas canvas) {
        float measuredWidth = (float) (getMeasuredWidth() / 2);
        float measuredHeight = (float) (getMeasuredHeight() / 2);
        float min = Math.min(measuredWidth, measuredHeight);
        RectF rectF = new RectF(measuredWidth - min, measuredHeight - min, measuredWidth + min, measuredHeight + min);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.enroll_progress_bar));
        Paint paint2 = paint;
        canvas.drawArc(rectF, 270.0f, 360.0f, true, paint2);
        paint.setColor(getContext().getColor(R.color.circle_progress_color));
        canvas.drawArc(rectF, 270.0f, this.mProgress * 3.6f, true, paint2);
        Path path = new Path();
        path.addCircle(measuredWidth, measuredHeight, min * 0.95f, Direction.CCW);
        canvas.clipPath(path);
        super.draw(canvas);
        invalidate();
    }
}
