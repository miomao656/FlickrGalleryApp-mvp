package com.misotest.flickrgalleryapp.data.database;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import com.misotest.flickrgalleryapp.data.datautils.FileUtils;
import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by miomao on 7/26/15.
 */
@Singleton
public class PhotoDbCacheImpl implements PhotosDbCache {

    private final Context mContext;
    private CompositeSubscription subscription = new CompositeSubscription();

    @Inject
    public PhotoDbCacheImpl(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public Observable<PhotoDataEntity> get(String photoId) {
        return Observable.create(subscriber -> {
            PhotoDataEntity dataEntity = getPhotoDataEntity(photoId);
            if (dataEntity != null) {
                subscriber.onNext(dataEntity);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new Throwable("No photo in db!"));
            }
        });
    }

    @Override
    public Observable<List<PhotoDataEntity>> getList() {
        return Observable.create(new Observable.OnSubscribe<List<PhotoDataEntity>>() {
            @Override
            public void call(Subscriber<? super List<PhotoDataEntity>> subscriber) {
                List<PhotoDataEntity> entities = getPhotoListFromDb();
                if (entities != null && entities.size() > 0) {
                    subscriber.onNext(entities);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Throwable("No photos in db!"));
                }
            }
        });
    }

    @Override
    public void put(PhotoDataEntity photoDataEntity) {
        saveOrUpdatePhoto(photoDataEntity);
    }

    @Override
    public void putList(List<PhotoDataEntity> photoDataEntities) {
        saveDataToDb(photoDataEntities);
    }

    @Override
    public boolean isCached(String photoId) {
        return getPhotoDataEntity(photoId) != null;
    }

    @Override
    public void evictAll() {

    }

    @Override
    public boolean isEmpty() {
        return getPhotoListFromDb().isEmpty();
    }

    /**
     * Save retrieved data from rest to database
     *
     * @param dataEntityList
     */
    public void saveDataToDb(final List<PhotoDataEntity> dataEntityList) {
        subscription.add(Observable.from(dataEntityList)
                        .flatMap(Observable::just)
                        .map(uri -> {
                            ContentValues values = new ContentValues();
                            values.put(PhotoFilesTable.KEY_PHOTO_ID, uri.photo_id);
                            values.put(PhotoFilesTable.KEY_PHOTO_TITLE, uri.photo_title);
                            values.put(PhotoFilesTable.KEY_PHOTO_URL, uri.photo_url);
                            values.put(PhotoFilesTable.KEY_PHOTO_PATH, uri.photo_file_path);
                            return values;
                        })
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                contentValues -> mContext.getContentResolver()
                                        .bulkInsert(PhotosContentProvider.CONTENT_URI,
                                                contentValues.toArray(new ContentValues[contentValues.size()])
                                        ),
                                throwable -> {
                                    throwable.printStackTrace();
//                                    repositoryDbListCallback.onError(throwable);
                                },
                                () -> {
                                    //todo filter if no change and don't send to presenter
//                                        repositoryDbListCallback.onPhotoDbDataSaved(dataEntityList);
//                                    cloudStore.downloadPhotos(filterForDownload(), listCallback);
                                }
                        )
        );
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
                urls = new ArrayList<>(cursor.getCount());
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
        }
        return toDownload;
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
//            repositoryDbListCallback.onError(new Exception("nothing to delete!"));
        }
//        repositoryDbListCallback.onPhotoDeleted(photoID);
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
//                repositoryDbListCallback.onError(e.fillInStackTrace());
            } finally {
//                repositoryDbListCallback.onPhotoUpdated(photoDataEntity);
            }
        }
    }

}
