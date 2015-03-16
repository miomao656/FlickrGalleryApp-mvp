package com.misotest.flickrgalleryapp.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class PhotoFilesTable {

    public static final String TABLE_NAME = "photos";

    public static final String KEY_ITEM_ID = BaseColumns._ID;
    public static final String KEY_FILE_URI = "photo_file_path";

    private static final String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            KEY_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_FILE_URI + " TEXT)";

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
