package com.misotest.flickrgalleryapp.domain.interactor;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.domain.exception.ErrorBundle;

import java.util.List;

public interface IGetPhotosUseCase {

    void requestPhotos(int page, String query, UseCaseCallback useCaseCallback);

    /**
     * UseCaseCallback used to be notified when either a photos collection has been loaded or an error
     * happened.
     */
    interface UseCaseCallback {
        void onPhotoListLoaded(List<PhotoDataEntity> usersCollection);

        void onError(ErrorBundle errorBundle);
    }
}
