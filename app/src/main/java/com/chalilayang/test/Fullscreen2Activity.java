package com.chalilayang.test;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;

import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Fullscreen2Activity extends AppCompatActivity implements
        OnGetPoiSearchResultListener, com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener {
    private static final LatLng GEO_BEIJING = new LatLng(39.945, 116.404);
    private static final LatLng GEO_SHANGHAI = new LatLng(31.227, 121.481);
    private static final LatLng GEO_GUANGZHOU = new LatLng(23.155, 113.264);
    private static final LatLng GEO_SHENGZHENG = new LatLng(22.560, 114.064);
    private static final LatLng GEO_TIANJIN = new LatLng(39.0833, 117.347);
    private PoiSearch mPoiSearch = null;
    private int loadIndex = 0;

//    private LatLng center = new LatLng(39.92235, 116.380338);
    private int radius = 1500;


    //高德SDK
    private com.amap.api.services.poisearch.PoiSearch poiSearch;
    private com.amap.api.services.poisearch.PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp = new LatLonPoint(39.0833, 117.347);//
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
    private MapView mapView;
    private BaiduMap baiduMap;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly PosBean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mapView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
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

        setContentView(R.layout.activity_fullscreen2);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mapView = (MapView) findViewById(R.id.fullscreen_content);

        poiSearch = new com.amap.api.services.poisearch.PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);

        // Set up the user interaction to manually show or hide the system UI.
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        baiduMap = mapView.getMap();
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(GEO_TIANJIN);
        baiduMap.setMapStatus(u1);
        mapView.setLogoPosition(LogoPosition.logoPostionRightTop);
        setBaiduMapLocation(0, 0);
    }

    public void setBaiduMapLocation(double longitude, double latitude) {
//        com.baidu.mapapi.model.LatLng point =
//                new com.baidu.mapapi.model.LatLng(latitude, longitude);
        MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(GEO_TIANJIN);
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(
                new MapStatus.Builder().zoom(15f).build())
        );
        baiduMap.setMapStatus(u1);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
        OverlayOptions option = new MarkerOptions()
                .position(GEO_TIANJIN)
                .icon(bitmap);
        baiduMap.addOverlay(option);
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
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mapView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mPoiSearch.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        doSearchQuery();
    }

    /**
     * 响应周边搜索按钮点击事件
     *
     */
    public void  searchNearbyProcess() {
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .sortType(PoiSortType.distance_from_near_to_far).location(GEO_TIANJIN)
                .radius(radius).pageNum(loadIndex);
        mPoiSearch.searchNearby(nearbySearchOption);
    }

    public void searchNearByInfosByGaode() {
        String poiType = "餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";
        query = new com.amap.api.services.poisearch.PoiSearch.Query("", poiType, "");
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，
        //POI搜索类型共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
         // 住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);//设置查询页码

        poiSearch.setBound(new com.amap.api.services.poisearch.PoiSearch.SearchBound(lp, 5000, true));
        poiSearch.searchPOIAsyn();
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        int currentPage = 0;
        query = new com.amap.api.services.poisearch.PoiSearch.Query("", "住宿服务", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new com.amap.api.services.poisearch.PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new com.amap.api.services.poisearch.PoiSearch.SearchBound(lp, 50000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }
    /**
     * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
     * @param result
     */
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(Fullscreen2Activity.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            List<PoiInfo> poiInfos = result.getAllPoi();
            if (poiInfos != null && poiInfos.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for(int index = 0, count = poiInfos.size(); index < count; index ++) {
                    sb.append(poiInfos.get(index).name).append('\n');
                }
                Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(Fullscreen2Activity.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void onGetPoiDetailResult(PoiDetailResult var1) {

    }

    public void onGetPoiIndoorResult(PoiIndoorResult var1) {

    }

    @Override
    public void onPoiSearched(com.amap.api.services.poisearch.PoiResult result, int rcode) {
        if (rcode == 1000) {
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(query)) {
                    List<PoiItem> poiItems = result.getPois();
                    if (poiItems != null && poiItems.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for(int index = 0, count = poiItems.size(); index < count; index ++) {
                            sb.append(poiItems.get(index).getTitle()).append('\n');
                        }
                        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
