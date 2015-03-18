package com.misotest.flickrgalleryapp.data.repository.datasource;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.domain.exception.ErrorBundle;

import java.util.List;

public interface IPhotoDataStore {

    /**
     * Get a collection of {@link PhotoDataEntity}.
     */
    void getPhotoEntityList(int page, String query, PhotoDataRepositoryListCallback callback);

    static final String DEFAULT_SEARCH_THERM = "akita";

    /**
     * UseCaseCallback used for clients to be notified when either a user list has been loaded or any error
     * occurred.
     */
    interface PhotoDataRepositoryListCallback {
        void onPhotoDataEntityListLoaded(List<PhotoDataEntity> photoDataEntities);

        void onError(ErrorBundle exception);
    }
}
