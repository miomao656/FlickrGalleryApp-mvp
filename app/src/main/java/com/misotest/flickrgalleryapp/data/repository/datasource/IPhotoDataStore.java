package com.misotest.flickrgalleryapp.data.repository.datasource;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.domain.exception.ErrorBundle;

import java.util.List;

public interface IPhotoDataStore {

    /**
     * Get a collection of {@link PhotoDataEntity}.
     */
    void getPhotoEntityList(int page, String query, PhotoDataEntityListCallback callback);

    /**
     * Callback used for clients to be notified when either a user list has been loaded or any error
     * occurred.
     */
    interface PhotoDataEntityListCallback {
        void onPhotoDataEntityListLoaded(List<PhotoDataEntity> photoDataEntities);

        void onError(ErrorBundle exception);
    }
}
