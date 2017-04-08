package com.lee.myweibo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2017/2/22.
 */

public class DbHelper {
    private static final String TABLE_NAME="Token";
   private SQLiteDatabase db;
    private OpenHelper openHelper;
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        openHelper=new OpenHelper(context,name,factory,version);
        db=openHelper.getWritableDatabase();
    }
    public List<Oauth2AccessToken> getTokenList(){
        List<Oauth2AccessToken> list =new ArrayList<>();
        Cursor cursor=db.query(TABLE_NAME,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            String uid=cursor.getString(cursor.getColumnIndex("uid"));
            String access_token=cursor.getString(cursor.getColumnIndex("access_token"));
            String refresh_token=cursor.getString(cursor.getColumnIndex("refresh_token"));
            long expires_in=cursor.getLong(cursor.getColumnIndex("expires_in"));

            Log.e("tokeinfo duchuqian",String.valueOf(expires_in));

            Oauth2AccessToken token=new Oauth2AccessToken();
            token.setUid(uid);
            token.setExpiresIn(String.valueOf(expires_in));
            token.setRefreshToken(refresh_token);
            token.setToken(access_token);
            if(token.isSessionValid()){
                Log.e("test","可用token");
                list.add(token);
            }else {
                Log.e("test","不可用token");

                deleteToken(token);
            }
            Log.e("token", "" + token.getExpiresTime() );
        }
        cursor.close();
        return list;
    }
    public void addToken(Oauth2AccessToken oauth2AccessToken){
        db.insert(TABLE_NAME, null,getContentValues(oauth2AccessToken));
    }
    public void updateToken(Oauth2AccessToken oauth2AccessToken){
        db.update(TABLE_NAME,getContentValues(oauth2AccessToken),"uid=?",new String[]{oauth2AccessToken.getUid()});
    }

    public boolean isExist(String uid){
        Cursor cursor=db.query(TABLE_NAME,null,"uid=?",new String[]{uid},null,null,null);
        int cnt=cursor.getCount();
        cursor.close();
        return cnt!=0;
    }

    public void deleteToken(Oauth2AccessToken oauth2AccessToken){
        db.delete(TABLE_NAME,"uid=?",new String[]{oauth2AccessToken.getUid()});
    }
    private ContentValues getContentValues(Oauth2AccessToken oauth2AccessToken){
        String uid=oauth2AccessToken.getUid();
        String access_token=oauth2AccessToken.getToken();
        String refresh_token=oauth2AccessToken.getRefreshToken();
        long expires_in=oauth2AccessToken.getExpiresTime();
        ContentValues values=new ContentValues();
        values.put("expires_in",expires_in);
        values.put("access_token",access_token);
        values.put("refresh_token",refresh_token);
        values.put("uid",uid);
        return values;
    }

}
