package com.misotest.flickrgalleryapp.presentation.viewinterfaces;

import com.misotest.flickrgalleryapp.presentation.PhotoPresentationModel;

import java.util.List;

public interface PhotoGridView extends BaseView {

    void presentPhotoItems(List<PhotoPresentationModel> itemDomainEntityList);

    void showLoading();

    void hideLoading();

    void showError(String error);

    void hideError();
}
