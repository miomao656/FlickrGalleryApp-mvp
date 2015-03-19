package com.misotest.flickrgalleryapp.presentation.viewinterfaces;

import com.misotest.flickrgalleryapp.presentation.entity.PhotoPresentationModel;

import java.util.List;

public interface PhotoGridView extends BaseView {

    void presentPhotos(List<PhotoPresentationModel> itemDomainEntityList);

    void presentPhotosUpdated(List<PhotoPresentationModel> itemDomainEntityList);

    void showLoading();

    void hideLoading();

    void onPhotoDeleted();

    void onPhotoUpdated(PhotoPresentationModel presentationModel);

    void showError(String error);

    void hideError();

}
