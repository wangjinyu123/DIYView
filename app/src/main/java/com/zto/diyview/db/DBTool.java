package com.zto.diyview.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wjy on 2018/12/11.
 */

public class DBTool extends SQLiteOpenHelper{
    public DBTool(Context context) {
        super(context, "student", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table student(" +
                "[id]             integer          not null     PRIMARY KEY," +
                "[number]             NVARCHAR(20)          not null     DEFAULT '',"+
                "[name]             NVARCHAR(20)          not null     DEFAULT '',"+
                "[age]             NVARCHAR(20)          not null     DEFAULT ''"+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
