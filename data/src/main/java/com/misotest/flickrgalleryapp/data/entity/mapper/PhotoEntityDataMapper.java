package com.misotest.flickrgalleryapp.data.entity.mapper;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.domain.entity.PhotoDomainEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Convert from data to domain model
 */
@Singleton
public class PhotoEntityDataMapper {

    @Inject
    public PhotoEntityDataMapper() {
    }

    /**
     * Transform a List of {@link PhotoDataEntity} into a List of {@link PhotoDomainEntity}.
     *
     * @param photoDataEntities Objects to be transformed.
     * @return List of {@link PhotoDomainEntity}.
     */
    public List<PhotoDomainEntity> transform(List<PhotoDataEntity> photoDataEntities) {
        List<PhotoDomainEntity> userModelsCollection;

        if (photoDataEntities != null && !photoDataEntities.isEmpty()) {
            userModelsCollection = new ArrayList<PhotoDomainEntity>();
            for (PhotoDataEntity photo : photoDataEntities) {
                userModelsCollection.add(transform(photo));
            }
        } else {
            userModelsCollection = Collections.emptyList();
        }
        return userModelsCollection;
    }

    /**
     * Transform a {@link PhotoDataEntity} into an {@link PhotoDomainEntity}.
     *
     * @param photoDataEntity Object to be transformed.
     * @return {@link PhotoDomainEntity}.
     */
    public PhotoDomainEntity transform(PhotoDataEntity photoDataEntity) {
        if (photoDataEntity == null) {
            throw new IllegalArgumentException("Cannot transform a null value");
        }
        PhotoDomainEntity photoPresentationModel = new PhotoDomainEntity();
        photoPresentationModel.photo_id = photoDataEntity.photo_id;
        photoPresentationModel.photo_title = photoDataEntity.photo_title;
        photoPresentationModel.photo_url = photoDataEntity.photo_url;
        photoPresentationModel.photo_file_path = photoDataEntity.photo_file_path;

        return photoPresentationModel;
    }
}
