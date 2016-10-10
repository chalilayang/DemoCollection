package com.chalilayang.test.entity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chalilayang on 2016/9/23.
 */

public class BaseData {
    private String mFilePath;
    private int mResID;
    private String mTitle;
    private long timeStamp;
    private long filedId = -1;

    public BaseData(@NonNull String path, @NonNull String mTitle, long filedId, long timeStamp) {
        this.mFilePath = path;
        this.mTitle = mTitle;
        this.filedId = filedId;
        this.timeStamp = timeStamp;
    }

    public int getmResID() {
        return mResID;
    }
    public String getmTitle() {
        return mTitle;
    }
    public String getBitmapPath() {
        return this.mFilePath;
    }
    public long getFiledId() {
        return this.filedId;
    }
    public long getTimeStamp() {
        return this.timeStamp;
    }
    public void setmFilePath(String path) {
        this.mFilePath = path;
    }
    public void setmTitle(String title) {
        this.mTitle = title;
    }
    public void setmResID(int id) {
        this.mResID = id;
    }
    public void setTimeStamp(long dd) {
        this.timeStamp = dd;
    }
    public void setFiledId(long dd) {
        this.filedId = dd;
    }

    public String getTimeString() {
        try {
            Date dateOld = new Date(timeStamp); // 根据long类型的毫秒数生命一个date类型的时间
            String sDateTime = new SimpleDateFormat("yyyy-MM-dd").format(dateOld);
            return sDateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
