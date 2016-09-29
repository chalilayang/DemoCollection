package com.chalilayang.test.entity;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Created by chalilayang on 2016/9/23.
 */

public class BaseData {
    private String mFilePath;
    private int mResID;
    private Bitmap mBitmap;
    private String mTitle;
    private long filedId = -1;

    public BaseData(int mResID, String mTitle) {
        this.mResID = mResID;
        this.mTitle = mTitle;
    }

    public BaseData(@NonNull Bitmap bitmap, @NonNull String mTitle) {
        this.mBitmap = bitmap;
        this.mTitle = mTitle;
    }

    public BaseData(@NonNull String path, @NonNull String mTitle, long filedId) {
        this.mFilePath = path;
        this.mTitle = mTitle;
        this.filedId = filedId;
    }

    public int getmResID() {
        return mResID;
    }
    public String getmTitle() {
        return mTitle;
    }
    public Bitmap getBitmap() {
        return this.mBitmap;
    }
    public String getBitmapPath() {
        return this.mFilePath;
    }
    public long getFiledId() {
        return this.filedId;
    }
}
