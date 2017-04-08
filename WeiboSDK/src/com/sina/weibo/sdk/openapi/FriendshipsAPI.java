package com.sina.weibo.sdk.openapi;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

/**
 * Created by lee on 2017/2/28.
 */

public class FriendshipsAPI extends AbsOpenAPI{

    private static final String BASE_URL=API_SERVER+"/friendships";
    private static final String FRIENDS_URL=BASE_URL+"/friends.json";
    private static final String fOLLOWERS_URL=BASE_URL+"/followers.json";
    /**
     * 构造函数，使用各个 API 接口提供的服务前必须先获取 Token。
     *
     * @param context
     * @param appKey
     * @param accessToken
     */
    public FriendshipsAPI(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
    }
    public void showFriends(WeiboParameters parameters, RequestListener listener){
        requestAsync(FRIENDS_URL, parameters, HTTPMETHOD_GET, listener);
    }
    public void showFollowers(WeiboParameters parameters,RequestListener listener){
        requestAsync(fOLLOWERS_URL, parameters, HTTPMETHOD_GET, listener);

    }

}
