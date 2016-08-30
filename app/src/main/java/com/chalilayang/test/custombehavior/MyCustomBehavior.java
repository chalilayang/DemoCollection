package com.chalilayang.test.custombehavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.chalilayang.test.R;
import com.nineoldandroids.animation.ArgbEvaluator;

/**
 * Created by chalilayang on 2016/8/23.
 */
public class MyCustomBehavior extends CoordinatorLayout.Behavior<View> {
    private static final String TAG = "MyCustomBehavior";
    private int id;
    private int lastTranslationY = 0;
    private ArgbEvaluator mArgbEvaluator;
    private Context mContext;

    public MyCustomBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getResources().obtainAttributes(attrs, R.styleable.MyCustomStyle);
        id = typedArray.getResourceId(R.styleable.MyCustomStyle_anchor_id, -1);
        typedArray.recycle();
        mArgbEvaluator = new ArgbEvaluator();
        mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

//        return dependency instanceof AppBarLayout;
        return dependency.getId() == id;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

//        if (lastTranslationY == 0) {
//            lastTranslationY = dependency.getTop();
//            return true;
//        } else {
//            lastTranslationY = dependency.getTop() - lastTranslationY;
//            child.setTranslationY(-lastTranslationY);
//            return true;
//        }
        Log.i(TAG, "onDependentViewChanged: dependency.Top  " + dependency.getTop() + "  child.Top " + child.getTop() + "  " + parent.getBottom());
//        ViewCompat.offsetTopAndBottom(child, dependency.getBottom());
        final int mStartColor = ContextCompat.getColor(mContext, R.color.colorPrimary);
        final int mEndColor = ContextCompat.getColor(mContext, R.color.rank_column_title_color_anime);
        final int mHeight = child.getHeight();
        final int mDependcyTop = dependency.getTop();
        if (mDependcyTop <= 0) {
            final int offset = Math.max(-mHeight, mDependcyTop);
            float ratio = Math.abs(offset / (float)mHeight);
//            child.setTranslationY(-offset);
            child.setAlpha(1-ratio);
            child.setBackgroundColor((Integer) mArgbEvaluator.evaluate(ratio, mStartColor, mEndColor));
        }
        return true;
    }
}
