package com.misotest.flickrgalleryapp.presentation.presenters;


import com.misotest.flickrgalleryapp.domain.entity.PhotoDomainEntity;
import com.misotest.flickrgalleryapp.domain.interactor.IGetPhotosUseCase;
import com.misotest.flickrgalleryapp.presentation.entity.PhotoPresentationModel;
import com.misotest.flickrgalleryapp.presentation.entity.mapper.PhotoPresentationModelMapper;
import com.misotest.flickrgalleryapp.presentation.utils.CommonUtils;
import com.misotest.flickrgalleryapp.presentation.viewinterfaces.PhotoGridView;

import java.util.List;

/**
 * {@link Presenter} that controls communication between views and models of the presentation
 * layer.
 */
public class PhotosListPresenter extends Presenter {

    private PhotoGridView photoGridView;
    private IGetPhotosUseCase getPhotosUseCase;
    private String query;
    private PhotoPresentationModelMapper mapper = new PhotoPresentationModelMapper();

    private final IGetPhotosUseCase.UseCaseCallback useCaseUseCaseCallback = new IGetPhotosUseCase.UseCaseCallback() {
        @Override
        public void onPhotoListLoaded(List<PhotoDomainEntity> photosCollection) {
            photoGridView.hideLoading();
            showPhotoListInView(photosCollection);
        }

        @Override
        public void onPhotoDeleted(String photoID) {
            photoGridView.hideLoading();
            photoGridView.onPhotoDeleted(photoID);
        }

        @Override
        public void onError(Throwable errorBundle) {
            photoGridView.hideLoading();
            photoGridView.showError(errorBundle.toString());
        }

        @Override
        public void onPhotoListUpdated(List<PhotoDomainEntity> photoDataEntityList) {
            photoGridView.hideLoading();
            photoGridView.presentPhotosUpdated(mapper.transform(photoDataEntityList));
        }

        @Override
        public void onPhotoUpdated(PhotoDomainEntity photoDataEntity) {
            photoGridView.updatePhotoInList(mapper.transform(photoDataEntity));
        }
    };

    public PhotosListPresenter(PhotoGridView photoGridView, IGetPhotosUseCase iGetPhotosUseCase) {
        if (photoGridView == null || iGetPhotosUseCase == null) {
            throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
        }
        this.photoGridView = photoGridView;
        this.getPhotosUseCase = iGetPhotosUseCase;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public void startPresenting() {
        photoGridView.showLoading();
        getPage(0, query);
    }

    @Override
    public void stop() {
        getPhotosUseCase.dispose();
    }

    private void showPhotoListInView(List<PhotoDomainEntity> usersCollection) {
        final List<PhotoPresentationModel> userModelsCollection =
                this.mapper.transform(usersCollection);
        photoGridView.presentPhotos(userModelsCollection);
    }

    /**
     * Request use case to get additional list of photos
     *
     * @param page
     * @param query
     */
    public void getPage(int page, String query) {
        getPhotosUseCase.requestPhotos(page, query,
                CommonUtils.isNetworkAvailable(photoGridView.getContext()), useCaseUseCaseCallback);
    }

    /**
     * Request delete photo from use case
     *
     * @param photoId
     */
    public void deletePhoto(String photoId) {
        photoGridView.showLoading();
        getPhotosUseCase.deletePhoto(photoId, useCaseUseCaseCallback);
    }
}
