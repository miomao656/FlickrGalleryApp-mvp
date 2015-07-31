package com.misotest.flickrgalleryapp.data.repository;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.entity.mapper.PhotoEntityDataMapper;
import com.misotest.flickrgalleryapp.data.repository.datasource.IPhotoDataStore;
import com.misotest.flickrgalleryapp.data.repository.datasource.PhotosDbStore;
import com.misotest.flickrgalleryapp.domain.repository.IPhotosRepository;

import java.util.List;

import javax.inject.Singleton;

/**
 * Interface that represents a Repository for getting {@link PhotoDataEntity} related data.
 */
@Singleton
public class PhotoDataRepositoryOld implements IPhotosRepository {

    private static PhotoDataRepositoryOld INSTANCE;
    private final PhotosDbStore photosDbStore;
    private IPhotosRepository.PhotoListCallback photoListCallback;
    private IPhotoDataStore.PhotoDBRepoCallback dbListCallback = new IPhotoDataStore.PhotoDBRepoCallback() {

        @Override
        public void onPhotoDbDataSaved(final List<PhotoDataEntity> photoDataEntities) {
            if (photoDataEntities != null) {
                photoListCallback.onPhotoListLoaded(new PhotoEntityDataMapper().transform(photoDataEntities));
            }
        }

        @Override
        public void onPhotoListRetrieved(List<PhotoDataEntity> photoDataEntities) {
            if (photoDataEntities != null) {
                photoListCallback.onPhotoListLoaded(new PhotoEntityDataMapper().transform(photoDataEntities));
            }
        }

        @Override
        public void onPhotoDeleted(String photoID) {
            photoListCallback.onPhotoDeleted(photoID);
        }

        @Override
        public void onError(Throwable exception) {
            photoListCallback.onError(exception);
        }

        @Override
        public void onPhotoUpdated(PhotoDataEntity photoDataEntity) {
            photoListCallback.onPhotoUpdated(new PhotoEntityDataMapper().transform(photoDataEntity));
        }
    };

    /**
     * Constructs a {@link PhotoDataRepositoryOld}.
     *
     * @param photosDbStore A factory to construct different data source implementations.
     */
    protected PhotoDataRepositoryOld(PhotosDbStore photosDbStore) {
        if (photosDbStore == null) {
            throw new IllegalArgumentException("Invalid null parameters in constructor!!!");
        }
        this.photosDbStore = photosDbStore;
    }

    public static synchronized PhotoDataRepositoryOld getInstance(PhotosDbStore dataStoreFactory) {
        if (INSTANCE == null) {
            INSTANCE = new PhotoDataRepositoryOld(dataStoreFactory);
        }
        return INSTANCE;
    }

    @Override
    public void deletePhotoFromDevice(String photoId, final PhotoListCallback photoListCallback) {
        if (photoListCallback == null) {
            throw new IllegalArgumentException("Callback cannot be null!!!");
        }
        this.photoListCallback = photoListCallback;
        if (photoId != null && !photoId.isEmpty()) {
            photosDbStore.deletePhotoFromDb(photoId, dbListCallback);
        }
    }

    @Override
    public void getPhotoList(int page, String query, boolean isOnline, final PhotoListCallback photoListCallback) {
        //    we always get all users from the cloud
        if (photoListCallback == null) {
            throw new IllegalArgumentException("Callback cannot be null!!!");
        }
        this.photoListCallback = photoListCallback;
        photosDbStore.getPhotosToPresent(page, query, dbListCallback);
    }

    public void dispose() {
        photosDbStore.dispose();
    }
}
