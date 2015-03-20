package com.misotest.flickrgalleryapp.presentation.viewinterfaces;

import com.misotest.flickrgalleryapp.presentation.entity.PhotoPresentationModel;

import java.util.List;

public interface PhotoGridView extends BaseView {

    void presentPhotos(List<PhotoPresentationModel> itemDomainEntityList);

    void presentPhotosUpdated(List<PhotoPresentationModel> itemDomainEntityList);

    void showLoading();

    void hideLoading();

    void onPhotoDeleted(String photoID);

    void showError(String error);

    void updatePhotoInList(PhotoPresentationModel transform);
}
