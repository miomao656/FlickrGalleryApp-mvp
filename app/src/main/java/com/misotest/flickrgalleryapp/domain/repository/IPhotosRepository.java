package com.misotest.flickrgalleryapp.domain.repository;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;

import java.util.List;

/**
 * Interface that represents a Repository for getting {@link PhotoDataEntity} related data.
 */
public interface IPhotosRepository {

    void deletePhotoFromDevice(String photoId, PhotoListCallback repositoryCallback);

    /**
     * UseCaseCallback used to be notified when either a photo list has been loaded or an error happened.
     */
    interface PhotoListCallback {
        void onPhotoListLoaded(List<PhotoDataEntity> photoCollection);

        void onPhotoDeleted();

        void onError(Throwable errorBundle);
    }

    /**
     * Get a collection of {@link PhotoDataEntity}.
     *
     * @param photoListCallback A {@link PhotoListCallback} used for notifying clients.
     */
    void getPhotoList(int page, String query,PhotoListCallback photoListCallback);
}
