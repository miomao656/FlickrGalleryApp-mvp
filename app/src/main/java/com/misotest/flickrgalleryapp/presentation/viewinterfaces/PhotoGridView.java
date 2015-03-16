package com.misotest.flickrgalleryapp.presentation.viewinterfaces;

import java.util.List;

public interface PhotoGridView extends BaseView {

    void showItemsFromDiskUrl(List<String> itemDomainEntityList);

    void showLoading();

    void hideLoading();

    void showError(String error);

    void hideError();
}
