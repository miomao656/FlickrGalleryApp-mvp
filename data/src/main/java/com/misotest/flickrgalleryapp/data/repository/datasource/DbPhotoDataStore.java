package com.misotest.flickrgalleryapp.data.repository.datasource;

import com.misotest.flickrgalleryapp.data.database.PhotosDbCache;
import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;

import java.util.List;

import rx.Observable;

/**
 * Created by miomao on 7/26/15.
 */
public class DbPhotoDataStore implements PhotoDataStore {

    private final PhotosDbCache photosDbCache;

    public DbPhotoDataStore(PhotosDbCache photosDbCache) {
        this.photosDbCache = photosDbCache;
    }

    @Override
    public Observable<List<PhotoDataEntity>> photos() {
        return this.photosDbCache.getList();
    }

    @Override
    public Observable<PhotoDataEntity> photo() {
        throw new UnsupportedOperationException("Operation is not available!!!");
    }
}
