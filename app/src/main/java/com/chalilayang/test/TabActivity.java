package com.chalilayang.test;

import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.chalilayang.test.aidl.BinderPool;
import com.chalilayang.test.aidl.BinderPoolImpl;
import com.chalilayang.test.aidl.BookManagerImpl;

import java.util.List;

public class TabActivity extends AppCompatActivity {

    private static final String TAG = "TabActivity";
    private static final int MSG_NEWBOOK_ARRIVED = 911;
    private static final int MSG_BINDER_READY = 912;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private List<Book> mBookList;
    private IBookManager iBookManager;
    private BinderPool mBinderPool;

    private IOnNewBookArrivedListener mIOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            Log.i(TAG, "onNewBookArrived: newBook");
            Message msg = mHandler.obtainMessage(MSG_NEWBOOK_ARRIVED);
            msg.obj = newBook;
            mHandler.sendMessage(msg);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_NEWBOOK_ARRIVED:
                    Log.i(TAG, "handleMessage: receive new book " + msg.obj);
                    Snackbar.make(mViewPager, "receive new book " + ((Book)msg.obj).bookName, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                case MSG_BINDER_READY:
                    try {
                        mBookList = iBookManager.getBookList();
                        initView();
                        iBookManager.registerListener(mIOnNewBookArrivedListener);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBinderPool = BinderPool.getInstance(getApplicationContext());
                IBinder binder = mBinderPool.queryBinder(BinderPoolImpl.BINDER_ID_BOOKMANAGER);
                iBookManager = BookManagerImpl.asInterface(binder);
                mHandler.obtainMessage(MSG_BINDER_READY).sendToTarget();
            }
        }).start();
    }

    public void initView() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mBookList);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setupWithViewPager(mViewPager);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (iBookManager != null) {
                            int bookId = iBookManager.getBookList().size() + 1;
                            iBookManager.addBook(Book.newInstance(bookId, "王子复仇记"));
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (iBookManager != null && iBookManager.asBinder().isBinderAlive()) {
            try {
                Log.d(TAG, "onDestroy: unregister listener :" + mIOnNewBookArrivedListener);
                iBookManager.unregisterListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
//        BinderPool.unbindService();
        super.onDestroy();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Book> mBookList;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public SectionsPagerAdapter(FragmentManager fm, List<Book> bookList) {
            this(fm);
            this.mBookList = bookList;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            if (this.mBookList != null && this.mBookList.size() >= 3) {
                return this.mBookList.size();
            } else {
                return 5;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (this.mBookList != null && this.mBookList.size() >= 3) {
                return this.mBookList.get(position).bookName;
            } else {
                switch (position) {
                    case 0:
                        return "SECTION 1";
                    case 1:
                        return "SECTION 2";
                    case 2:
                        return "SECTION 3";
                    case 3:
                        return "SECTION 4";
                    case 4:
                        return "SECTION 5";

                }
            }
            return null;
        }
    }
}
