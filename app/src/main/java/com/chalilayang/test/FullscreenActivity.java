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

import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private final Handler mHideHandler = new Handler();
    private RequestQueue requestQueue;
    private View menuContainer;
    private SimpleDraweeView simpleDraweeView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            menuContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private TextView imageInfoTv;
    private DisplayMetrics displayMetrics;

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            menuContainer.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        displayMetrics = getResources().getDisplayMetrics();
        mVisible = true;
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.fullscreen_content);
        imageInfoTv = (TextView) findViewById(R.id.image_info);
        menuContainer = findViewById(R.id.menu_container);
        // Set up the user interaction to manually show or hide the system UI.
        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        requestQueue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        ImageData data = intent.getParcelableExtra(DATA_KEY);
        if (data != null) {
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
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
        menuContainer.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        menuContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
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
                            NearByResponse nearByResponse = (NearByResponse) gson.fromJson(response, responseType);
                            if (nearByResponse != null) {
                                NearByInfoBean data = nearByResponse.getData();
                                imageInfoTv.setText(data.getPos());
                                imageInfoTv.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageInfoTv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
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
}
