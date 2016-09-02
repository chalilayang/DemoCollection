package com.chalilayang.test.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewAnimator;

import com.chalilayang.test.R;

/**
 * Created by chalilayang on 2016/8/26.
 */
public class CustomViewAnimator extends ViewAnimator {
    private volatile boolean mRunning = false;
    public static final int DURATION = 800;
    private Context mContext;
    private int mWidth;
    private int mHeight;
    public CustomViewAnimator(Context context) {
        super(context);
        mContext = context;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public CustomViewAnimator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(new Runnable() {
            @Override
            public void run() {
                mWidth = getMeasuredWidth();
                mHeight = getMeasuredHeight();

                AnimationSet inAniSet = new AnimationSet(true);
                TranslateAnimation inTranslateAni = new TranslateAnimation(-mWidth, 0, mHeight*0.5f, 0);
                inTranslateAni.setDuration(DURATION);
                ScaleAnimation inScaleAni = new ScaleAnimation(0, 1, 0, 1);
                inScaleAni.setDuration(DURATION);
                inAniSet.addAnimation(inScaleAni);
                inAniSet.addAnimation(inTranslateAni);
                setInAnimation(inAniSet);

                AnimationSet outAniSet = new AnimationSet(true);
                TranslateAnimation outTranslateAni = new TranslateAnimation(0, mWidth, 0, mHeight*0.5f);
                outTranslateAni.setDuration(DURATION);
                ScaleAnimation outScaleAni = new ScaleAnimation(1, 0, 1, 0);
                outScaleAni.setDuration(DURATION);
                outAniSet.addAnimation(outScaleAni);
                outAniSet.addAnimation(outTranslateAni);
                setOutAnimation(outAniSet);
            }
        });
        startSwich();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopSwich();
    }

    public synchronized void startSwich() {
        if (!mRunning) {
            post(mSwichRunable);
            mRunning = true;
        }
    }

    private Runnable mSwichRunable = new Runnable() {
        @Override
        public void run() {
            showNext();
            postDelayed(this, 2*DURATION);
        }
    };

    public synchronized void stopSwich() {
        if(mRunning) {
            removeCallbacks(mSwichRunable);
            mRunning = false;
        }

    }

    private void init() {
        ImageView viewOne = new ImageView(mContext);
        viewOne.setImageResource(R.drawable.bg1);
        viewOne.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(viewOne, new FrameLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));

        ImageView viewSecond = new ImageView(mContext);
        viewSecond.setImageResource(R.drawable.bg2);
        viewSecond.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(viewSecond, new FrameLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        ImageView viewThree = new ImageView(mContext);
        viewThree.setImageResource(R.drawable.bg3);
        viewThree.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(viewThree, new FrameLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
