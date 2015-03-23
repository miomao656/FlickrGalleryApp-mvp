package com.misotest.flickrgalleryapp.presentation.entity.mapper;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.presentation.entity.PhotoPresentationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Convert from data to presentation model
 */
public class PhotoPresentationModelMapper {

    public PhotoPresentationModelMapper() {
    }

    /**
     * Transform a {@link PhotoDataEntity} into an {@link PhotoPresentationModel}.
     *
     * @param photoDataEntity Object to be transformed.
     * @return {@link PhotoPresentationModel}.
     */
    public PhotoPresentationModel transform(PhotoDataEntity photoDataEntity) {
        if (photoDataEntity == null) {
            throw new IllegalArgumentException("Cannot transform a null value");
        }
        PhotoPresentationModel photoPresentationModel = new PhotoPresentationModel();
        photoPresentationModel.photo_id = photoDataEntity.photo_id;
        photoPresentationModel.photo_title = photoDataEntity.photo_title;
        photoPresentationModel.photo_url = photoDataEntity.photo_url;
        photoPresentationModel.photo_file_path = photoDataEntity.photo_file_path;

        return photoPresentationModel;
    }

    /**
     * Transform a List of {@link PhotoDataEntity} into a List of {@link PhotoPresentationModel}.
     *
     * @param photoDataEntities Objects to be transformed.
     * @return List of {@link PhotoPresentationModel}.
     */
    public List<PhotoPresentationModel> transform(List<PhotoDataEntity> photoDataEntities) {
        List<PhotoPresentationModel> userModelsCollection;

        if (photoDataEntities != null && !photoDataEntities.isEmpty()) {
            userModelsCollection = new ArrayList<PhotoPresentationModel>();
            for (PhotoDataEntity photo : photoDataEntities) {
                userModelsCollection.add(transform(photo));
            }
        } else {
            userModelsCollection = Collections.emptyList();
        }
        return userModelsCollection;
    }
}
