package com.lee.myweibo.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lee.myweibo.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by lee on 2017/2/25.
 */

public class PhotosPagerAdapter extends PagerAdapter {

    List<String> urls;
    List<View> views;
    Context context;
    View crtView;
    public PhotosPagerAdapter(List<String> urls, Context context){
        this.urls=urls;
        this.context=context;
        views=new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            View view= LayoutInflater.from(context).inflate(R.layout.layout_photoview,null);
            views.add(view);

        }
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object==view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.e("count",1+"");
        View view=views.get(position);
        Log.e("count","view +"+views.size());
        ImageView ivPhotho= (ImageView) view.findViewById(R.id.iv_photo);
//        ivPhotho.setImageResource(R.drawable.ic_com_sina_weibo_sdk_login_with_account_text_normal);
        setupImageView(ivPhotho,urls.get(position));
        container.addView(view);
        return view;
    }

    private void setupImageView(ImageView iv,String url) {
        final PhotoViewAttacher attacher=new PhotoViewAttacher(iv);
        Log.e("count",url);

        Picasso.with(context).load(url).into(iv, new Callback() {
            @Override
            public void onSuccess() {
                attacher.update();
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView(views.get(position));
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
       crtView= (View) object;
    }
    public View getPrimaryItem(){
        return crtView;
    }
}
