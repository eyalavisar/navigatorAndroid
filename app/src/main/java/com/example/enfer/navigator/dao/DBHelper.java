package com.example.enfer.navigator.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLACES_TABLE = "CREATE TABLE " + DBConstants.TABLE_PLACES + "("
                + DBConstants._ID + " INTEGER PRIMARY KEY," + DBConstants.CITY + " TEXT,"+ DBConstants.NAME + " TEXT,"
                + DBConstants.ADDRESS + " TEXT," + DBConstants.DISTANCE + " INTEGER," + DBConstants.LATITUDE + " TEXT,"
                + DBConstants.LONGITUDE + ")";
        db.execSQL(CREATE_PLACES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_PLACES);

        // Create tables again
        onCreate(db);
    }

}
