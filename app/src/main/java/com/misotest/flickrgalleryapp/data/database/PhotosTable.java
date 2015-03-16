package com.misotest.flickrgalleryapp.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class PhotosTable {

    public static final String TABLE_NAME = "photos";

    public static final String KEY_ITEM_ID = BaseColumns._ID;
    public static final String KEY_PHOTO_ID = "photo_id";
    public static final String KEY_PHOTO_DESCRIPTION = "item_description";
    public static final String KEY_FILE_URI_LARGE = "photo_file_small";
    public static final String KEY_FILE_URI_SMALL = "photo_file_large";
    public static final String KEY_PHOTO_URL_SMALL = "photo_url_small";
    public static final String KEY_PHOTO_URL_LARGE = "photo_url_large";

    private static final String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            KEY_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_PHOTO_ID + " TEXT, " +
            KEY_PHOTO_DESCRIPTION + " TEXT, " +
            KEY_FILE_URI_SMALL + " TEXT, " +
            KEY_FILE_URI_LARGE + " TEXT, " +
            KEY_PHOTO_URL_SMALL + " TEXT, " +
            KEY_PHOTO_URL_LARGE + " TEXT)";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_ITEMS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(PhotosTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

}
