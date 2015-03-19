package com.misotest.flickrgalleryapp.domain.interactor;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;

import java.util.List;

/**
 * This interface represents a execution unit for a use case to get a collection of {@link PhotoDataEntity}.
 *
 */
public interface IGetPhotosUseCase {

    void requestPhotos(int page, String query, boolean isOnline, UseCaseCallback useCaseCallback);

    void deletePhoto(String photoId, UseCaseCallback useCaseUseCaseCallback);

    /**
     * UseCaseCallback used to be notified when either a photos collection has been loaded or an error
     * happened.
     */
    interface UseCaseCallback {
        void onPhotoListLoaded(List<PhotoDataEntity> usersCollection);

        void onPhotoDeleted();

        void onError(Throwable errorBundle);

        void onPhotoListUpdated(List<PhotoDataEntity> photoDataEntityList);
    }
}
