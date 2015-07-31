package com.misotest.flickrgalleryapp.data.repository;

import com.misotest.flickrgalleryapp.data.entity.mapper.PhotoEntityDataMapper;
import com.misotest.flickrgalleryapp.data.repository.datasource.PhotoDataStore;
import com.misotest.flickrgalleryapp.data.repository.datasource.PhotosDataStoreFactory;
import com.misotest.flickrgalleryapp.domain.entity.PhotoDomainEntity;
import com.misotest.flickrgalleryapp.domain.repository.PhotoRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by miomao on 7/26/15.
 */
@Singleton
public class PhotoDataRepository implements PhotoRepository {

    private final PhotoEntityDataMapper photoEntityDataMapper;
    private final PhotosDataStoreFactory photosDataStoreFactory;

    @Inject
    public PhotoDataRepository(PhotosDataStoreFactory photosDataStoreFactory, PhotoEntityDataMapper photoEntityDataMapper) {
        this.photosDataStoreFactory = photosDataStoreFactory;
        this.photoEntityDataMapper = photoEntityDataMapper;
    }

    @Override
    public Observable<List<PhotoDomainEntity>> photos() {
        final PhotoDataStore photoDataStore = photosDataStoreFactory.create();
        return photoDataStore.photos().map(photoEntityDataMapper::transform);
    }

    @Override
    public Observable<PhotoDomainEntity> photo(int userId) {
        throw new UnsupportedOperationException("Operation is not available!!!");
    }
}
