package com.chalilayang.test.aidl;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.chalilayang.test.Book;
import com.chalilayang.test.IBookManager;
import com.chalilayang.test.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by chalilayang on 2016/9/2.
 */
public class BookManagerImpl extends IBookManager.Stub {
    private static final String TAG = "BookManagerImpl";
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mLisnteners
            = new RemoteCallbackList<>();

    public BookManagerImpl() {
        super();
        init();
    }

    private void init() {
        mBookList.add(Book.newInstance(1, "平凡的世界"));
        mBookList.add(Book.newInstance(2, "高山下的花环"));
        mBookList.add(Book.newInstance(3, "人生"));
        mBookList.add(Book.newInstance(4, "霓虹灯下的哨兵"));
        mBookList.add(Book.newInstance(5, "狂人日记"));
        mBookList.add(Book.newInstance(6, "诗经"));
        mBookList.add(Book.newInstance(7, "几度夕阳红"));
        mBookList.add(Book.newInstance(8, "血染的风采"));
    }
    @Override
    public List<Book> getBookList() throws RemoteException {
        return mBookList;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
        this.onNewBookArrived(book);
    }

    @Override
    public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
        mLisnteners.register(listener);
        Log.i(TAG, "registerListener: success");
    }

    @Override
    public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
        mLisnteners.unregister(listener);
        Log.i(TAG, "unregisterListener: success");
    }

    public void onNewBookArrived(Book book) throws RemoteException{
        Log.i(TAG, "onNewBookArrived: book " + book.bookName);
        boolean hasAdded = false;
        if (!mBookList.contains(book)) {
            mBookList.add(book);
            hasAdded = true;
        }
        Log.i(TAG, "onNewBookArrived: new Book has been added");
        if (hasAdded) {
            final int N = mLisnteners.beginBroadcast();
            for (int index = 0; index < N; index ++) {
                IOnNewBookArrivedListener listener = mLisnteners.getBroadcastItem(index);
                if (listener != null) {
                    listener.onNewBookArrived(book);
                }
            }
            mLisnteners.finishBroadcast();
        }
    }
}
