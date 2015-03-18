package com.misotest.flickrgalleryapp.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PhotoFilesTable {

    public static final String TABLE_NAME = "photos";

    public static final String KEY_PHOTO_ID = "photo_id";
    public static final String KEY_PHOTO_TITLE = "photo_title";
    public static final String KEY_PHOTO_URL = "photo_url";
    public static final String KEY_PHOTO_PATH = "photo_file_path";

    private static final String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            KEY_PHOTO_ID + " TEXT PRIMARY KEY UNIQUE, " +
            KEY_PHOTO_TITLE + " TEXT, " +
            KEY_PHOTO_URL + " TEXT, " +
            KEY_PHOTO_PATH + " TEXT)";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_ITEMS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(PhotoFilesTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

}
