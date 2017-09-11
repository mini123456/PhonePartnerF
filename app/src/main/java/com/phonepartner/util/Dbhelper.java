package com.phonepartner.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cwj on 2016/10/18 0018.
 */
public class Dbhelper extends SQLiteOpenHelper {

    private static final String DBNAME="my.db";
    private static final int VERSION=1;


    public Dbhelper(Context context)
    {
        super(context, DBNAME, null, VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
//        String sql = "create TABLE UserInfo(id INTEGER PRIMARY KEY, " +
//                "name VARCHAR(20),pwd VARCHAR(20),uid VARCHAR(20)) ";
//        db.execSQL(sql);
        String sql2 = "create TABLE UserInfo(" +
                "id INTEGER PRIMARY KEY, " +
                "phone VARCHAR(20),pwd VARCHAR(20)," +
                "uid VARCHAR(20),name VARCHAR(20)," +
                "emName VARCHAR(20),emPhone VARCHAR(20)," +
                "photo VARCHAR(20)," +
                "email VARCHAR(20)," +
                "address VARCHAR(20)," +
                "emName2 VARCHAR(20),emPhone2 VARCHAR(20)," +
                "emName3 VARCHAR(20),emPhone3 VARCHAR(20)) ";
        db.execSQL(sql2);
//        String sql1 =
//                "create TABLE FavouriteNEWS(id INTEGER PRIMARY KEY,name VARCHAR(20),title VARCHAR(100),date VARCHAR(20),authorName VARCHAR(20),photo VARCHAR(50),url VARCHAR(50),type VARCHAR(20))";
//        db.execSQL(sql1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
