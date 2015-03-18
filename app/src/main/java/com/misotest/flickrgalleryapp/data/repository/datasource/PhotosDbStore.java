package com.misotest.flickrgalleryapp.data.repository.datasource;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.misotest.flickrgalleryapp.MainApplication;
import com.misotest.flickrgalleryapp.data.database.PhotoFilesTable;
import com.misotest.flickrgalleryapp.data.database.PhotosContentProvider;
import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.presentation.PhotoPresentationModel;

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
 * Created by miomao on 3/17/15.
 */
public class PhotosDbStore implements IPhotoDataStore {

    private CompositeSubscription subscription = new CompositeSubscription();
    private PhotoDataRepositoryListCallback repositoryListCallback;

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
                    new PhotoDataEntity(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4)
                    );
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return urls;
    }

    private List<PhotoPresentationModel> getUriPresentationListFromDb() {
        List<PhotoPresentationModel> urls = Collections.emptyList();
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
                urls = new ArrayList<PhotoPresentationModel>(cursor.getCount());
                do {
                    urls.add(new PhotoPresentationModel(cursor.getInt(0),
                                    cursor.getString(1),
                                    cursor.getString(2),
                                    cursor.getString(3),
                                    cursor.getString(4)
                            )
                    );
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return urls;
    }

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
                                if (isDownloaded) {
                                    values.put(PhotoFilesTable.KEY_PHOTO_PATH, uri.file_path);
                                }
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
//                                        repositoryListCallback.onError();
                                    }
                                },
                                new Action0() {
                                    @Override
                                    public void call() {
                                        repositoryListCallback.onPhotoDataEntityListLoaded(getPhotoListFromDb());
                                    }
                                }
                        )
        );
    }

    private void saveUriListToDb() {
        List<PhotoPresentationModel> urls = null;
        ContentResolver resolver = MainApplication.getContext().getContentResolver();
        String[] projection = PhotosContentProvider.PROJECTION;
//        Cursor cursor = resolver.applyBatch();
    }

    @Override
    public void getPhotoEntityList(int page, String query, PhotoDataRepositoryListCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Interactor callback cannot be null!!!");
        }
        this.repositoryListCallback = callback;
    }
}
