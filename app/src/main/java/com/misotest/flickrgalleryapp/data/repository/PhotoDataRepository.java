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

    private IPhotoDataStore.PhotoDataRepositoryDbListCallback dbListCallback = new IPhotoDataStore.PhotoDataRepositoryDbListCallback() {
        @Override
        public void onPhotoDataStored(List<PhotoDataEntity> photoDataEntities) {
            photoListCallback.onPhotoListUpdated(photoDataEntities);
        }

        @Override
        public void onPhotoDeleted() {
            photoListCallback.onPhotoDeleted();
        }

        @Override
        public void onError(Throwable exception) {
            photoListCallback.onError(exception);
        }
    };

    private IPhotoDataStore.PhotoDataRepositoryListCallback listCallback = new IPhotoDataStore.PhotoDataRepositoryListCallback() {
        @Override
        public void onPhotoDataEntityListLoaded(List<PhotoDataEntity> photoDataEntities) {
            photosDbStore.savePhotoEntityList(photoDataEntities,
                    new IPhotoDataStore.PhotoDataRepositoryDbListCallback() {
                        @Override
                        public void onPhotoDataStored(List<PhotoDataEntity> photoDataEntities) {
                            photoListCallback.onPhotoListLoaded(photoDataEntities);
                        }

                        @Override
                        public void onPhotoDeleted() {
                            photoListCallback.onPhotoDeleted();
                        }

                        @Override
                        public void onError(Throwable exception) {
                            photoListCallback.onError(exception);
                        }
                    }
            );
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
        if (isOnline) {
            photoCloudStore.getPhotoEntityList(page, query, listCallback);
        }
        photosDbStore.getPhotoEntityList(page, query, dbListCallback);
    }

    public void dispose() {
        photosDbStore.dispose();
        photoCloudStore.dispose();
    }
}
