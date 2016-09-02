// IOnNewBookArrivedListener.aidl
package com.chalilayang.test;

// Declare any non-default types here with import statements
import com.chalilayang.test.Book;
interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}
