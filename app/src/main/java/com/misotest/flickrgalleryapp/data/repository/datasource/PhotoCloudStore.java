package com.misotest.flickrgalleryapp.data.repository.datasource;

import com.misotest.flickrgalleryapp.Constants;
import com.misotest.flickrgalleryapp.data.datautils.BitmapUtils;
import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.entity.PhotoElement;
import com.misotest.flickrgalleryapp.data.entity.PhotoEntity;
import com.misotest.flickrgalleryapp.data.entity.PhotosEntity;
import com.misotest.flickrgalleryapp.data.entity.SizeElement;
import com.misotest.flickrgalleryapp.data.rest.photosApi.PhotosApi;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * {@link IPhotoDataStore} implementation based on connections to the api (Cloud).
 */
public class PhotoCloudStore implements IPhotoDataStore {

    private CompositeSubscription subscription = new CompositeSubscription();
    private PhotoDataRepositoryListCallback photoDataRepositoryListCallback;

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
                PhotosApi.photosApi.getPhotos(Constants.FLICKR_API_KEY, query, Constants.NO_PRIVACY_FILTER,
                        Constants.PHOTO_PER_PAGE, page, Constants.FLICKR_FORMAT, Constants.NO_JSONP_RESPONSE)
                        .flatMap(new Func1<PhotosEntity, Observable<PhotoElement>>() {
                            @Override
                            public Observable<PhotoElement> call(PhotosEntity photosEntity) {
                                return Observable.from(photosEntity.photos.photo);
                            }
                        })
                        .map(new Func1<PhotoElement, PhotosResponse>() {

                            @Override
                            public PhotosResponse call(PhotoElement photoElement) {
                                PhotosResponse photosResponse = new PhotosResponse();
                                photosResponse.id = photoElement.id;
                                photosResponse.title = photoElement.title;
                                return photosResponse;
                            }
                        })
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<List<PhotosResponse>>() {
                                    @Override
                                    public void call(List<PhotosResponse> photosResponseList) {
                                        Timber.e(photosResponseList.toString());
                                        getPhotosUrlList(photosResponseList);
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
//                                        photoDataRepositoryListCallback.onError();
                                    }
                                }
                        )
        );
    }

    /**
     * Rest call for retrieving photo resources url list
     *
     * @param entities
     */
    private void getPhotosUrlList(List<PhotosResponse> entities) {
        subscription.add(Observable.from(entities)
                        .flatMap(new Func1<PhotosResponse, Observable<PhotoDataEntity>>() {
                            @Override
                            public Observable<PhotoDataEntity> call(final PhotosResponse response) {
                                return PhotosApi.photosApi.getPhotoData(Constants.FLICKR_API_KEY, response.id,
                                        Constants.FLICKR_FORMAT, Constants.NO_JSONP_RESPONSE)
                                        .flatMap(new Func1<PhotoEntity, Observable<SizeElement>>() {
                                            @Override
                                            public Observable<SizeElement> call(PhotoEntity photoEntity) {
                                                return Observable.from(photoEntity.sizes.size);
                                            }
                                        })
                                        .filter(new Func1<SizeElement, Boolean>() {
                                            @Override
                                            public Boolean call(SizeElement sizeElement) {
                                                return sizeElement.label.equals(Constants.IMAGE_SIZE);
                                            }
                                        })
                                        .map(new Func1<SizeElement, PhotoDataEntity>() {
                                            @Override
                                            public PhotoDataEntity call(SizeElement sizeElement) {
                                                return new PhotoDataEntity(0, response.id, sizeElement.source, response.title, "");
                                            }
                                        });
                            }
                        })
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<List<PhotoDataEntity>>() {
                                    @Override
                                    public void call(List<PhotoDataEntity> photoDataEntityList) {
                                        photoDataRepositoryListCallback.onPhotoDataEntityListLoaded(photoDataEntityList);
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
//                                        photoDataRepositoryListCallback.onError();
                                    }
                                }
                        )
        );
    }

    @Override
    public void getPhotoEntityList(int page, String query, PhotoDataRepositoryListCallback photoDataRepositoryListCallback) {
        if (photoDataRepositoryListCallback == null) {
            throw new IllegalArgumentException("Interactor callback cannot be null!!!");
        }
        this.photoDataRepositoryListCallback = photoDataRepositoryListCallback;
        getPhotos(page, query);
    }

    /**
     * Temp inner class for saving id and title and mapping it to PhotoDataEntity
     * when rest returns photo details response
     *
     */
    class PhotosResponse {
        public String id;
        public String title;
    }

    /**
     * RxObservable for downloading a list of files from url's to local storage
     *
     * @param domainEntityList
     */
    private void downloadPhotos(List<PhotoDataEntity> domainEntityList) {
        subscription.add(
                Observable.from(domainEntityList)
                        .flatMap(new Func1<PhotoDataEntity, Observable<PhotoDataEntity>>() {
                            @Override
                            public Observable<PhotoDataEntity> call(final PhotoDataEntity photoDomainEntity) {
                                return BitmapUtils.downloadBitmapFromUrl(photoDomainEntity.photo_url).map(
                                        new Func1<String, PhotoDataEntity>() {
                                            @Override
                                            public PhotoDataEntity call(String s) {
                                                PhotoDataEntity photoDataEntity = new PhotoDataEntity();
                                                photoDataEntity.photo_id = photoDomainEntity.photo_id;
                                                photoDataEntity.photo_title = photoDomainEntity.photo_title;
                                                photoDataEntity.photo_url = photoDomainEntity.photo_url;
                                                photoDataEntity.file_path = s;
                                                return photoDataEntity;
                                            }
                                        }
                                );
                            }
                        })
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<List<PhotoDataEntity>>() {
                                    @Override
                                    public void call(List<PhotoDataEntity> uriList) {
//                                        sendPhotosToPresenter(uriList);
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
//                                        photoDataRepositoryListCallback.onError();
                                    }
                                }
                        )
        );
    }
}
