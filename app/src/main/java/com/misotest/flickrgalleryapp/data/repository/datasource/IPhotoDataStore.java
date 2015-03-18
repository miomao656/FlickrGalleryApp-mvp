package com.misotest.flickrgalleryapp.data.repository.datasource;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;

import java.util.List;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface IPhotoDataStore {

    /**
     * Get a collection of {@link PhotoDataEntity}.
     */
    void getPhotoEntityList(int page, String query, PhotoDataRepositoryListCallback callback);

    /**
     * Save a collection of {@link PhotoDataEntity}.
     */
    void savePhotoEntityList(List<PhotoDataEntity> dataEntityList, PhotoDataRepositoryDbListCallback callback);

    /**
     * When search term not provided this is used
     */
    static final String DEFAULT_SEARCH_THERM = "akita";

    /**
     * UseCaseCallback used for clients to be notified when either a user list has been loaded or any error
     * occurred.
     */
    interface PhotoDataRepositoryListCallback {
        void onPhotoDataEntityListLoaded(List<PhotoDataEntity> photoDataEntities);

        void onError(Throwable exception);
    }

    /**
     * UseCaseCallback used for clients to be notified when either a user list has been loaded or any error
     * occurred.
     */
    interface PhotoDataRepositoryDbListCallback {
        void onPhotoDataStored(List<PhotoDataEntity> photoDataEntities);

        void onError(Throwable exception);
    }
}
