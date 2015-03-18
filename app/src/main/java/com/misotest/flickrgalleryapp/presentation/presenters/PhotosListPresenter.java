package com.misotest.flickrgalleryapp.presentation.presenters;


import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.domain.interactor.GetPhotosUseCaseImpl;
import com.misotest.flickrgalleryapp.domain.interactor.IGetPhotosUseCase;
import com.misotest.flickrgalleryapp.presentation.entity.PhotoPresentationModel;
import com.misotest.flickrgalleryapp.presentation.entity.mapper.PhotoPresentationModelMapper;
import com.misotest.flickrgalleryapp.presentation.viewinterfaces.PhotoGridView;

import java.util.List;

/**
 * {@link Presenter} that controls communication between views and models of the presentation
 * layer.
 */
public class PhotosListPresenter extends Presenter {

    private final PhotoGridView photoGridView;
    private GetPhotosUseCaseImpl getPhotosUseCase;
    private String query;
    private PhotoPresentationModelMapper mapper = new PhotoPresentationModelMapper();

    private final IGetPhotosUseCase.UseCaseCallback useCaseUseCaseCallback = new IGetPhotosUseCase.UseCaseCallback() {
        @Override
        public void onPhotoListLoaded(List<PhotoDataEntity> photosCollection) {
            photoGridView.hideLoading();
            showPhotoListInView(photosCollection);
        }

        @Override
        public void onPhotoDeleted() {
            photoGridView.onPhotoDeleted();
        }

        @Override
        public void onError(Throwable errorBundle) {
            photoGridView.hideLoading();
            photoGridView.showError(errorBundle.toString());
        }
    };

    public PhotosListPresenter(PhotoGridView photoGridView) {
        this.photoGridView = photoGridView;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public void startPresenting() {
        photoGridView.showLoading();
        getPhotosUseCase = new GetPhotosUseCaseImpl();
        getPage(0, query);
    }

    @Override
    public void stop() {

    }

    private void showPhotoListInView(List<PhotoDataEntity> usersCollection) {
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
        getPhotosUseCase.requestPhotos(page, query, useCaseUseCaseCallback);
    }

    /**
     * Request delete photo from use case
     *
     * @param photoId
     */
    public void deletePhoto(String photoId){
        getPhotosUseCase.deletePhoto(photoId, useCaseUseCaseCallback);
    }
}
