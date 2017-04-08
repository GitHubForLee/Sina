package com.lee.myweibo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.myweibo.R;
import com.lee.myweibo.adapter.PhotosPagerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lee on 2017/2/25.
 */

public class PhotosActivity extends AppCompatActivity {
    @BindView(R.id.tv_whichPhoto)
    TextView tvWhichPhoto;
    @BindView(R.id.vp_photosContainer)
    ViewPager vpPhotosContainer;
    @BindView(R.id.fab_save)
    FloatingActionButton fabSave;
    int crtPosition;
    private List<String> urls;
    private String path;
    private PhotosPagerAdapter adapter;
    public static void startActivity(Context context, ArrayList<String> urls,int crtPosition){
        Intent intent=new Intent(context,PhotosActivity.class);
        intent.putStringArrayListExtra("urls",urls);
        intent.putExtra("position",crtPosition);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        ButterKnife.bind(this);
        path= Environment.getExternalStorageDirectory().toString()+"/weibo/picture/";
        setupViewPager();
        setupFAB();
        setupTextView();

    }

    private void setupTextView() {
        int a=crtPosition+1;
        int b=urls.size();
        tvWhichPhoto.setText(a+"/"+b);
    }

    private void setupFAB() {
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInFile();
            }
        });
    }

    private void saveInFile() {

        View crtView=adapter.getPrimaryItem();

        ImageView iv= (ImageView) crtView.findViewById(R.id.iv_photo);
        Drawable drawable=iv.getDrawable();
        if (drawable == null) {
            return;
        }
        BitmapDrawable bitmapDrawable= (BitmapDrawable) drawable;
        Bitmap bitmap=bitmapDrawable.getBitmap();

        File dir=new File(path);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file=new File(path,getFileName());
        if (file.exists()){
            Toast.makeText(this, "图片已存在，无需再次保存。", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            file.createNewFile();
            FileOutputStream out=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.flush();
            out.close();
            Toast.makeText(this, "成功保存到："+path+getFileName(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void setupViewPager() {
        Intent intent=getIntent();
        urls = intent.getStringArrayListExtra("urls");
        crtPosition=intent.getIntExtra("position",-1);
        Log.e("count", urls.size()+"");
        adapter=new PhotosPagerAdapter(urls,this);
        vpPhotosContainer.setAdapter(adapter);
        vpPhotosContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                crtPosition=position;
                setupTextView();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpPhotosContainer.setCurrentItem(crtPosition);
    }

    public String getFileName() {
        String crtUrl=urls.get(crtPosition);
        int index=crtUrl.lastIndexOf(".");
        int start=index-8;
        return crtUrl.substring(start);
    }
}
