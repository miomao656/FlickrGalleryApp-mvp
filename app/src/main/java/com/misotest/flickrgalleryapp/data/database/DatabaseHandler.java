package com.misotest.flickrgalleryapp.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "flickrApp.db";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        PhotosTable.onCreate(db);
    }

    // upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        PhotosTable.onUpgrade(db, oldVersion, newVersion);
    }

}
