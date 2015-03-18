package com.misotest.flickrgalleryapp.domain.interactor;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.domain.exception.ErrorBundle;

import java.util.List;

public interface IGetPhotosUseCase extends UseCase {

//    public void requestPhotos(int page, String query, Callback callback);

    public void unregister();

    public void requestPhotos(int page, String query, Callback callback);

    /**
     * Callback used to be notified when either a photos collection has been loaded or an error
     * happened.
     */
    interface Callback {
        void onPhotoListLoaded(List<PhotoDataEntity> usersCollection);

        void onError(ErrorBundle errorBundle);
    }

}
