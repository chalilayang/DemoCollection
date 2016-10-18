package com.chalilayang.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.chalilayang.test.constants.URLConstants;
import com.chalilayang.test.entity.ImageData;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener {
    public static final String DATA_KEY = "ImageData";
    private static final String TAG = "FullscreenActivity";
    public static final LatLng ZHONGGUANCUN = new LatLng(39.983456, 116.3154950);// 北京市中关村经纬度
    private static final com.baidu.mapapi.model.LatLng GEO_TIANJIN =
            new com.baidu.mapapi.model.LatLng(39.0833, 117.347);

    // 117.7133, 39.0272
    // 117.701, 39.0202
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
    private ScrollView imageInfoScrollView;
    private ImageButton locationBtn;
    private DisplayMetrics displayMetrics;

    private ImageData imageData;
//    private NearByInfoBean nearByInfo;

    //高德SDK
    private PoiSearch poiSearch;
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp = new LatLonPoint(39.0833, 117.347);

    //百度地图SDK
    private MapView mapView = null;
    private BaiduMap baiduMap;
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
        imageInfoScrollView = (ScrollView) findViewById(R.id.image_info_container);
        locationBtn = (ImageButton) findViewById(R.id.location_button);


        mapView = (MapView) findViewById(R.id.map);
        mapView.getLayoutParams().height = displayMetrics.widthPixels;
        baiduMap = mapView.getMap();

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
//            tryGetNearByInfo(data.getLongitude(),
//                    data.getLatitude(),
//                    new TypeToken<NearByResponse>(){}.getType()
//            );
            doSearchQuery(data.getLongitude(), data.getLatitude());
            locationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = Intent.getIntent(
                                URLConstants.getBaiduMapUri(
                                        imageData.getLongitude(),
                                        imageData.getLatitude()
                                )
                        );
                        startActivity(intent);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        imageInfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
            }
        });
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
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

    /*
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
    */
    private int savedMenuHeight = 0;
    private int savedHeightMeasured = 0;
    private boolean menuExpande = false;
    public void toggleMenu() {
        if (menuExpande) {
            performHideAnimator(imageInfoScrollView);
            menuExpande = false;
        } else {
            performShowAnimator(imageInfoScrollView);
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

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String longitude, String latitude) {
        if (TextUtils.isEmpty(longitude) || TextUtils.isEmpty(latitude)) {
            Toast.makeText(this, "缺少位置信息，无法定位", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "经纬度：" + longitude + " " + latitude, Toast.LENGTH_SHORT).show();
        double lon = Double.parseDouble(longitude);
        double lat = Double.parseDouble(latitude);
        setBaiduMapLocation(lon + 0.0065, lat + 0.0062);
        lp = new LatLonPoint(lat, lon);
        int currentPage = 0;
        /*
        "餐饮服务|购物服务|生活服务|体育休闲服务|住宿服务|风景名胜|商务住宅
        |政府机构及社会团体|科教文化服务|交通设施服务|公司企业|道路附属设施|地名地址信息|公共设施";
         */
        String poiType = "风景名胜";
        query = new com.amap.api.services.poisearch.PoiSearch.Query("", poiType, "");
        query.setPageSize(10);
        query.setPageNum(currentPage);

        if (lp != null) {
            poiSearch = new com.amap.api.services.poisearch.PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(
                    new com.amap.api.services.poisearch.PoiSearch.SearchBound(lp, 20000, true)
            );
            poiSearch.searchPOIAsyn();
        }
    }

    public void setBaiduMapLocation(double longitude, double latitude) {
        com.baidu.mapapi.model.LatLng point =
                new com.baidu.mapapi.model.LatLng(latitude, longitude);
        MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(point);
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(
                new MapStatus.Builder().zoom(15f).build())
        );
        baiduMap.setMapStatus(u1);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        baiduMap.addOverlay(option);
    }

    private PoiResult savedPoiResult;
    public String getDescByPoiResult(PoiResult result) {
        StringBuilder sb = new StringBuilder();
        if (result != null && result.getQuery() != null) {
            if (result.getQuery().equals(query)) {
                List<PoiItem> poiItems = result.getPois();
                if (poiItems != null && poiItems.size() > 0) {
                    sb.append(poiItems.get(0).getProvinceName())
                            .append("，").append(poiItems.get(0).getCityName())
                            .append("，").append(poiItems.get(0).getAdName())
                            .append('\n');
                    sb.append("经纬度：").append(lp.getLongitude())
                            .append(' ')
                            .append(lp.getLatitude())
                            .append('\n');
                    for (int index = 0, count = poiItems.size(); index < count; index++) {
                        sb.append(poiItems.get(index).getTitle())
                                .append("  ")
                                .append(poiItems.get(index).getDistance()).append('米')
                                .append('\n');
                        addMarker(
                                poiItems.get(index).getLatLonPoint().getLongitude() + 0.0065,
                                poiItems.get(index).getLatLonPoint().getLatitude() + 0.0062,
                                poiItems.get(index).getTitle()
                        );
                    }
                }
            }
        }
        return sb.toString();
    }
    @Override
    public void onPoiSearched(com.amap.api.services.poisearch.PoiResult result, int rcode) {
        if (rcode == 1000) {
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(query)) {
                    savedPoiResult = result;
                    mHideHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ValueAnimator animator = ValueAnimator.ofInt(0, HEIGHT_IMAGE_INFO_DEFAULT);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    ViewGroup.LayoutParams layoutParams = imageInfoScrollView.getLayoutParams();
                                    if (layoutParams != null) {
                                        layoutParams.height = (int)animation.getAnimatedValue();
                                        imageInfoScrollView.requestLayout();
                                    }
                                }
                            });
                            animator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    imageInfoTv.setText(getDescByPoiResult(savedPoiResult));
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
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    public void addMarker(double lon, double lat, String title) {
        com.baidu.mapapi.model.LatLng point =
                new com.baidu.mapapi.model.LatLng(lat, lon);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        baiduMap.addOverlay(option);

        OverlayOptions textOption = new TextOptions()
                .fontSize(30)
                .fontColor(getResources().getColor(R.color.black))
                .text(title)
                .position(point);
        baiduMap.addOverlay(textOption);
    }
}
