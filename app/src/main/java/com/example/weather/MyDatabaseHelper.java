package com.example.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_INTERESTCITY= "create table interestCity (cityID text primary key , cityName text)";
    public static final String CREATE_TEMPCITY = "create table tempCity ("
            + "num integer primary key autoincrement,province text, cityName text,weather text,"
            + "cityID text, refreshTime text, temperature text ,humidity text)";
    public static final String CREATE_INDEX= "create table indexNum (num integer primary key )";
    private Context mContext;
    //创建数据库，参数name为数据库名
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    //创建数据库时为数据库建立表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_INTERESTCITY);
        db.execSQL(CREATE_TEMPCITY);
        db.execSQL(CREATE_INDEX);
        //Toast.makeText(mContext,"数据库创建成功",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
