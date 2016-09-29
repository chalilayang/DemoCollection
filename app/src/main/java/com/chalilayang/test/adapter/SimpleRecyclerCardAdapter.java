package com.chalilayang.test.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.chalilayang.test.R;
import com.chalilayang.test.entity.BaseData;
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
    private List<BaseData> mDataList = new ArrayList<>();
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

    public void addDataList(List<BaseData> mDataList) {
        this.mDataList.addAll(mDataList);
        this.notifyDataSetChanged();
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
            BaseData mData = mDataList.get(position);
            FlowViewHolder tempHolder = (FlowViewHolder)holder;
            tempHolder.mPosition = position;
            tempHolder.mTextView.setText(mData.getmTitle());
            float ratio = -1f;
            if (mData.getBitmap() != null) {
                tempHolder.mImageView.setImageBitmap(mData.getBitmap());
//            } else if (mData.getFiledId() >= 0) {
//                SimpleDraweeView view = (SimpleDraweeView) tempHolder.mImageView;
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                Bitmap thumnail = MediaStore.Images.Thumbnails
//                        .getThumbnail(
//                                cr,
//                                mData.getFiledId(),
//                                MediaStore.Images.Thumbnails.MINI_KIND,
//                                options
//                        );//获取指定图片缩略片
//                ratio = thumnail.getHeight() * 1f / thumnail.getWidth();
//                view.setImageBitmap(thumnail);
//                ImageRequest imgRequest = new ImageRequest();
//                boolean inMemoryCache = imagePipeline.isInBitmapMemoryCache(uri);
//                imagePipeline.getCacheKeyFactory().getBitmapCacheKey(ImageRequest)
//
//                tempHolder.mTextView.setText("缩略图"+mData.getmTitle());
            } else if (!TextUtils.isEmpty(mData.getBitmapPath())) {
                Log.i(TAG, "onBindViewHolder: start  " + position);
                if (position == 31) {
                    Log.i(TAG, "onBindViewHolder: start  " + position);
                }
                SimpleDraweeView view = (SimpleDraweeView) tempHolder.mImageView;
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mData.getBitmapPath(), opts);
                ratio = opts.outHeight * 1f / opts.outWidth;

                String filepath = mData.getBitmapPath();
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
            } else if (mData.getmResID() > 0) {
                tempHolder.mImageView.setImageResource(mData.getmResID());
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

    class FlowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        }

        @Override
        public void onClick(View v) {
            if (SimpleRecyclerCardAdapter.this.onItemClickListener != null) {
                SimpleRecyclerCardAdapter.this.onItemClickListener.onItemClick(v, mPosition);
            }
        }
    }

    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }
}