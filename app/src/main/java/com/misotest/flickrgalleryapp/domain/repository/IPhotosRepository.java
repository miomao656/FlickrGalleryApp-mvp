package com.misotest.flickrgalleryapp.domain.repository;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.domain.PhotoDomainEntity;
import com.misotest.flickrgalleryapp.domain.exception.ErrorBundle;

import java.util.List;

/**
 * Created by miomao on 3/17/15.
 */
public interface IPhotosRepository {
    /**
     * Callback used to be notified when either a photo list has been loaded or an error happened.
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
