package com.misotest.flickrgalleryapp.data.repository;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.repository.datasource.IPhotoDataStore;
import com.misotest.flickrgalleryapp.data.repository.datasource.PhotosDbStore;
import com.misotest.flickrgalleryapp.domain.repository.IPhotosRepository;

import java.util.List;

/**
 * Interface that represents a Repository for getting {@link PhotoDataEntity} related data.
 */
public class PhotoDataRepository implements IPhotosRepository {

    private PhotosDbStore photosDbStore = new PhotosDbStore();

    private PhotoListCallback photoListCallback;

    private IPhotoDataStore.PhotoDBRepoCallback dbListCallback = new IPhotoDataStore.PhotoDBRepoCallback() {

        @Override
        public void onPhotoDbDataSaved(final List<PhotoDataEntity> photoDataEntities) {
            if (photoDataEntities != null) {
                photoListCallback.onPhotoListLoaded(photoDataEntities);
            }
        }

        @Override
        public void onPhotoListRetrieved(List<PhotoDataEntity> photoDataEntities) {
            if (photoDataEntities != null) {
                photoListCallback.onPhotoListLoaded(photoDataEntities);
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
        photosDbStore.getPhotosToPresent(page, query, dbListCallback);
    }

    public void dispose() {
        photosDbStore.dispose();
    }
}
