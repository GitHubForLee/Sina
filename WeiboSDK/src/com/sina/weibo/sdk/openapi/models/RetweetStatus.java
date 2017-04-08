package com.sina.weibo.sdk.openapi.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lee on 2017/2/24.
 */

public class RetweetStatus {

    public String id;

    /** 微博信息内容 */
    public String text;

    /** 微博配图地址。多图时返回多图链接。无配图返回"[]" */
    public ArrayList<String> pic_urls;

    /** 微博作者的用户信息字段 */
    public User user;

    public static RetweetStatus parse(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return RetweetStatus.parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static RetweetStatus parse(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }
        RetweetStatus retweetStatus=new RetweetStatus();

        retweetStatus.id          =   jsonObject.optString("id");
        retweetStatus.text        =   jsonObject.optString("test");
        retweetStatus.user        =   User.parse(jsonObject.optJSONObject("user"));


        JSONArray picUrlsArray = jsonObject.optJSONArray("pic_urls");
        if (picUrlsArray != null && picUrlsArray.length() > 0) {
            int length = picUrlsArray.length();
            retweetStatus.pic_urls = new ArrayList<String>(length);
            JSONObject tmpObject = null;
            for (int ix = 0; ix < length; ix++) {
                tmpObject = picUrlsArray.optJSONObject(ix);
                if (tmpObject != null) {
                    retweetStatus.pic_urls.add(tmpObject.optString("thumbnail_pic"));
                }
            }
        }
        return retweetStatus;
    }
}
