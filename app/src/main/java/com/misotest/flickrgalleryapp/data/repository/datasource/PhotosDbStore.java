package com.misotest.flickrgalleryapp.data.repository.datasource;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;

import com.misotest.flickrgalleryapp.MainApplication;
import com.misotest.flickrgalleryapp.data.database.PhotoFilesTable;
import com.misotest.flickrgalleryapp.data.database.PhotosContentProvider;
import com.misotest.flickrgalleryapp.data.datautils.FileUtils;
import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.presentation.entity.PhotoPresentationModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * {@link IPhotoDataStore} implementation based on database data store.
 */
public class PhotosDbStore implements IPhotoDataStore {

    private CompositeSubscription subscription = new CompositeSubscription();
    private PhotoDataRepositoryDbListCallback repositoryDbListCallback;

    /**
     * Get a list of PhotoDataEntity from database
     *
     * @return
     */
    private List<PhotoDataEntity> getPhotoListFromDb() {
        List<PhotoDataEntity> urls = null;
        ContentResolver resolver = MainApplication.getContext().getContentResolver();
        String[] projection = PhotosContentProvider.PROJECTION;
        Cursor cursor =
                resolver.query(PhotosContentProvider.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                urls = new ArrayList<PhotoDataEntity>(cursor.getCount());
                do {
                    urls.add(new PhotoDataEntity(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3)
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return urls;
    }

    /**
     * Get a list of PhotoPresentationModel from database
     *
     * @return
     */
    private List<PhotoPresentationModel> getUriPresentationListFromDb() {
        List<PhotoPresentationModel> urls = Collections.emptyList();
        ContentResolver resolver = MainApplication.getContext().getContentResolver();
        Cursor cursor =
                resolver.query(PhotosContentProvider.CONTENT_URI,
                        PhotosContentProvider.PROJECTION,
                        null,
                        null,
                        null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                urls = new ArrayList<PhotoPresentationModel>(cursor.getCount());
                do {
                    urls.add(new PhotoPresentationModel(cursor.getString(0),
                                    cursor.getString(1),
                                    cursor.getString(2),
                                    cursor.getString(3)
                            )
                    );
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return urls;
    }

    /**
     * Save retrieved data from rest to database
     *
     * @param uriList
     * @param isDownloaded
     */
    public void saveDataToDb(List<PhotoDataEntity> uriList, final boolean isDownloaded) {
        subscription.add(Observable.from(uriList)
                        .flatMap(new Func1<PhotoDataEntity, Observable<PhotoDataEntity>>() {
                            @Override
                            public Observable<PhotoDataEntity> call(PhotoDataEntity photoDomainEntity) {
                                return Observable.just(photoDomainEntity);
                            }
                        })
                        .map(new Func1<PhotoDataEntity, ContentValues>() {
                            @Override
                            public ContentValues call(PhotoDataEntity uri) {
                                ContentValues values = new ContentValues();
                                values.put(PhotoFilesTable.KEY_PHOTO_ID, uri.photo_id);
                                values.put(PhotoFilesTable.KEY_PHOTO_TITLE, uri.photo_title);
                                values.put(PhotoFilesTable.KEY_PHOTO_URL, uri.photo_url);
                                values.put(PhotoFilesTable.KEY_PHOTO_PATH, uri.photo_file_path);
                                return values;
                            }
                        })
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<List<ContentValues>>() {
                                    @Override
                                    public void call(List<ContentValues> contentValues) {
                                        MainApplication.getContext().getContentResolver()
                                                .bulkInsert(PhotosContentProvider.CONTENT_URI,
                                                        contentValues.toArray(new ContentValues[contentValues.size()])
                                                );
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
                                        repositoryDbListCallback.onError(throwable);
                                    }
                                },
                                new Action0() {
                                    @Override
                                    public void call() {
                                        repositoryDbListCallback.onPhotoDataStored(getPhotoListFromDb());
                                    }
                                }
                        )
        );
    }

    /**
     * Retrieves PhotoDataEntity from database
     *
     * @param photo_id
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private PhotoDataEntity getPhotoDataEntity(String photo_id) {
        String selection = PhotoFilesTable.KEY_PHOTO_ID + " = '"
                + photo_id + "'";
        PhotoDataEntity dataEntity = null;
        Cursor c = MainApplication.getContext().getContentResolver()
                .query(
                        PhotosContentProvider.CONTENT_URI,
                        PhotosContentProvider.PROJECTION,
                        selection,
                        null,
                        null,
                        null
                );
        if (c != null) {
            if (c.moveToFirst()) {
                dataEntity = new PhotoDataEntity(
                        c.getString(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3)
                );
            }
            c.close();
        }
        return dataEntity;
    }

    /**
     * Saves file path to the Photo database row
     *
     * @param photoDataEntity
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void saveOrUpdatePhoto(PhotoDataEntity photoDataEntity) {
        ContentValues values = new ContentValues();
        values.put(PhotoFilesTable.KEY_PHOTO_ID, photoDataEntity.photo_id);
        values.put(PhotoFilesTable.KEY_PHOTO_TITLE, photoDataEntity.photo_title);
        values.put(PhotoFilesTable.KEY_PHOTO_URL, photoDataEntity.photo_url);
        values.put(PhotoFilesTable.KEY_PHOTO_PATH, photoDataEntity.photo_file_path);
        MainApplication.getContext().getContentResolver()
                .update(
                        PhotosContentProvider.CONTENT_URI,
                        values,
                        PhotoFilesTable.KEY_PHOTO_ID + "=" + photoDataEntity.photo_id,
                        null
                );
    }

    /**
     * Deletes a row in photos table and file from the device if it exists
     *
     * @param photoID
     */
    private void deletePhotoFromDb(String photoID) {
        PhotoDataEntity entity = getPhotoDataEntity(photoID);
        if (entity != null) {
            if (FileUtils.isExistingFile(entity.photo_file_path)) {
                FileUtils.deleteFile(new File(entity.photo_file_path));
            }
            MainApplication.getContext().getContentResolver()
                    .delete(PhotosContentProvider.CONTENT_URI,
                            PhotoFilesTable.KEY_PHOTO_ID + " = " + photoID,
                            null
                    );
        } else {
            repositoryDbListCallback.onError(new Exception("nothing to delete!"));
        }
        repositoryDbListCallback.onPhotoDeleted();
    }

    @Override
    public void getPhotoEntityList(int page, String query, PhotoDataRepositoryListCallback callback) {

    }

    @Override
    public void savePhotoEntityList(List<PhotoDataEntity> dataEntityList, PhotoDataRepositoryDbListCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Interactor callback cannot be null!!!");
        }
        this.repositoryDbListCallback = callback;
        saveDataToDb(dataEntityList, false);
    }

    @Override
    public void deletePhotoFromDb(String photoId, PhotoDataRepositoryDbListCallback photoDataRepositoryDbListCallback) {
        if (photoDataRepositoryDbListCallback == null) {
            throw new IllegalArgumentException("Interactor callback cannot be null!!!");
        }
        this.repositoryDbListCallback = photoDataRepositoryDbListCallback;
        deletePhotoFromDb(photoId);
    }
}
