package com.misotest.flickrgalleryapp.domain.repository;


import com.misotest.flickrgalleryapp.domain.entity.PhotoDomainEntity;

import java.util.List;

/**
 * Interface that represents a Repository for getting {@link PhotoDomainEntity} related data.
 */
public interface IPhotosRepository {

    void deletePhotoFromDevice(String photoId, PhotoListCallback repositoryCallback);

    /**
     * Get a collection of {@link PhotoDomainEntity}.
     *
     * @param photoListCallback A {@link PhotoListCallback} used for notifying clients.
     */
    void getPhotoList(int page, String query, boolean isOnline, PhotoListCallback photoListCallback);

    /**
     * UseCaseCallback used to be notified when either a photo list has been loaded or an error happened.
     */
    interface PhotoListCallback {
        void onPhotoListLoaded(List<PhotoDomainEntity> photoCollection);

        void onPhotoListUpdated(List<PhotoDomainEntity> photoDataEntityList);

        void onPhotoDeleted(String photoID);

        void onError(Throwable errorBundle);

        void onPhotoUpdated(PhotoDomainEntity photoDataEntity);
    }

    void dispose();
}
