package com.chalilayang.test.aidl;

import android.os.IBinder;
import android.os.RemoteException;

import com.chalilayang.test.IBinderPool;

/**
 * Created by chalilayang on 2016/9/2.
 */
public class BinderPoolImpl extends IBinderPool.Stub {
    public BinderPoolImpl() {
        super();
    }
    @Override
    public IBinder queryBinder(int binderCode) throws RemoteException {
        return new BookManagerImpl();
    }
}
