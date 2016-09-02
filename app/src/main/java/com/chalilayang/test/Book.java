package com.chalilayang.test;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chalilayang on 2016/9/2.
 */
public class Book implements Parcelable {
    public int bookId;
    public String bookName;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bookId);
        dest.writeString(this.bookName);
    }

    public static final Parcelable.Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[0];
        }
    };

    private Book(Parcel in) {
        this.bookId = in.readInt();
        this.bookName = in.readString();
    }

    private Book(int id, String name) {
        this.bookName = name;
        this.bookId = id;
    }
    public static Book newInstance(int id, String name) {
        return new Book(id, name);
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
