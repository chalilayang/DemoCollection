package com.chalilayang.test;

import android.os.Bundle;
import android.app.Activity;
import android.view.Display;

import com.chalilayang.test.customview.BitmapMeshView;
import com.chalilayang.test.customview.WaveTestView;

public class CustomViewActivity extends Activity {
    /* load our native library */
    static {
//        System.loadLibrary("plasma");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customview_layout);
    }
}
