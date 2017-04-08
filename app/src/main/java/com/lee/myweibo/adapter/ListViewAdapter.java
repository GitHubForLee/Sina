package com.lee.myweibo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.myweibo.Constants;
import com.lee.myweibo.R;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lee on 2017/2/23.
 */

public class ListViewAdapter extends BaseAdapter {
    private List<Oauth2AccessToken> oauth2AccessTokenList;
    private LayoutInflater inflater;
    private Context context;
    public ListViewAdapter(Context context){
        this.inflater=LayoutInflater.from(context);
        this.context=context;
    }
    @Override
    public int getCount() {
        return oauth2AccessTokenList.size();
    }

    @Override
    public Object getItem(int i) {
        return oauth2AccessTokenList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public void setData(List<Oauth2AccessToken> oauth2AccessTokenList){
        this.oauth2AccessTokenList=oauth2AccessTokenList;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       final MyHolder holder;
        if (view == null) {
            view=inflater.inflate(R.layout.account_item,viewGroup,false);
            holder=new MyHolder(view);
            view.setTag(holder);
        }else {
            holder= (MyHolder) view.getTag();
        }

        Oauth2AccessToken crtToken=oauth2AccessTokenList.get(i);
        UsersAPI user=new UsersAPI(context, Constants.APP_KEY,crtToken);
        holder.position=i;
        user.show(Long.valueOf(crtToken.getUid()), new RequestListener() {
            @Override
            public void onComplete(String s) {
                Log.e("???",s);
                User userinfo=User.parse(s);
                holder.tv_userName.setText(userinfo.screen_name);
                Picasso.with(context).load(userinfo.profile_image_url).into(holder.iv_userIcon);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Log.e("???",e.toString());

            }
        });
        return view;
    }
    class MyHolder{
        ImageView iv_userIcon;
        TextView tv_userName;
        int position;
        public MyHolder(View itemView) {
            iv_userIcon= (ImageView) itemView.findViewById(R.id.iv_userIcon);
            tv_userName= (TextView) itemView.findViewById(R.id.tv_userName);

        }
    }
}
