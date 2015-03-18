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

    final PhotoCloudStore photoCloudStore = new PhotoCloudStore();
    final PhotosDbStore photosDbStore = new PhotosDbStore();

    @Override
    public void getPhotoList(int page, String query, final PhotoListCallback photoListCallback) {
        //we always get all users from the cloud
        if (photoListCallback == null) {
            throw new IllegalArgumentException("Interactor callback cannot be null!!!");
        }
        photoCloudStore.getPhotoEntityList(page, query,
                new IPhotoDataStore.PhotoDataRepositoryListCallback() {
                    @Override
                    public void onPhotoDataEntityListLoaded(List<PhotoDataEntity> photoDataEntities) {
//                        photoListCallback.onPhotoListLoaded(photoDataEntities);
                        photosDbStore.savePhotoEntityList(photoDataEntities,
                                new IPhotoDataStore.PhotoDataRepositoryDbListCallback() {
                                    @Override
                                    public void onPhotoDataStored(List<PhotoDataEntity> photoDataEntities) {
                                        photoListCallback.onPhotoListLoaded(photoDataEntities);
                                    }

                                    @Override
                                    public void onError(Throwable exception) {
                                        photoListCallback.onError(exception);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(Throwable exception) {
                        photoListCallback.onError(exception);
                    }
                }
        );
    }
}
