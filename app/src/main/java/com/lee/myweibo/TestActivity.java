package com.lee.myweibo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class TestActivity extends AppCompatActivity {

    private ImageView ivImg;
    Bitmap bitmap;
    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ivImg = (ImageView) findViewById(R.id.iv_img);
        ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiImageSelector.create()
                        .single()
                        .start(TestActivity.this,1);

            }
        });
        registerForContextMenu(ivImg);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (bitmap!=null)
            menu.add(0,0,0,"删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId()==0){
            ivImg.setImageBitmap(null);
            bitmap=null;
        }
        return super.onContextItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("url","requ "+requestCode+"  resu  "+resultCode);
        if (requestCode==1&&resultCode==RESULT_OK){
            List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            try {
                FileInputStream fis=new FileInputStream(path.get(0));
               bitmap= BitmapFactory.decodeStream(fis);
                ivImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivImg.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

//            Picasso.with(this).load(path.get(0)).into(ivImg);
        }
    }
}