package com.chalilayang.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

import org.askerov.dynamicgrid.example.GridActivity;

import com.balysv.materialmenu.MaterialMenuView;
import com.balysv.materialmenu.MaterialMenuDrawable.IconState;
import com.mobeta.android.demodslv.Launcher;

import cn.bmob.v3.Bmob;


public class MainActivity extends Activity implements ActivitiesAdapter.onItemClickListener {
    public RecyclerView mRecyclerView;
    public DisplayMetrics mDisplayMetrics;
    private ActivitiesAdapter mActivitiesAdapter;
    private MaterialMenuView mMaterialMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initMetrics();
    }

    private void initMetrics() {
        mDisplayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        Log.i("displayMetrics", mDisplayMetrics.toString());
    }

    private void init() {
        Bmob.initialize(this, "c2a7ca6835292ac0bb7caf216d4e3bcc");
        if (mMaterialMenuView == null) {
            mMaterialMenuView = (MaterialMenuView) findViewById(R.id.action_bar_menu);
            mMaterialMenuView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    IconState state = mMaterialMenuView.getState();
                    switch (state) {
                        case ARROW:
                            mMaterialMenuView.animateState(IconState.BURGER);
                            break;
                        case BURGER:
                            mMaterialMenuView.animateState(IconState.ARROW);
                            break;
                        default:
                            break;
                    }

                }
            });
        }
        if (mActivitiesAdapter == null) {
            mActivitiesAdapter = new ActivitiesAdapter(getApplicationContext());
            mActivitiesAdapter.setmDataList(getData());
        }
        if (mRecyclerView == null) {
            mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutAnimation(getAnimationController());
        }

        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mActivitiesAdapter);
        mActivitiesAdapter.setOnItemClickListener(this);
    }

    private List<ActivitiesAdapter.Data> getData() {
        List<ActivitiesAdapter.Data> mList = new ArrayList<ActivitiesAdapter.Data>(10);
        mList.add(new ActivitiesAdapter.Data(R.drawable.behance, getString(R.string.explosion)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.youtube, getString(R.string.waveloading)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.facebook, getString(R.string.sticky_list_view)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.dribble, getString(R.string.lable_tag_sample)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.dropbox, getString(R.string.sticky_list_view_extra)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.instagram, getString(R.string.clip_path_demo)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.linkdin, getString(R.string.title_activity_dynamic_gridview)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.twitter, getString(R.string.dslv_demo)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.pintrest, getString(R.string.sprindicator_demo)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.behance, getString(R.string.title_activity_scrolling)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.deviantart, getString(R.string.title_activity_tab)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.googleplus, getString(R.string.title_activity_navigation_drawer)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.youtube, getString(R.string.title_activity_maptest)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.facebook, getString(R.string.title_activity_login)));
        mList.add(new ActivitiesAdapter.Data(R.drawable.dribble, getString(R.string.title_activity_flow)));
        return mList;
    }

    /**
     * Layout动画
     *
     * @return
     */
    protected LayoutAnimationController getAnimationController() {
        int duration = 400;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.3f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
    }

    @Override
    public void onItemClick(View view, int position) {
        // TODO Auto-generated method stub
        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(this, ExplosionActivity.class);
                break;
            case 1:
                intent = new Intent(this, WaveLoadingActivity.class);
                break;
            case 2:
                intent = new Intent(this, StickyListActivity.class);
                break;
            case 3:
                intent = new Intent(this, LableTagActivity.class);
                break;
            case 4:
                intent = new Intent(this, StickyListTestActivity.class);
                break;
            case 5:
                intent = new Intent(this, CustomViewActivity.class);
                break;
            case 6:
                intent = new Intent(this, GridActivity.class);
                break;
            case 7:
                intent = new Intent(this, Launcher.class);
                break;
            case 8:
                intent = new Intent(this, SprindicatorActivity.class);
                break;
            case 9:
                intent = new Intent(this, ScrollingActivity.class);
                break;
            case 10:
                intent = new Intent(this, TabActivity.class);
                break;
            case 11:
                intent = new Intent(this, NavigationDrawerActivity.class);
                break;
            case 12:
                intent = new Intent(this, Fullscreen2Activity.class);
                break;
            case 13:
                intent = new Intent(this, LoginActivity.class);
                break;
            case 14:
                intent = new Intent(this, FlowActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            this.startActivity(intent);
        }
    }
}
