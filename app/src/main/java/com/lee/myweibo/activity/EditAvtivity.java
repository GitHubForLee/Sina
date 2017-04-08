package com.lee.myweibo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lee.myweibo.Constants;
import com.lee.myweibo.R;
import com.lee.myweibo.utils.SharePreferenceUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.StatusesAPI;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by lee on 2017/2/28.
 */

public class EditAvtivity extends AppCompatActivity {
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.bt_send)
    Button btSend;
    @BindView(R.id.cb_rac)
    CheckBox cbRac;
    @BindView(R.id.iv_pic)
    ImageView ivPic;
    @BindView(R.id.iv_pick)
    ImageView ivPick;
    Bitmap bitmap;
    boolean isRac;
    long id;
    Oauth2AccessToken token;
    public  static final int TYPE_COMMENT=0;
    public static final int TYPE_REPOST=1;
    public static final int TYPE_WEIBO=2;
    private int type;
    private RequestListener listener=new RequestListener() {
        @Override
        public void onComplete(String s) {
            Toast.makeText(EditAvtivity.this, "发送成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.e("error",e.toString());
            Toast.makeText(EditAvtivity.this, "发送失败，请检查网络连接。", Toast.LENGTH_SHORT).show();
        }
    };
    public static void startActivity(Context context,Long id,int type){
        Intent intent = new Intent(context,EditAvtivity.class);
        intent.putExtra("id",id);
        intent.putExtra("type",type);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_report);
        ButterKnife.bind(this);
        initdatas();
        setupViews();
        seupEvent();
    }

    private void initdatas() {
        Intent intent=getIntent();
        id=intent.getLongExtra("id",0);
        token= SharePreferenceUtil.readToken(this);
        type=intent.getIntExtra("type",-1);
    }

    private void seupEvent() {
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type==TYPE_REPOST){
                    repost();
                }else if (type==TYPE_WEIBO){
                    send();
                }else {
                    comment();
                }
                finish();
            }
        });
        if (type==TYPE_WEIBO){
            ivPick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MultiImageSelector.create()
                            .single()
                            .start(EditAvtivity.this,1);
                }
            });
            registerForContextMenu(ivPic);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,0,0,"删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId()==0){
            bitmap=null;
            ivPic.setVisibility(View.GONE);
        }
        return super.onContextItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==RESULT_OK){
            List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//            try {
//                FileInputStream fis=new FileInputStream(path.get(0));
//                bitmap= BitmapFactory.decodeStream(fis);

                bitmap=BitmapFactory.decodeFile(path.get(0));
                Log.e("bytes",bitmap.getByteCount()+"src");
                if (bitmap.getByteCount()>=3000000)
                    bitmap=Bitmap.createScaledBitmap(bitmap,460,600,false);
                Log.e("bytes",bitmap.getByteCount()+"");
                ivPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivPic.setImageBitmap(bitmap);
                ivPic.setVisibility(View.VISIBLE);
//           /
        }
    }

    private void comment(){
        WeiboParameters parameters=new WeiboParameters(Constants.APP_KEY);
        parameters.put("access_token",token.getToken());
        parameters.put("id",id);
        Log.e("israc",""+isRac);
        parameters.put("comment",etContent.getText().toString());
        CommentsAPI commentsAPI=new CommentsAPI(this,Constants.APP_KEY,token);
        commentsAPI.comment(parameters,listener);
    }
    private void repost() {
        WeiboParameters parameters=new WeiboParameters(Constants.APP_KEY);
        parameters.put("access_token",token.getToken());
        parameters.put("id",id);
        Log.e("israc",""+isRac);
        parameters.put("is_comment",isRac?3:0);
        parameters.put("status",etContent.getText().toString());
        StatusesAPI statusesAPI=new StatusesAPI(this,Constants.APP_KEY,token);
        statusesAPI.report(parameters,listener);
    }

    private void send(){
        WeiboParameters parameters=new WeiboParameters(Constants.APP_KEY);
        parameters.put("access_token",token.getToken());
        parameters.put("status",etContent.getText().toString());
        StatusesAPI statusesAPI=new StatusesAPI(this,Constants.APP_KEY,token);
        if (bitmap==null){
            statusesAPI.send(parameters,listener);
        }else {
            parameters.put("pic",bitmap);
            statusesAPI.sendWithPic(parameters,listener);
        }
    }
    private void setupViews() {
        
        if (type==TYPE_REPOST){
            cbRac.setVisibility(View.VISIBLE);
            etContent.setHint("说说分享心得…");
        }else if (type==TYPE_COMMENT){
            etContent.setHint("写评论…");
        }else {
            etContent.setHint("分享新鲜事…");
            ivPick.setVisibility(View.VISIBLE);
        }
        etContent.requestFocus();
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().equals("")){
                    btSend.setEnabled(false);
//                    btSend.setTextColor(Color.GRAY);
                }else {
                    btSend.setEnabled(true);
//                    btSend.setTextColor(getResources().getColor(R.color.colorOrange));
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cbRac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isRac=b;
            }
        });
        
        
    }

}
