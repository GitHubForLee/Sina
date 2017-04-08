package com.lee.myweibo.utils;

import android.content.Context;

import com.lee.myweibo.Constants;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2017/2/23.
 */

public class TransformUtil {
    public static List<User> toKenToUserList(Context context,List<Oauth2AccessToken> oauth2AccessTokenList){
        final List<User> users=new ArrayList<>();
        for (int i = 0; i < oauth2AccessTokenList.size(); i++) {
            User user=tokenToUser(context,oauth2AccessTokenList.get(i));
            UsersAPI usersAPI=new UsersAPI(context, Constants.APP_KEY,oauth2AccessTokenList.get(i));
            usersAPI.show(oauth2AccessTokenList.get(i).getUid(), new RequestListener() {
                @Override
                public void onComplete(String s) {
                    User user=User.parse(s);
                    users.add(user);

                }

                @Override
                public void onWeiboException(WeiboException e) {

                }
            });
        }
        return users;
    }

    public static User tokenToUser(Context context,Oauth2AccessToken oauth2AccessToken) {
        UsersAPI usersAPI=new UsersAPI(context, Constants.APP_KEY,oauth2AccessToken);
        usersAPI.show(oauth2AccessToken.getUid(), new RequestListener() {
            @Override
            public void onComplete(String s) {
               User user=User.parse(s);

            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });
        return null;
    }
}
