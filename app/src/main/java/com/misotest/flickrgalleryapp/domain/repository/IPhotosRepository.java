package com.misotest.flickrgalleryapp.domain.repository;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.domain.PhotoDomainEntity;
import com.misotest.flickrgalleryapp.domain.exception.ErrorBundle;

import java.util.List;

/**
 * Interface that represents a Repository for getting {@link PhotoDataEntity} related data.
 */
public interface IPhotosRepository {
    /**
     * UseCaseCallback used to be notified when either a photo list has been loaded or an error happened.
     */
    interface PhotoListCallback {
        void onPhotoListLoaded(List<PhotoDataEntity> photoCollection);

        void onError(ErrorBundle errorBundle);
    }

    /**
     * Get a collection of {@link PhotoDomainEntity}.
     *
     * @param photoListCallback A {@link PhotoListCallback} used for notifying clients.
     */
    void getPhotoList(int page, String query,PhotoListCallback photoListCallback);
}
