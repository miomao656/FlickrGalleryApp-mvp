package com.misotest.flickrgalleryapp.data.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * PhotoUri content provider class for interacting with photos database
 * CRUD-e operations
 */
public class PhotosContentProvider extends ContentProvider {

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/photos";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/photo";
    public static final String[] PROJECTION = {PhotoFilesTable.KEY_PHOTO_ID, PhotoFilesTable.KEY_PHOTO_TITLE,
            PhotoFilesTable.KEY_PHOTO_URL, PhotoFilesTable.KEY_PHOTO_PATH};
    // used for the urimatcher
    private static final int DATA = 10;
    private static final int DATA_ID = 20;
    private static final String AUTHORITY = "com.misotest.flickrgalleryapp.photosprovider";
    private static final String BASE_PATH = "photos";
    private static final String URL = "content://" + AUTHORITY + "/" + BASE_PATH;
    public static final Uri CONTENT_URI = Uri.parse(URL);
    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, DATA);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", DATA_ID);
    }

    // database
    private DatabaseHandler database;

    @Override
    public boolean onCreate() {
        database = new DatabaseHandler(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // Check if the caller has requested a column which does not exists
        checkColumns(projection);
        // Set the table
        queryBuilder.setTables(PhotoFilesTable.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case DATA:
                break;
            case DATA_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere(PhotoFilesTable.KEY_PHOTO_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        // int rowsDeleted = 0;
        long id = 0;
        switch (uriType) {
            case DATA:
                id = sqlDB.insert(PhotoFilesTable.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    /**
     * Bulk insert data to database and ignore existing rows matching
     *
     * @param uri
     * @param values
     * @return
     */
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        String table = null;

        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case DATA:
                table = PhotoFilesTable.TABLE_NAME;
                break;
        }
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        sqlDB.beginTransaction();
        try {
            //standard SQL insert statement, that can be reused
            SQLiteStatement insert =
                    sqlDB.compileStatement("insert or ignore into " + table
                            + "(" +
                            PhotoFilesTable.KEY_PHOTO_ID + "," +
                            PhotoFilesTable.KEY_PHOTO_TITLE + "," +
                            PhotoFilesTable.KEY_PHOTO_URL + "," +
                            PhotoFilesTable.KEY_PHOTO_PATH + ")" +
                            " values " + "(?,?,?,?)");

            for (ContentValues value : values) {
                //bind the 1-indexed ?'s to the values specified
                insert.bindString(1, value.getAsString(PhotoFilesTable.KEY_PHOTO_ID));
                insert.bindString(2, value.getAsString(PhotoFilesTable.KEY_PHOTO_TITLE));
                insert.bindString(3, value.getAsString(PhotoFilesTable.KEY_PHOTO_URL));
                insert.bindString(4, value.getAsString(PhotoFilesTable.KEY_PHOTO_PATH));
                insert.execute();
            }
            sqlDB.setTransactionSuccessful();
            numInserted = values.length;
        } finally {
            sqlDB.endTransaction();
        }
        return numInserted;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted;
        switch (uriType) {
            case DATA:
                rowsDeleted = sqlDB.delete(PhotoFilesTable.TABLE_NAME, selection, selectionArgs);
                break;
            case DATA_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(PhotoFilesTable.TABLE_NAME, PhotoFilesTable.KEY_PHOTO_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(PhotoFilesTable.TABLE_NAME, PhotoFilesTable.KEY_PHOTO_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated;
        switch (uriType) {
            case DATA:
                rowsUpdated = sqlDB.update(PhotoFilesTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case DATA_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(PhotoFilesTable.TABLE_NAME, values, PhotoFilesTable.KEY_PHOTO_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(PhotoFilesTable.TABLE_NAME, values, PhotoFilesTable.KEY_PHOTO_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    /**
     * Check for columns in database
     *
     * @param projection
     */
    private void checkColumns(String[] projection) {
        String[] available = {PhotoFilesTable.KEY_PHOTO_ID, PhotoFilesTable.KEY_PHOTO_TITLE, PhotoFilesTable.KEY_PHOTO_URL, PhotoFilesTable.KEY_PHOTO_PATH};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

}
