package com.misotest.flickrgalleryapp.data.repository.datasource;

import android.content.ContentResolver;
import android.database.Cursor;

import com.misotest.flickrgalleryapp.MainApplication;
import com.misotest.flickrgalleryapp.data.database.PhotosContentProvider;
import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.presentation.PhotoPresentationModel;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by miomao on 3/17/15.
 */
public class PhotosDbStore implements IPhotoDataStore {

    private CompositeSubscription subscription = new CompositeSubscription();


    private List<PhotoDataEntity> getUriListFromDb() {
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

    private void saveUriListToDb() {
        List<PhotoPresentationModel> urls = null;
        ContentResolver resolver = MainApplication.getContext().getContentResolver();
        String[] projection = PhotosContentProvider.PROJECTION;
//        Cursor cursor = resolver.applyBatch();
    }

    @Override
    public void getPhotoEntityList() {
        List<PhotoDataEntity> entityList = getUriListFromDb();
        if (entityList!=null && !entityList.isEmpty()) {
            MainApplication.getRxBusSingleton().send(getUriListFromDb());
        } else {
            //todo empty list
        }
    }
}
