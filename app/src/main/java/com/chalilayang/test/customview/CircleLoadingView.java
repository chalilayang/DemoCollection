package com.chalilayang.test.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.chalilayang.test.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.lang.reflect.Constructor;

public class CircleLoadingView extends View {
    private static final String TAG = "CircleLoadingView";
    private static final int DEFAULT_WAVE_PROGRESS_VALUE = 50;
    private static final int DEFAULT_CIRCLE_WIDTH = 20;
    private static final int DEFAULT_VALUE_TEXT_SIZE = 30;
    private static final int DEFAULT_WAVE_COLOR = Color.parseColor("#212121");
    private static final Interpolator DEFAULT_ANIMATOR_INTERPOLATOR =
            new AccelerateDecelerateInterpolator();


    private int mCanvasSize;

    // Dynamic Properties.
    private float progressValue = 0;
    private Float circleWidth;

    // Paint to draw circle.
    private Paint mCirclePaint;
    private int mCircleColor;

    // Paint to draw text.
    private Paint mTextPaint;
    private float mTextSize;
    private int mTextColor;

    private Context mContext;
    private Animator mProgressAnimator;
    private Interpolator mInterpolator;

    // Constructor & Init Method.
    public CircleLoadingView(final Context context) {
        this(context, null);
    }

    public CircleLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;

        // Load the styled attributes and set their properties
        TypedArray attributes = context.obtainStyledAttributes(
                attrs,
                R.styleable.CircleLoadingView,
                defStyleAttr,
                0
        );
        circleWidth = attributes.getDimension(
                R.styleable.CircleLoadingView_clv_borderWidth,
                dp2px(DEFAULT_CIRCLE_WIDTH)
        );

        mCircleColor = attributes.getColor(
                R.styleable.CircleLoadingView_clv_borderColor,
                DEFAULT_WAVE_COLOR
        );

        mTextSize = attributes.getDimension(
                R.styleable.CircleLoadingView_clv_valueTextSize
                , sp2px(DEFAULT_VALUE_TEXT_SIZE)
        );

        mTextColor = attributes.getColor(
                R.styleable.CircleLoadingView_clv_valueTextColor,
                DEFAULT_WAVE_COLOR
        );

        progressValue = 0f;
        // Init Progress
        int tmpProgressValue = attributes.getInteger(
                R.styleable.CircleLoadingView_clv_progressValue,
                DEFAULT_WAVE_PROGRESS_VALUE
        );

        mInterpolator = parseInterpolator(getContext(), attributes.getString(
                R.styleable.CircleLoadingView_clv_animator_Interpolator));

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(circleWidth);
        mCirclePaint.setColor(mCircleColor);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(circleWidth);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        attributes.recycle();
        initProgressAnimation(Math.round(tmpProgressValue));
    }

    private Interpolator parseInterpolator(Context context, String classPath) {
        if (TextUtils.isEmpty(classPath)) {
            return DEFAULT_ANIMATOR_INTERPOLATOR;
        }

        String fullName = null;
        if (classPath.startsWith(".")) {
            // Relative to the app package. Prepend the app package name.
            fullName = context.getPackageName() + classPath;
        } else if (classPath.indexOf('.') >= 0) {
            // Fully qualified package name.
            fullName = classPath;
        } else {
            return DEFAULT_ANIMATOR_INTERPOLATOR;
        }
        try {
            final Class<Interpolator> clazz = (Class<Interpolator>) Class.forName(fullName, true,
                    context.getClassLoader());
            final Constructor<Interpolator> c = clazz.getConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not inflate Interpolator subclass " + fullName, e);
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            progressValue += 0.1f;
            if (progressValue > 100) {
                progressValue = 0;
            }
            invalidate();
            postDelayed(this, 50);
        }
    };

    @Override
    public void onDraw(Canvas canvas) {
        mCanvasSize = canvas.getWidth();
        if (canvas.getHeight() < mCanvasSize) {
            mCanvasSize = canvas.getHeight();
        }

        final float mCenterX = mCanvasSize/2;
        final float mCenterY = mCanvasSize/2;
        final float mCircleRadius = (mCanvasSize-circleWidth)/2;
        canvas.save();
        mTextPaint.setStyle(Paint.Style.FILL);
        final String valueStr = String.valueOf(Math.round(progressValue));
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        final float textWidth = mTextPaint.measureText(valueStr);
        final float textCenterVerticalBaselineY = mCanvasSize / 2 - fm.descent + (fm.descent - fm.ascent) / 2;
        final float textCenterX = (mCanvasSize-textWidth) / 2;
        final float textBaselineY = textCenterVerticalBaselineY;
        canvas.drawText(valueStr, textCenterX, textBaselineY, mTextPaint);
        canvas.restore();

        canvas.save();
        mCirclePaint.setStrokeWidth(circleWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        canvas.rotate(-90, mCenterX, mCenterY);
        final float startAngle = 0;
        final float sweepAngle = 360*progressValue*0.01f;
        canvas.drawArc(new RectF(circleWidth/2, circleWidth/2, mCanvasSize-circleWidth/2, mCanvasSize-circleWidth/2), startAngle, sweepAngle, false, mCirclePaint);
        canvas.restore();

        canvas.save();
        mCirclePaint.setStrokeWidth(circleWidth);
        mCirclePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mCenterX, circleWidth/2, circleWidth/2, mCirclePaint);
        double endPosX = mCenterX + mCircleRadius*Math.cos(sweepAngle*Math.PI/180f);
        double endPosY = mCenterY + mCircleRadius*Math.sin(sweepAngle*Math.PI/180f);
        canvas.rotate(-90, mCenterX, mCenterY);
        canvas.drawCircle((float) endPosX, (float) endPosY, circleWidth/2, mCirclePaint);
        canvas.restore();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        int imageSize = (width < height) ? width : height;
        setMeasuredDimension(imageSize, imageSize);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // The parent has not imposed any constraint on the child.
            result = mCanvasSize;
        }
        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number).
            result = mCanvasSize;
        }
        return (result + 2);
    }

    public void setProgress(int progress) {
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(this, "progressValue", progressValue, progress);
        waterLevelAnim.setDuration(1500);
        waterLevelAnim.setInterpolator(mInterpolator);
        AnimatorSet animatorSetProgress = new AnimatorSet();
        animatorSetProgress.play(waterLevelAnim);
        animatorSetProgress.start();
    }

    private void initProgressAnimation(int progress) {
        mProgressAnimator = ObjectAnimator.ofFloat(this, "progressValue", progressValue, progress);
        mProgressAnimator.setDuration(1500);
        mProgressAnimator.setInterpolator(mInterpolator);
        AnimatorSet animatorSetProgress = new AnimatorSet();
        animatorSetProgress.play(mProgressAnimator);
    }

    @Override
    protected void onAttachedToWindow() {
        if (mProgressAnimator != null && !mProgressAnimator.isRunning()) {
            mProgressAnimator.start();
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mProgressAnimator != null && !mProgressAnimator.isRunning()) {
            mProgressAnimator.end();
        }
        super.onDetachedFromWindow();
    }

    public float getProgressValue() {
        return this.progressValue;
    }

    public void setProgressValue(float tmpValue) {
        if (this.progressValue != tmpValue) {
            this.progressValue = tmpValue;
            invalidate();
        }
    }

    /**
     * Paint.setTextSize(float textSize) default unit is px.
     *
     * @param spValue The real size of text
     * @return int - A transplanted sp
     */
    public int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}