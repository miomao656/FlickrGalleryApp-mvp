package com.misotest.flickrgalleryapp.data.repository.datasource;

import com.misotest.flickrgalleryapp.data.datautils.BitmapUtils;
import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.rest.photosApi.PhotosApi;

import java.util.List;

import retrofit.RetrofitError;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * {@link IPhotoDataStore} implementation based on connections to the api (Cloud).
 */
public class PhotoCloudStore implements IPhotoDataStore {

    private CompositeSubscription subscription = new CompositeSubscription();
    private PhotoRestRepoCallback photoRestRepoCallback;

    /**
     * Rest call for retrieving photo resources url list
     *
     * @param entities
     */
    private void getPhotosUrlList(List<PhotosResponse> entities) {
        subscription.add(Observable.from(entities)
                        .flatMap(response -> PhotosApi.photosApi.getPhotoData(FLICKR_API_KEY, response.id,
                                FLICKR_FORMAT, NO_JSONP_RESPONSE)
                                .flatMap(photoEntity -> Observable.from(photoEntity.sizes.size))
                                .filter(sizeElement -> sizeElement.label.equals(IMAGE_SIZE))
                                .map(sizeElement -> new PhotoDataEntity(response.id, response.title, sizeElement.source, "")))
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                photoRestRepoCallback::onPhotoDataEntityListLoaded,
                                throwable -> {
                                    throwable.printStackTrace();
                                    if (throwable instanceof RetrofitError) {
                                        if (((RetrofitError) throwable).isNetworkError()) {
                                            //handle network error
                                        } else {
                                            //handle error message from server
                                        }
                                        photoRestRepoCallback.onPhotoDataEntityListLoaded(null);
                                    } else {
                                        photoRestRepoCallback.onError(throwable);
                                    }
                                }
                        )
        );
    }

    @Override
    public void getPhotoEntityList(int page, String query, PhotoRestRepoCallback photoRestRepoCallback) {
        if (photoRestRepoCallback == null) {
            throw new IllegalArgumentException("Callback cannot be null!!!");
        }
        this.photoRestRepoCallback = photoRestRepoCallback;
        getPhotos(page, query);
    }

    /**
     * Rest call for retrieving a list of PhotosEntity objects and mapping them to temp
     * class PhotoResponse and calling getPhotosUrlList
     *
     * @param page
     * @param query
     */
    public void getPhotos(int page, String query) {
        if (!query.isEmpty()) {
            query = DEFAULT_SEARCH_THERM;
        }
        subscription.add(
                PhotosApi.photosApi.getPhotos(FLICKR_API_KEY, query, NO_PRIVACY_FILTER,
                        PHOTO_PER_PAGE, page, FLICKR_FORMAT, NO_JSONP_RESPONSE)
                        .flatMap(photosEntity -> Observable.from(photosEntity.photos.photo))
                        .map(photoElement -> {
                            PhotosResponse photosResponse = new PhotosResponse();
                            photosResponse.id = photoElement.id;
                            photosResponse.title = photoElement.title;
                            return photosResponse;
                        })
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                PhotoCloudStore.this::getPhotosUrlList,
                                throwable -> {
                                    throwable.printStackTrace();
                                    if (throwable instanceof RetrofitError) {
                                        if (((RetrofitError) throwable).isNetworkError()) {
                                            //handle network error
                                        } else {
                                            //handle error message from server
                                        }
                                        photoRestRepoCallback.onPhotoDataEntityListLoaded(null);
                                    } else {
                                        photoRestRepoCallback.onError(throwable);
                                    }
                                }
                        )
        );
    }

    @Override
    public void savePhotoEntityList(List<PhotoDataEntity> dataEntityList, PhotoDBRepoCallback callback) {
        //save photo to server
    }

    @Override
    public void deletePhotoFromDb(String photoId, PhotoDBRepoCallback photoDBRepoCallback) {
        //delete photo from server
    }

    @Override
    public void dispose() {
        subscription.unsubscribe();
    }

    /**
     * RxObservable for downloading a list of files from url's to local storage
     *
     * @param domainEntityList
     */
    public void downloadPhotos(List<PhotoDataEntity> domainEntityList, final PhotoRestRepoCallback callback) {
        subscription.add(
                Observable.from(domainEntityList)
                        .filter(photoDataEntity -> photoDataEntity.photo_file_path.isEmpty())
                        .flatMap(photoDomainEntity -> BitmapUtils.downloadBitmapFromUrl(photoDomainEntity.photo_url).map(
                                s -> {
                                    PhotoDataEntity photoDataEntity = new PhotoDataEntity();
                                    photoDataEntity.photo_id = photoDomainEntity.photo_id;
                                    photoDataEntity.photo_title = photoDomainEntity.photo_title;
                                    photoDataEntity.photo_url = photoDomainEntity.photo_url;
                                    photoDataEntity.photo_file_path = s;
                                    return photoDataEntity;
                                }
                        ))
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                callback::onPhotosDownloaded,
                                throwable -> {
                                    throwable.printStackTrace();
                                    callback.onError(throwable);
                                }
                        )
        );
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
