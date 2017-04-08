package com.lee.myweibo.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.lee.myweibo.activity.PhotosActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2017/2/24.
 */

public class NineGridAdapter extends NineGridImageViewAdapter<String> {
    @Override
    protected void onDisplayImage(Context context, ImageView imageView, String s) {
        Log.e("image",s);
        Glide.with(context).load(s).into(imageView);
    }

    @Override
    protected void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
        super.onItemImageClick(context, imageView, index, list);
        PhotosActivity.startActivity(context, (ArrayList<String>) list,index);
    }

    @Override
    protected ImageView generateImageView(Context context) {
        return super.generateImageView(context);
    }
}
