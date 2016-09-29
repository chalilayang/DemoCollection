package com.chalilayang.test;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.chalilayang.test.adapter.SimpleRecyclerCardAdapter;
import com.chalilayang.test.entity.BaseData;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FlowActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "FlowActivity";
    private static final int MSG_READY = 1212;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<BaseData> mDatas = null;
    private List<BaseData> mBitmapDatas = new ArrayList<>();

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private DisplayMetrics displayMetrics;
    private SimpleDraweeView floatingView;

    private int lastSelectionFloating = 0;

    private SimpleRecyclerCardAdapter mSimpleRecyclerAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_READY:
                    mSimpleRecyclerAdapter.addDataList(mBitmapDatas);
                    Toast.makeText(FlowActivity.this, "图片获取成功" + mBitmapDatas.size(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.app_recyclerview);
        windowManager = (WindowManager) getSystemService(
                Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        displayMetrics = getResources().getDisplayMetrics();

        initAppToolBar();
        initDataAndView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllSDImageFolder();
                mHandler.obtainMessage(MSG_READY).sendToTarget();
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initDataAndView() {
        mSimpleRecyclerAdapter = new SimpleRecyclerCardAdapter(this);
        mRecyclerView.setAdapter(mSimpleRecyclerAdapter);
        //设置网格布局管理器
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mSimpleRecyclerAdapter.setOnItemClickListener(new SimpleRecyclerCardAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (lastSelectionFloating == position) {
                    return;
                }
                Toast.makeText(FlowActivity.this,
                        "pos " + position + " "+ view.getTop() + " "+view.getLeft(),
                        Toast.LENGTH_SHORT).show();

                createFloatView(view, position);
            }
        });
    }

    /**
     * init app bar
     */
    private void initAppToolBar() {
        setSupportActionBar(mToolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private List<BaseData> getData() {
        List<BaseData> mList = new ArrayList<BaseData>(10);
        mList.add(new BaseData(R.drawable.behance, getString(R.string.explosion)));
        mList.add(new BaseData(R.drawable.youtube, getString(R.string.waveloading)));
        mList.add(new BaseData(R.drawable.facebook, getString(R.string.sticky_list_view)));
        mList.add(new BaseData(R.drawable.dribble, getString(R.string.lable_tag_sample)));
        mList.add(new BaseData(R.drawable.dropbox, getString(R.string.sticky_list_view_extra)));
        return mList;
    }

    private boolean hasFloatViewAdded = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (hasFloatViewAdded) {
            windowManager.removeView(floatingView);
            floatingView = null;
            hasFloatViewAdded = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setShortcutsVisible(Menu menu) {
        if (MenuBuilder.class.isInstance(menu)) {
            MenuBuilder builder = (MenuBuilder) menu;
            builder.setShortcutsVisible(true);
            try {
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(builder, true);
            } catch (Exception ie) {
            }
        }
    }

    private void getAllSDImageFolder() {

        ContentResolver cr = getContentResolver();

        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);


        if(null != cursor){
            while(cursor.moveToNext()){
                long fileId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String filename = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                this.mBitmapDatas.add(new BaseData(path, filename, fileId));
            }
            if(!cursor.isClosed()){
                cursor.close();
            }
        }
    }

    public void createFloatView(View view, int postion) {
        BaseData data = mBitmapDatas.get(postion);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(data.getBitmapPath(), opts);
        int targetWidth;
        int targetHeight;
        if (opts.outWidth >= opts.outHeight) {
            targetWidth = displayMetrics.widthPixels;
            targetHeight = (int)(1.0f * targetWidth * opts.outHeight / opts.outWidth);
        } else {
            targetHeight = (int)(displayMetrics.heightPixels * 0.7f);
            targetWidth = (int)(1.0f * targetHeight * opts.outWidth / opts.outHeight);
        }
        if (floatingView == null) {
            floatingView = new SimpleDraweeView(this);
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;// 系统提示window
            layoutParams.format = PixelFormat.TRANSLUCENT;// 支持透明
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
            layoutParams.width = targetWidth;//窗口的宽和高
            layoutParams.height = targetHeight;
            layoutParams.x = 0;//Math.round((displayMetrics.widthPixels - targetWidth)*0.5f);//窗口位置的偏移量
            layoutParams.y = 0;//Math.round((displayMetrics.heightPixels - targetHeight)*0.5f);
            windowManager.addView(floatingView, layoutParams);
            hasFloatViewAdded =true;
        }
        layoutParams.width = targetWidth;//窗口的宽和高
        layoutParams.height = targetHeight;

        String filepath = data.getBitmapPath();
        ImageRequest imageRequest =
                ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(new File(filepath)))
                .setResizeOptions(new ResizeOptions(targetWidth, targetHeight))
                .build();
        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .build();
        floatingView.setController(controller);
        windowManager.updateViewLayout(floatingView, layoutParams);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
