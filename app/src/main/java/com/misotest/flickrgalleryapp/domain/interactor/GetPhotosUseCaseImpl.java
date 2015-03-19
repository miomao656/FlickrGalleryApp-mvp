package com.misotest.flickrgalleryapp.domain.interactor;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.repository.PhotoDataRepository;
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
            notifyGetPhotoListSuccessfully(photoCollection);
        }

        @Override
        public void onPhotoListUpdated(List<PhotoDataEntity> photoDataEntityList) {
            useCaseCallback.onPhotoListUpdated(photoDataEntityList);
        }

        @Override
        public void onPhotoDeleted(String photoID) {
            useCaseCallback.onPhotoDeleted(photoID);
        }

        @Override
        public void onError(Throwable errorBundle) {
            notifyError(errorBundle);
        }

        @Override
        public void onPhotoUpdated(PhotoDataEntity photoDataEntity) {
            useCaseCallback.onPhotoUpdated(photoDataEntity);
        }
    };

    public GetPhotosUseCaseImpl() {
    }

    private void notifyGetPhotoListSuccessfully(final List<PhotoDataEntity> photoDataEntityList) {
        useCaseCallback.onPhotoListLoaded(photoDataEntityList);
    }

    private void notifyError(final Throwable errorBundle) {
        useCaseCallback.onError(errorBundle);
    }

    @Override
    public void requestPhotos(int page, String query, boolean isOnline, UseCaseCallback useCaseCallback) {
        if (useCaseCallback == null) {
            throw new IllegalArgumentException("Interactor useCaseCallback cannot be null!!!");
        }
        this.useCaseCallback = useCaseCallback;
        photoDataRepository.getPhotoList(page, query, isOnline, repositoryCallback);
    }

    @Override
    public void deletePhoto(String photoId, UseCaseCallback useCaseUseCaseCallback) {
        if (useCaseCallback == null) {
            throw new IllegalArgumentException("Interactor useCaseCallback cannot be null!!!");
        } else {
            this.useCaseCallback = useCaseUseCaseCallback;
        }
        photoDataRepository.deletePhotoFromDevice(photoId, repositoryCallback);
    }

    /**
     * Stop all operations in repository
     */
    public void dispose() {
        photoDataRepository.dispose();
    }
}
