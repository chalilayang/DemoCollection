package com.chalilayang.test.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.chalilayang.test.Book;
import com.chalilayang.test.IBookManager;
import com.chalilayang.test.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";
    private volatile boolean isServiceDestroyed = false;
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mLisnteners
            = new RemoteCallbackList<>();
    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            onNewBookArrived(book);
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
    };
    public BookManagerService() {
        init();
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isServiceDestroyed = true;
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
    public IBinder onBind(Intent intent) {
        return this.mBinder;
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

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while(!isServiceDestroyed) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book book = Book.newInstance(bookId, "book#" + bookId);
                try {
                    onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
