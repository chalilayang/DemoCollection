package com.chalilayang.test.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chalilayang.test.R;
import com.chalilayang.test.entity.BaseData;
import com.chalilayang.test.entity.ImageData;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class SimpleRecyclerCardAdapter extends RecyclerView.Adapter {

    private static final String TAG = "SimpleRecyclerCardAdapt";
    private List<ImageData> mDataList = new ArrayList<>();
    private Context mContext;
    private SimpleRecyclerCardAdapter.onItemClickListener onItemClickListener;
    private int mBaseHeight;
    private int mMeasuredWidth;
    private ContentResolver cr;
    public DisplayMetrics mDisplayMetrics;

    public SimpleRecyclerCardAdapter(Context mContext) {
        this.mContext = mContext;
        mBaseHeight = mContext.getResources().getDimensionPixelSize(R.dimen.app_bar_height);
        cr = mContext.getContentResolver();
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        mMeasuredWidth = mDisplayMetrics.widthPixels / 2;
    }

    public ImageData getItem(int position) {
        if (mDataList != null && mDataList.size() > position) {
            return mDataList.get(position);
        }
        return null;
    }

    public void clearDataList() {
        this.mDataList.clear();
        this.notifyDataSetChanged();
    }

    public void addDataList(List<ImageData> baseDatas) {
        int itemCount = this.mDataList.size();
        this.mDataList.addAll(baseDatas);
        this.notifyItemRangeInserted(itemCount, baseDatas.size());
    }

    public boolean deleteData(int position) {
        if (mDataList != null && mDataList.size() > position) {
            boolean success = true;
//            File file = new File(mDataList.get(position).getBitmapPath());
//            if (file != null && file.exists()) {
//                ContentResolver cr = mContext.getContentResolver();
//                success = cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        BaseColumns._ID + "=" + mDataList.get(position).getFiledId(), null) >= 1;
//            }
            if (success) {
                mDataList.remove(position);
                this.notifyItemRemoved(position);
                int count = Math.min(this.getItemCount() - position, 10);
                this.notifyItemRangeChanged(position, count);
            }
            return success;
        } else {
            Toast.makeText(mContext, "deleting pos " + position + " size " + mDataList.size(),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void setOnItemClickListener(SimpleRecyclerCardAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.simple_card_item, parent, false);
        return new FlowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mDataList != null) {
            ImageData mData = mDataList.get(position);
            FlowViewHolder tempHolder = (FlowViewHolder)holder;
            tempHolder.mPosition = position;
            tempHolder.mTextView.setText("pos " + position + " " + mData.getBucketName());
            float ratio = -1f;
            if (!TextUtils.isEmpty(mData.getFilePath())) {
                Log.i(TAG, "onBindViewHolder: start  " + position);
                if (position == 31) {
                    Log.i(TAG, "onBindViewHolder: start  " + position);
                }
                SimpleDraweeView view = (SimpleDraweeView) tempHolder.mImageView;
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mData.getFilePath(), opts);
                ratio = opts.outHeight * 1f / opts.outWidth;

                String filepath = mData.getFilePath();
                ImageRequest imageRequest = getImageRequest(
                        filepath,
                        this.mMeasuredWidth,
                        (int)(ratio * this.mMeasuredWidth)
                );
                AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .build();
                view.setController(controller);
                Log.i(TAG, "onBindViewHolder: end  " + position);
            }

            int tt = (position+1) % 2;
            int pp = (position+1) % 3;
            int meaWidth = tempHolder.mRoot.getMeasuredWidth();
            if (meaWidth > 0) {
                this.mMeasuredWidth = meaWidth;
            }
            if (ratio > 0) {
                tempHolder.mRoot.getLayoutParams().height =
                        (int)(ratio * this.mMeasuredWidth);
//                tempHolder.mTextView.setText(ratio + "  " + tempHolder.mRoot.getLayoutParams().height);
                tempHolder.mRoot.requestLayout();
            } else {
                if (tt == 0) {
                    tempHolder.mRoot.getLayoutParams().height = Math.round(1.0f * mBaseHeight);
                    tempHolder.mRoot.requestLayout();
                } else if (pp == 0) {
                    tempHolder.mRoot.getLayoutParams().height = Math.round(1.2f * mBaseHeight);
                    tempHolder.mRoot.requestLayout();
                } else {
                    tempHolder.mRoot.getLayoutParams().height = Math.round(0.8f * mBaseHeight);
                    tempHolder.mRoot.requestLayout();
                }
            }
        }
    }

    private ImageRequest getImageRequest(String filePath, int width,int height) {
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        return ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
    }

    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    class FlowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView mImageView;
        public TextView mTextView;
        public View mRoot;
        public int mPosition;
        public FlowViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.item_img);
            mTextView = (TextView) itemView.findViewById(R.id.item_title);
            mRoot = itemView.findViewById(R.id.cardview);
            mRoot.setOnClickListener(this);
            mRoot.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (SimpleRecyclerCardAdapter.this.onItemClickListener != null) {
                SimpleRecyclerCardAdapter.this.onItemClickListener.onItemClick(v, mPosition);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (SimpleRecyclerCardAdapter.this.onItemClickListener != null) {
                return SimpleRecyclerCardAdapter.this
                        .onItemClickListener.onItemLongClick(v, mPosition);
            }
            return false;
        }
    }

    public interface onItemClickListener {
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
    }
}