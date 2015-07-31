package com.misotest.flickrgalleryapp.data.repository.datasource;

import com.misotest.flickrgalleryapp.data.database.PhotosDbCache;
import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.rest.photosApi.PhotosApi;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by miomao on 7/26/15.
 */
public class CloudPhotoDataStore implements PhotoDataStore {

    private final PhotosDbCache photosDbCache;

    private final Action1<PhotoDataEntity> saveToDbAction =
            photoDataEntity -> {
                if (photoDataEntity != null) {
                    CloudPhotoDataStore.this.photosDbCache.put(photoDataEntity);
                }
            };

    private final Action1<List<PhotoDataEntity>> saveListToDbAction =
            photoDataEntities -> {
                if (photoDataEntities != null && photoDataEntities.size() > 0) {
                    CloudPhotoDataStore.this.photosDbCache.putList(photoDataEntities);
                }
            };

    public CloudPhotoDataStore(PhotosDbCache photosDbCache) {
        this.photosDbCache = photosDbCache;
    }

    @Override
    public Observable<List<PhotoDataEntity>> photos() {
        return PhotosApi.photosApi
                .getPhotos(FLICKR_API_KEY, "cats", NO_PRIVACY_FILTER, PHOTO_PER_PAGE, 1, FLICKR_FORMAT, NO_JSONP_RESPONSE)
                .flatMap(photosEntity -> Observable.from(photosEntity.photos.photo))
                .map(photoElement -> {
                    PhotosResponse photosResponse = new PhotosResponse();
                    photosResponse.id = photoElement.id;
                    photosResponse.title = photoElement.title;
                    return photosResponse;
                })
                .flatMap(response -> PhotosApi.photosApi.getPhotoData(FLICKR_API_KEY, response.id,
                        FLICKR_FORMAT, NO_JSONP_RESPONSE)
                        .flatMap(photoEntity -> Observable.from(photoEntity.sizes.size))
                        .filter(sizeElement -> sizeElement.label.equals(IMAGE_SIZE))
                        .map(sizeElement -> new PhotoDataEntity(response.id, response.title, sizeElement.source, "")))
                .toList()
                .doOnNext(saveListToDbAction);
    }

    @Override
    public Observable<PhotoDataEntity> photo() {
        throw new UnsupportedOperationException("Operation is not available!!!");
    }

    /**
     * Temp inner class for saving id and title and mapping it to PhotoDataEntity
     * when rest returns photo details response
     */
    class PhotosResponse {
        public String id;
        public String title;
    }
}
