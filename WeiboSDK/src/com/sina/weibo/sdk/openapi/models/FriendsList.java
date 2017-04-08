package com.sina.weibo.sdk.openapi.models;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lee on 2017/2/28.
 */

public class FriendsList {
    public ArrayList<User> users;
    public int total_number;
    public static FriendsList parse(String jsonString){
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        FriendsList friendsList=new FriendsList();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            friendsList.total_number      =    jsonObject.optInt("total_number",0);

            JSONArray array               =    jsonObject.optJSONArray("users");
            if (array != null && array.length() > 0) {
                int length = array.length();
                friendsList.users = new ArrayList<User>(length);
                for (int ix = 0; ix < length; ix++) {
                    friendsList.users.add(User.parse(array.optJSONObject(ix)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friendsList;
    }

}
