package com.chalilayang.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by chalilayang on 2016/5/13.
 */
public class ActivitiesAdapter extends RecyclerView.Adapter {

    private List<Data> mDataList;
    private Context mContext;
    private onItemClickListener onItemClickListener;

    public ActivitiesAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setmDataList(List<Data> mDataList) {
        this.mDataList = mDataList;
    }

    public void setOnItemClickListener(ActivitiesAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_cardview_item_layout, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mDataList != null) {
            Data mData = mDataList.get(position);
            ActivityViewHolder tempHolder = (ActivityViewHolder)holder;
            tempHolder.mPosition = position;
            tempHolder.mImageView.setImageResource(mData.getmResID());
            tempHolder.mTextView.setText(mData.getmTitle());
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mImageView;
        public TextView mTextView;
        public View mRoot;
        public int mPosition;
        public ActivityViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.activity_icon);
            mTextView = (TextView) itemView.findViewById(R.id.activity_title);
            mRoot = itemView.findViewById(R.id.cardview);
            mRoot.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (ActivitiesAdapter.this.onItemClickListener != null) {
                ActivitiesAdapter.this.onItemClickListener.onItemClick(v, mPosition);
            }
        }
    }

    static class Data {
        private int mResID;

        private String mTitle;

        public Data(int mResID, String mTitle) {
            this.mResID = mResID;
            this.mTitle = mTitle;
        }

        public int getmResID() {
            return mResID;
        }
        public String getmTitle() {
            return mTitle;
        }
    }

    public interface onItemClickListener {
        public void onItemClick(View view, int position);
    }
}
