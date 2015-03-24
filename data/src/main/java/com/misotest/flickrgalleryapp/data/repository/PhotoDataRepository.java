package com.misotest.flickrgalleryapp.data.repository;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.entity.mapper.PhotoDomainModelMapper;
import com.misotest.flickrgalleryapp.data.repository.datasource.IPhotoDataStore;
import com.misotest.flickrgalleryapp.data.repository.datasource.PhotosDbStore;
import com.misotest.flickrgalleryapp.domain.repository.IPhotosRepository;

import java.util.List;

/**
 * Interface that represents a Repository for getting {@link PhotoDataEntity} related data.
 */
public class PhotoDataRepository implements IPhotosRepository {

    private static PhotoDataRepository INSTANCE;
    private PhotosDbStore photosDbStore;
    private PhotoListCallback photoListCallback;

    /**
     * Constructs a {@link PhotoDataRepository}.
     *
     * @param photosDbStore A factory to construct different data source implementations.
     */
    protected PhotoDataRepository(PhotosDbStore photosDbStore) {
        if (photosDbStore == null) {
            throw new IllegalArgumentException("Invalid null parameters in constructor!!!");
        }
        this.photosDbStore = photosDbStore;
    }

    public static synchronized PhotoDataRepository getInstance(PhotosDbStore dataStoreFactory) {
        if (INSTANCE == null) {
            INSTANCE = new PhotoDataRepository(dataStoreFactory);
        }
        return INSTANCE;
    }

    private IPhotoDataStore.PhotoDBRepoCallback dbListCallback = new IPhotoDataStore.PhotoDBRepoCallback() {

        @Override
        public void onPhotoDbDataSaved(final List<PhotoDataEntity> photoDataEntities) {
            if (photoDataEntities != null) {
                photoListCallback.onPhotoListLoaded(new PhotoDomainModelMapper().transform(photoDataEntities));
            }
        }

        @Override
        public void onPhotoListRetrieved(List<PhotoDataEntity> photoDataEntities) {
            if (photoDataEntities != null) {
                photoListCallback.onPhotoListLoaded(new PhotoDomainModelMapper().transform(photoDataEntities));
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
            photoListCallback.onPhotoUpdated(new PhotoDomainModelMapper().transform(photoDataEntity));
        }
    };

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
        //we always get all users from the cloud
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
