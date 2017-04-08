package com.lee.myweibo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lee on 2017/2/22.
 */

public class OpenHelper extends SQLiteOpenHelper {
    public static final String CREATE_TABLE="create table Token ("
            +"id integer primary key autoincrement,"
            +"uid text,"
            +"access_token text,"
            +"refresh_token text,"
            +"expires_in integer )";
    public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
