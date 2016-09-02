package com.chalilayang.test.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.chalilayang.test.aidl.BinderPoolImpl;

public class BinderPoolService extends Service {
    private static final String TAG = "BinderPoolService";
    private Binder mBinder = new BinderPoolImpl();
    public BinderPoolService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }
}
