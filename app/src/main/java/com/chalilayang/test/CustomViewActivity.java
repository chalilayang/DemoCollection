package com.chalilayang.test;

import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chalilayang.test.customview.CircleLoadingView;

public class CustomViewActivity extends Activity {
    private static final String TAG = "CustomViewActivity";
    /* load our native library */
    static {
//        System.loadLibrary("plasma");
    }

    private EditText mEditText;
    private CircleLoadingView mCircleLoadingView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customview_layout);
        mCircleLoadingView = (CircleLoadingView) findViewById(R.id.wave_test_view);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mButton = (Button) findViewById(R.id.buttonPanel);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueStr = mEditText.getText().toString();
                if (!TextUtils.isEmpty(valueStr)) {
                    try {
                        float value = Float.parseFloat(valueStr);
                        mCircleLoadingView.setProgress((int)value);
                    } catch (NumberFormatException e) {
                        Toast.makeText(CustomViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
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
