package com.misotest.flickrgalleryapp.data.repository.datasource;


import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import com.misotest.flickrgalleryapp.data.database.PhotoFilesTable;
import com.misotest.flickrgalleryapp.data.database.PhotosContentProvider;
import com.misotest.flickrgalleryapp.data.datautils.FileUtils;
import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;

import java.io.File;
import java.util.ArrayList;
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

    private Context mContext;
    private CompositeSubscription subscription = new CompositeSubscription();
    private PhotoDBRepoCallback repositoryDbListCallback;

    /**
     * Constructor for adding context dependency to this module
     *
     * @param context
     */
    public PhotosDbStore(Context context) {
        if (mContext == null) {
            this.mContext = context;
        }
    }

    /**
     * Used to call rest service and download image to device
     */
    private PhotoCloudStore cloudStore = new PhotoCloudStore();

    /**
     * Callback to download images and update database
     */
    private PhotoRestRepoCallback listCallback = new PhotoRestRepoCallback() {
        @Override
        public void onPhotoDataEntityListLoaded(List<PhotoDataEntity> photoDataEntities) {
            if (photoDataEntities != null && !photoDataEntities.isEmpty()) {
                saveDataToDb(photoDataEntities);
            } else {
                repositoryDbListCallback.onPhotoListRetrieved(getPhotoListFromDb());
            }
        }

        @Override
        public void onPhotosDownloaded(List<PhotoDataEntity> photoDataEntities) {
            Observable.from(photoDataEntities)
                    .flatMap(new Func1<PhotoDataEntity, Observable<PhotoDataEntity>>() {
                        @Override
                        public Observable<PhotoDataEntity> call(PhotoDataEntity photoDataEntity) {
                            return Observable.just(photoDataEntity);
                        }
                    })
                    .subscribe(
                            new Action1<PhotoDataEntity>() {
                                @Override
                                public void call(PhotoDataEntity photoDataEntity) {
                                    updatePhotoInDb(photoDataEntity);
                                }
                            },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            },
                            new Action0() {
                                @Override
                                public void call() {
                                    repositoryDbListCallback.onPhotoDbDataSaved(getPhotoListFromDb());
                                }
                            }
                    );
        }

        @Override
        public void onPhotoDownloaded(PhotoDataEntity photoDataEntity) {
            updatePhotoInDb(photoDataEntity);
        }

        @Override
        public void onError(Throwable exception) {
            repositoryDbListCallback.onError(exception);
        }
    };

    /**
     * Get a list of PhotoDataEntity from database
     *
     * @return
     */
    private List<PhotoDataEntity> getPhotoListFromDb() {
        List<PhotoDataEntity> urls = null;
        ContentResolver resolver = mContext.getContentResolver();
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
     * Save retrieved data from rest to database
     *
     * @param dataEntityList
     */
    public void saveDataToDb(final List<PhotoDataEntity> dataEntityList) {
        subscription.add(Observable.from(dataEntityList)
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
                                        mContext.getContentResolver()
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
                                        //todo filter if no change and don't send to presenter
//                                        repositoryDbListCallback.onPhotoDbDataSaved(dataEntityList);
                                        cloudStore.downloadPhotos(filterForDownload(), listCallback);
                                    }
                                }
                        )
        );
    }

    /**
     * Check database and return list with photos that have not been downloaded
     *
     * @return
     */
    private List<PhotoDataEntity> filterForDownload() {
        List<PhotoDataEntity> existing = getPhotoListFromDb();
        List<PhotoDataEntity> toDownload = new ArrayList<>();
        if (existing != null) {
            for (PhotoDataEntity entity : existing) {
                if (entity.photo_file_path.isEmpty()) {
                    toDownload.add(entity);
                }
            }
            existing.clear();
            existing = null;
        }
        return toDownload;
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
        Cursor c = mContext.getContentResolver()
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
    private synchronized void saveOrUpdatePhoto(PhotoDataEntity photoDataEntity) {
        ContentValues values = new ContentValues();
        values.put(PhotoFilesTable.KEY_PHOTO_ID, photoDataEntity.photo_id);
        values.put(PhotoFilesTable.KEY_PHOTO_TITLE, photoDataEntity.photo_title);
        values.put(PhotoFilesTable.KEY_PHOTO_URL, photoDataEntity.photo_url);
        values.put(PhotoFilesTable.KEY_PHOTO_PATH, photoDataEntity.photo_file_path);
        mContext.getContentResolver()
                .update(
                        PhotosContentProvider.CONTENT_URI,
                        values,
                        PhotoFilesTable.KEY_PHOTO_ID + " = " + photoDataEntity.photo_id,
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
            mContext.getContentResolver()
                    .delete(PhotosContentProvider.CONTENT_URI,
                            PhotoFilesTable.KEY_PHOTO_ID + " = " + photoID,
                            null
                    );
        } else {
            repositoryDbListCallback.onError(new Exception("nothing to delete!"));
        }
        repositoryDbListCallback.onPhotoDeleted(photoID);
    }

    /**
     * Update photo row entity in database
     *
     * @param photoDataEntity
     */
    public void updatePhotoInDb(PhotoDataEntity photoDataEntity) {
        if (getPhotoDataEntity(photoDataEntity.photo_id) != null) {
            try {
                saveOrUpdatePhoto(photoDataEntity);
            } catch (Exception e) {
                repositoryDbListCallback.onError(e.fillInStackTrace());
            } finally {
                repositoryDbListCallback.onPhotoUpdated(photoDataEntity);
            }
        }
    }

    /**
     * On app start retrieve data to present if any in db
     *
     * @param repositoryDbListCallback
     */
    public void getPhotosToPresent(int page, String query, PhotoDBRepoCallback repositoryDbListCallback) {
        if (repositoryDbListCallback == null) {
            throw new IllegalArgumentException("callback cannot be null!!!");
        }
        this.repositoryDbListCallback = repositoryDbListCallback;

//        callback.onPhotoListRetrieved(getPhotoListFromDb());
        cloudStore.getPhotoEntityList(page, query, listCallback);
    }

    @Override
    public void getPhotoEntityList(int page, String query, PhotoRestRepoCallback photoRestRepoCallback) {
    }

    @Override
    public void savePhotoEntityList(List<PhotoDataEntity> dataEntityList, PhotoDBRepoCallback repositoryDbListCallback) {
        if (repositoryDbListCallback == null) {
            throw new IllegalArgumentException("callback cannot be null!!!");
        }
        this.repositoryDbListCallback = repositoryDbListCallback;
        saveDataToDb(dataEntityList);
    }

    @Override
    public void deletePhotoFromDb(String photoId, PhotoDBRepoCallback repositoryDbListCallback) {
        if (repositoryDbListCallback == null) {
            throw new IllegalArgumentException("callback cannot be null!!!");
        }
        this.repositoryDbListCallback = repositoryDbListCallback;
        deletePhotoFromDb(photoId);
    }

    @Override
    public void dispose() {
        subscription.unsubscribe();
        cloudStore.dispose();
    }
}
