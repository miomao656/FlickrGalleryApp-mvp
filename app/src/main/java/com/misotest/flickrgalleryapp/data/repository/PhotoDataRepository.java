package com.misotest.flickrgalleryapp.data.repository;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.repository.datasource.IPhotoDataStore;
import com.misotest.flickrgalleryapp.data.repository.datasource.PhotoCloudStore;
import com.misotest.flickrgalleryapp.data.repository.datasource.PhotosDbStore;
import com.misotest.flickrgalleryapp.domain.repository.IPhotosRepository;

import java.util.List;

/**
 * Interface that represents a Repository for getting {@link PhotoDataEntity} related data.
 */
public class PhotoDataRepository implements IPhotosRepository {

    private PhotoCloudStore photoCloudStore = new PhotoCloudStore();

    private PhotosDbStore photosDbStore = new PhotosDbStore();

    private PhotoListCallback photoListCallback;

    private int page;
    private String query;
    private boolean isOnline;

    private IPhotoDataStore.PhotoDBRepoCallback dbListCallback = new IPhotoDataStore.PhotoDBRepoCallback() {

        @Override
        public void onPhotoDbDataSaved(final List<PhotoDataEntity> photoDataEntities) {
            if (photoDataEntities != null) {
                photoListCallback.onPhotoListUpdated(photoDataEntities);
            }
        }

        @Override
        public void onPhotoListRetrieved(List<PhotoDataEntity> photoDataEntities) {
            if (photoDataEntities != null) {
                photoListCallback.onPhotoListLoaded(photoDataEntities);
            }
            if (isOnline) {
                photoCloudStore.getPhotoEntityList(page, query, listCallback);
            }
        }

        @Override
        public void onPhotoDeleted(String photoID) {
            photoListCallback.onPhotoDeleted(photoID);
        }

        @Override
        public void onError(Throwable exception) {
            photoListCallback.onError(exception);
        }

        @Override
        public void onPhotoUpdated(PhotoDataEntity photoDataEntity) {
            photoListCallback.onPhotoUpdated(photoDataEntity);
        }
    };
    private IPhotoDataStore.PhotoRestRepoCallback listCallback = new IPhotoDataStore.PhotoRestRepoCallback() {
        @Override
        public void onPhotoDataEntityListLoaded(List<PhotoDataEntity> photoDataEntities) {
            photosDbStore.savePhotoEntityList(photoDataEntities, dbListCallback);
        }

        @Override
        public void onPhotoDownloaded(PhotoDataEntity photoDataEntity) {
            photosDbStore.updatePhotoInDb(photoDataEntity);
        }

        @Override
        public void onError(Throwable exception) {
            photoListCallback.onError(exception);
        }
    };

    @Override
    public void deletePhotoFromDevice(String photoId, final PhotoListCallback photoListCallback) {
        if (photoListCallback == null) {
            throw new IllegalArgumentException("Callback cannot be null!!!");
        }
        this.photoListCallback = photoListCallback;
        if (photoId != null && !photoId.isEmpty()) {
            photosDbStore.deletePhotoFromDb(photoId, dbListCallback);
        }
    }

    @Override
    public void getPhotoList(int page, String query, boolean isOnline, final PhotoListCallback photoListCallback) {
        //we always get all users from the cloud
        if (photoListCallback == null) {
            throw new IllegalArgumentException("Callback cannot be null!!!");
        }
        this.photoListCallback = photoListCallback;
        this.page = page;
        this.query = query;
        this.isOnline = isOnline;
        photosDbStore.getPhotosToPresent(dbListCallback);
    }

    public void dispose() {
        photosDbStore.dispose();
        photoCloudStore.dispose();
    }
}
