// IBookManager.aidl
package com.chalilayang.test;

// Declare any non-default types here with import statements
import com.chalilayang.test.Book;
import com.chalilayang.test.IOnNewBookArrivedListener;
interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);
}
