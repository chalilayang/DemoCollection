package com.chalilayang.test;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

public class CustomViewActivity extends Activity {
    private static final String TAG = "CustomViewActivity";
    /* load our native library */
    static {
//        System.loadLibrary("plasma");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customview_layout);
    }

    private void sayHello(String dds) {
        Log.d(TAG, "sayHello: " + dds);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
