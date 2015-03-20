package com.misotest.flickrgalleryapp.data.repository.datasource;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;

import java.util.List;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface IPhotoDataStore {

    /**
     * When search term not provided this is used
     */
    static final String DEFAULT_SEARCH_THERM = "akita";
    /**
     * Flickr rest api access key
     */
    static final String FLICKR_API_KEY = "90a9eddb63cbe3de1359aaf0e70778aa";
    /**
     * Flickr rest api response format
     */
    static final String FLICKR_FORMAT = "json";
    /**
     * Flickr rest api privacy filter off
     */
    static final int NO_PRIVACY_FILTER = 1;
    /**
     * Flickr rest api jsonp response off
     */
    static final int NO_JSONP_RESPONSE = 1;
    /**
     * Flickr rest api chosen image size
     */
    static final String IMAGE_SIZE = "Large";
    /**
     * Flickr rest api number of responses per page
     */
    static final int PHOTO_PER_PAGE = 50;

    /**
     * Get a collection of {@link PhotoDataEntity}.
     */
    void getPhotoEntityList(int page, String query, PhotoRestRepoCallback photoRestRepoCallback);

    /**
     * Save a collection of {@link PhotoDataEntity}.
     */
    void savePhotoEntityList(List<PhotoDataEntity> dataEntityList, PhotoDBRepoCallback callback);

    void deletePhotoFromDb(String photoId, PhotoDBRepoCallback photoDBRepoCallback);

    /**
     * Cancel all running operations
     */
    void dispose();

    /**
     * UseCaseCallback used for clients to be notified when either a user list has been loaded or any error
     * occurred.
     */
    interface PhotoRestRepoCallback {
        void onPhotoDataEntityListLoaded(List<PhotoDataEntity> photoDataEntities);

        void onPhotoDownloaded(PhotoDataEntity photoDataEntity);

        void onError(Throwable exception);
    }

    /**
     * UseCaseCallback used for clients to be notified when either a user list has been loaded or any error
     * occurred.
     */
    interface PhotoDBRepoCallback {

        void onPhotoDbDataSaved(List<PhotoDataEntity> photoDataEntities);

        void onPhotoListRetrieved(List<PhotoDataEntity> photoDataEntities);

        void onPhotoDeleted(String photoID);

        void onError(Throwable exception);

        void onPhotoUpdated(PhotoDataEntity photo_id);
    }
}
