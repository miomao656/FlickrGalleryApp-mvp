package com.misotest.flickrgalleryapp.domain.interactor;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.repository.PhotoDataRepository;
import com.misotest.flickrgalleryapp.domain.exception.ErrorBundle;
import com.misotest.flickrgalleryapp.domain.repository.IPhotosRepository;

import java.util.List;

/**
 * This class is an implementation of {@link IGetPhotosUseCase} that represents a use case for
 * retrieving a collection of all {@link PhotoDataEntity}.
 */
public class GetPhotosUseCaseImpl implements IGetPhotosUseCase {

    private PhotoDataRepository photoDataRepository = new PhotoDataRepository();

    private UseCaseCallback useCaseCallback;

    private IPhotosRepository.PhotoListCallback repositoryCallback = new IPhotosRepository.PhotoListCallback() {
        @Override
        public void onPhotoListLoaded(List<PhotoDataEntity> photoCollection) {
            notifyGetUserListSuccessfully(photoCollection);
        }

        @Override
        public void onError(ErrorBundle errorBundle) {
            notifyError(errorBundle);
        }
    };

    public GetPhotosUseCaseImpl() {
    }

    private void notifyGetUserListSuccessfully(final List<PhotoDataEntity> photoDataEntityList) {
        useCaseCallback.onPhotoListLoaded(photoDataEntityList);
    }

    private void notifyError(final ErrorBundle errorBundle) {
        useCaseCallback.onError(errorBundle);
    }

    @Override
    public void requestPhotos(int page, String query, UseCaseCallback useCaseCallback) {
        if (useCaseCallback == null) {
            throw new IllegalArgumentException("Interactor useCaseCallback cannot be null!!!");
        }
        this.useCaseCallback = useCaseCallback;
        photoDataRepository.getPhotoList(page, query, repositoryCallback);
    }
}
