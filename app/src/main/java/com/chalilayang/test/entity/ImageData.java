package com.chalilayang.test.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by chalilayang on 2016/10/9.
 */

public class ImageData implements Parcelable {
    private String filePath;
    private String title;
    private long timeStamp;
    private long filedId = -1;
    private String description;
    private String latitude;
    private String longitude;
    private String bucketName;

    public ImageData(@NonNull String path, @NonNull String mTitle, long filedId, long timeStamp) {
        this.filePath = path;
        this.title = mTitle;
        this.filedId = filedId;
        this.timeStamp = timeStamp;
    }
    public void setLocation(String lati, String lonti) {
        this.latitude = lati;
        this.longitude = lonti;
    }
    public void setBucketName(String name) {
        this.bucketName = name;
    }
    public String getLatitude() {
        return this.latitude;
    }
    public String getLongitude() {
        return this.longitude;
    }
    public String getBucketName() {
        return this.bucketName;
    }
    public void setDescription(String des) {
        this.description = des;
    }
    public String getDescription() {
        return this.description;
    }
    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeString(this.title);
        dest.writeLong(this.timeStamp);
        dest.writeLong(this.filedId);
        dest.writeString(this.description);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.bucketName);
    }

    public static final Parcelable.Creator<ImageData> CREATOR = new Creator<ImageData>() {
        @Override
        public ImageData createFromParcel(Parcel source) {
            return new ImageData(source);
        }

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[0];
        }
    };

    private ImageData(Parcel in) {
        this.filePath = in.readString();
        this.title = in.readString();
        this.timeStamp = in.readLong();
        this.filedId = in.readLong();
        this.description = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.bucketName = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
