package com.misotest.flickrgalleryapp.data.repository.datasource;

import android.content.Context;

import com.misotest.flickrgalleryapp.data.database.PhotosDbCache;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by miomao on 7/26/15.
 */
@Singleton
public class PhotosDataStoreFactory {

    private final Context mContext;
    private final PhotosDbCache photosDbCache;

    @Inject
    public PhotosDataStoreFactory(Context context, PhotosDbCache photosDbCache) {
        if (context == null || photosDbCache == null) {
            throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
        }
        this.mContext = context.getApplicationContext();
        this.photosDbCache = photosDbCache;
    }

    public PhotoDataStore create() {
        PhotoDataStore dataStore;

        if (!this.photosDbCache.isEmpty()) {
            dataStore = new DbPhotoDataStore(photosDbCache);
        } else {
            dataStore = createCloudDataStore();
        }
        return dataStore;
    }

    public PhotoDataStore createCloudDataStore() {
        return new CloudPhotoDataStore(photosDbCache);
    }
}
