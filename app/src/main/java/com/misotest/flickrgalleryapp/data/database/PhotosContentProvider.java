package com.misotest.flickrgalleryapp.data.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * PhotoUri content provider class for interacting with photos database
 * CRUD-e operations
 */
public class PhotosContentProvider extends ContentProvider {

    private static long startTime;
    private static long endTime;

    // database
    private DatabaseHandler database;

    // used for the urimatcher
    private static final int DATA = 10;
    private static final int DATA_ID = 20;

    private static final String AUTHORITY = "com.misotest.flickrgalleryapp.photosprovider";
    private static final String BASE_PATH = "photos";
    private static final String URL = "content://" + AUTHORITY + "/" + BASE_PATH;


    public static final Uri CONTENT_URI = Uri.parse(URL);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/photos";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/photo";
    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, DATA);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", DATA_ID);
    }

    public static final String[] PROJECTION = {PhotoFilesTable.KEY_ITEM_ID, PhotoFilesTable.KEY_FILE_URI};

    @Override
    public boolean onCreate() {
        database = new DatabaseHandler(getContext());
        return false;
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
                queryBuilder.appendWhere(PhotoFilesTable.KEY_ITEM_ID + "=" + uri.getLastPathSegment());
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

//    @Override
//    public int bulkInsert(Uri uri, ContentValues[] values) {
//        final SQLiteDatabase db = database.getWritableDatabase();
//        final int match = sURIMatcher.match(uri);
//        int numInserted = 0;
//        switch (match) {
//            case DATA:
//                db.beginTransaction();
//                try {
//                    // standard SQL insert statement, that can be reused
//                    SQLiteStatement insert = db.compileStatement("insert or replace into "
//                            + PhotosTable.TABLE_NAME + "("
////                            + PhotosTable.KEY_ITEM_ID + ","
//                            + PhotosTable.KEY_PHOTO_ID + ","
//                            + PhotosTable.KEY_PHOTO_DESCRIPTION + ","
//                            + PhotosTable.KEY_FILE_URI_SMALL + ","
//                            + PhotosTable.KEY_FILE_URI_LARGE + ","
//                            + PhotosTable.KEY_PHOTO_URL_SMALL + ","
//                            + PhotosTable.KEY_PHOTO_URL_LARGE
//                            + ")" + " values " + "(?,?,?,?,?,?)");
//
//                    startTime = Calendar.getInstance().getTimeInMillis();
//                    ContentValues value;
//                    for (ContentValues value1 : values) {
//                        value = value1;
//                        // bind the 1-indexed ?'s to the values specified
////                        insert.bindLong(1, value.getAsLong(PhotosTable.KEY_ITEM_ID));
//                        insert.bindString(2, value.getAsString(PhotosTable.KEY_PHOTO_ID));
//                        insert.bindString(3, value.getAsString(PhotosTable.KEY_PHOTO_DESCRIPTION));
//                        insert.bindString(4, value.getAsString(PhotosTable.KEY_FILE_URI_SMALL));
//                        insert.bindString(5, value.getAsString(PhotosTable.KEY_FILE_URI_LARGE));
//                        insert.bindString(6, value.getAsString(PhotosTable.KEY_PHOTO_URL_SMALL));
//                        insert.bindString(7, value.getAsString(PhotosTable.KEY_PHOTO_URL_LARGE));
//                        insert.run();
//                    }
//                    endTime = Calendar.getInstance().getTimeInMillis();
//                    System.out.println("FOR LOOP TIME!! = " + (endTime - startTime) + " ms");
//                    db.setTransactionSuccessful();
//                    numInserted = values.length;
//                } catch (SQLException ex) {
//                    Log.e("LOG_TAG", "There was a problem with the bulk insert: ");
//                } finally {
//                    db.endTransaction();
//                    db.close();
//                    getContext().getContentResolver().notifyChange(uri, null);
//                }
//                break;
//            default:
//                throw new UnsupportedOperationException("unsupported uri: " + uri);
//        }
//        return numInserted;
//    }

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
            for (ContentValues cv : values) {
                long newID = sqlDB.insertOrThrow(table, null, cv);
                if (newID <= 0) {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            }
            sqlDB.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
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
                    rowsDeleted = sqlDB.delete(PhotoFilesTable.TABLE_NAME, PhotoFilesTable.KEY_ITEM_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(PhotoFilesTable.TABLE_NAME, PhotoFilesTable.KEY_ITEM_ID + "=" + id + " and " + selection, selectionArgs);
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
                    rowsUpdated = sqlDB.update(PhotoFilesTable.TABLE_NAME, values, PhotoFilesTable.KEY_ITEM_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(PhotoFilesTable.TABLE_NAME, values, PhotoFilesTable.KEY_ITEM_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {PhotoFilesTable.KEY_ITEM_ID, PhotoFilesTable.KEY_FILE_URI};
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
