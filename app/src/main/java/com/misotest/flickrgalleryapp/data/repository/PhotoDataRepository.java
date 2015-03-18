package com.misotest.flickrgalleryapp.data.repository;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.repository.datasource.IPhotoDataStore;
import com.misotest.flickrgalleryapp.data.repository.datasource.PhotoCloudStore;
import com.misotest.flickrgalleryapp.domain.PhotoDomainEntity;
import com.misotest.flickrgalleryapp.domain.exception.ErrorBundle;
import com.misotest.flickrgalleryapp.domain.repository.IPhotosRepository;

import java.util.List;

/**
 * Interface that represents a Repository for getting {@link PhotoDomainEntity} related data.
 */
public class PhotoDataRepository implements IPhotosRepository {

    @Override
    public void getPhotoList(int page, String query, final PhotoListCallback photoListCallback) {
        //we always get all users from the cloud
        if (photoListCallback == null) {
            throw new IllegalArgumentException("Interactor callback cannot be null!!!");
        }
        final PhotoCloudStore photoCloudStore = new PhotoCloudStore();
        photoCloudStore.getPhotoEntityList(page, query,
                new IPhotoDataStore.PhotoDataRepositoryListCallback() {
                    @Override
                    public void onPhotoDataEntityListLoaded(List<PhotoDataEntity> photoDataEntities) {
                        photoListCallback.onPhotoListLoaded(photoDataEntities);
                    }

                    @Override
                    public void onError(ErrorBundle exception) {
                        photoListCallback.onError(exception);
                    }
                }
        );
    }
}
