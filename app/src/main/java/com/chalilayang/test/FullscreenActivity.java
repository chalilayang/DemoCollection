package com.chalilayang.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chalilayang.test.beans.NearByInfoBean;
import com.chalilayang.test.constants.URLConstants;
import com.chalilayang.test.entity.ImageData;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.io.File;
import java.lang.reflect.Type;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    public static final String DATA_KEY = "ImageData";
    private static final String TAG = "FullscreenActivity";
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static int HEIGHT_IMAGE_INFO_DEFAULT = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final Handler mHideHandler = new Handler();
    private RequestQueue requestQueue;
    private View menuContainer;
    private View btnContainer;
    private SimpleDraweeView simpleDraweeView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly PosBean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            simpleDraweeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            Log.i(TAG, "hide: " + menuContainer.getTranslationY() + "  " + menuContainer.getTop());
            ObjectAnimator.ofFloat(menuContainer,
                    "translationY",
                    0,
                    menuContainer.getMeasuredHeight()
            ).setDuration(150).start();
        }
    };
    private TextView imageInfoTv;
    private DisplayMetrics displayMetrics;

    private ImageData imageData;
    private NearByInfoBean nearByInfo;

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            Log.i(TAG, "show: " + menuContainer.getTranslationY() + "  " + menuContainer.getTop());
            ObjectAnimator.ofFloat(menuContainer,
                    "translationY",
                    menuContainer.getMeasuredHeight(),
                    0
            ).setDuration(150).start();
        }
    };
    private boolean mVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        displayMetrics = getResources().getDisplayMetrics();
        HEIGHT_IMAGE_INFO_DEFAULT = (int)(displayMetrics.heightPixels * 0.1f);
        mVisible = false;
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.fullscreen_content);
        imageInfoTv = (TextView) findViewById(R.id.image_info);
        menuContainer = findViewById(R.id.menu_container);
        btnContainer = findViewById(R.id.fullscreen_content_controls);
        // Set up the user interaction to manually show or hide the system UI.
        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        simpleDraweeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        requestQueue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        ImageData data = intent.getParcelableExtra(DATA_KEY);
        if (data != null) {
            imageData = data;
            String filepath = data.getFilePath();
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filepath, opts);

            int targetWidth;
            int targetHeight;
            if (opts.outWidth >= opts.outHeight) {
                targetWidth = displayMetrics.widthPixels;
                targetHeight = (int) (1.0f * targetWidth * opts.outHeight / opts.outWidth);
            } else {
                targetHeight = (int) (displayMetrics.heightPixels * 1.0f);
                targetWidth = (int) (1.0f * targetHeight * opts.outWidth / opts.outHeight);
            }
            ImageRequest imageRequest =
                    ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(new File(filepath)))
                            .setResizeOptions(new ResizeOptions(targetWidth, targetHeight))
                            .build();
            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .build();
            simpleDraweeView.setController(controller);
            tryGetNearByInfo(data.getLongitude(),
                    data.getLatitude(),
                    new TypeToken<NearByResponse>(){}.getType()
            );
        }
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        simpleDraweeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void tryGetNearByInfo(
            final String longitude,
            final String latitude,
            final Type responseType) {
        String url = URLConstants.getURL_NearByInfo(latitude, longitude);
        Log.i(TAG, "tryGetNearByInfo: " + url);
        StringRequest stringRequest = new StringRequest(
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            Gson gson = new Gson();
                            NearByResponse nearByResponse = gson.fromJson(response, responseType);
                            if (nearByResponse != null) {
                                nearByInfo = nearByResponse.getData();
                                mHideHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ValueAnimator animator = ValueAnimator.ofInt(0, HEIGHT_IMAGE_INFO_DEFAULT);
                                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                ViewGroup.LayoutParams layoutParams = imageInfoTv.getLayoutParams();
                                                if (layoutParams != null) {
                                                    layoutParams.height = (int)animation.getAnimatedValue();
                                                    imageInfoTv.requestLayout();
                                                }
                                            }
                                        });
                                        animator.addListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                imageInfoTv.setText(nearByInfo.getDesc());
                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        });
                                        animator.setInterpolator(new LinearInterpolator());
                                        animator.setDuration(300).start();
                                    }
                                });

                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
        });
        imageInfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
            }
        });
        requestQueue.add(stringRequest);
        requestQueue.start();
    }

    class NearByResponse {
        private String status;
        private NearByInfoBean data;

        public NearByInfoBean getData() {
            return data;
        }
    }

    private int savedMenuHeight = 0;
    private int savedHeightMeasured = 0;
    private boolean menuExpande = false;
    public void toggleMenu() {
        if (menuExpande) {
            performHideAnimator(imageInfoTv);
            menuExpande = false;
        } else {
            performShowAnimator(imageInfoTv);
            menuExpande = true;
        }
    }
    private void performShowAnimator(View view) {
        final View targetView = view;
        final int height = savedMenuHeight = menuContainer.getTop();
        final int initHeight = savedHeightMeasured = view.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                ViewGroup.LayoutParams layoutParams = targetView.getLayoutParams();
                if (targetView != null) {
                    layoutParams.height = initHeight + (int)(height * fraction);
                    targetView.requestLayout();
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                imageInfoTv.setText(nearByInfo.getStringInfo());
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(400).start();
    }

    private void performHideAnimator(View view) {
        final View targetView = view;
        final int initHeight = view.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofInt(initHeight, savedHeightMeasured);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = targetView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = (int)animation.getAnimatedValue();
                    targetView.requestLayout();
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                imageInfoTv.setText(nearByInfo.getDesc());
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(300).start();
    }

    public void performAnimation(View view, int fromHeight, int toHeight) {
        final View targetView = view;
        ValueAnimator animator = ValueAnimator.ofInt(fromHeight, toHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = targetView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = (int)animation.getAnimatedValue();
                    targetView.requestLayout();
                }
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(600).start();
    }
}
