package com.lee.myweibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.myweibo.Constants;
import com.lee.myweibo.R;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lee on 2017/2/22.
 */

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.UserHolder> {
    private List<Oauth2AccessToken> oauth2AccessTokenList;
    private LayoutInflater inflater;
    private Context context;
    public AccountAdapter(Context context){
        this.inflater=LayoutInflater.from(context);
        this.context=context;
    }
    public void setData(List<Oauth2AccessToken> oauth2AccessTokenList){
        this.oauth2AccessTokenList=oauth2AccessTokenList;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.account_item,parent,false);
        return new UserHolder(view);

    }

    @Override
    public void onBindViewHolder(final UserHolder holder, int position) {
        Oauth2AccessToken crtToken=oauth2AccessTokenList.get(position);
        UsersAPI user=new UsersAPI(context, Constants.APP_KEY,crtToken);
        holder.position=position;
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
    }

    @Override
    public int getItemCount() {

        return oauth2AccessTokenList.size();
    }
    public  class UserHolder extends RecyclerView.ViewHolder{
        ImageView iv_userIcon;
        TextView tv_userName;
        int position;
        public UserHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Oauth2AccessToken crtToken = oauth2AccessTokenList.get(position);
                    new StatusesAPI(context,Constants.APP_KEY,crtToken)
                            .update("这是一条测试微博", null, null, new RequestListener() {
                                @Override
                                public void onComplete(String s) {
                                    Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onWeiboException(WeiboException e) {

                                }
                            });
                }
            });
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    contextMenu.add(0,0,0,"取消授权");
                }
            });

            iv_userIcon= (ImageView) itemView.findViewById(R.id.iv_userIcon);
            tv_userName= (TextView) itemView.findViewById(R.id.tv_userName);

        }
    }
    public static interface OnClickListener{
        void click(Oauth2AccessToken token);
    }
}
