package com.misotest.flickrgalleryapp.data.repository.datasource;

import com.misotest.flickrgalleryapp.Constants;
import com.misotest.flickrgalleryapp.data.datautils.BitmapUtils;
import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.entity.PhotoElement;
import com.misotest.flickrgalleryapp.data.entity.PhotoEntity;
import com.misotest.flickrgalleryapp.data.entity.PhotosEntity;
import com.misotest.flickrgalleryapp.data.entity.SizeElement;
import com.misotest.flickrgalleryapp.data.rest.photosApi.PhotosApi;
import com.misotest.flickrgalleryapp.domain.PhotoDomainEntity;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by miomao on 3/17/15.
 */
public class PhotoCloudStore implements IPhotoDataStore {

    private CompositeSubscription subscription = new CompositeSubscription();
    private static final String DEFAULT_SEARCH_THERM = "akita";
    private PhotoDataEntityListCallback restCallback;

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
                        .subscribeOn(Schedulers.computation())
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
                        .subscribe(
                                new Action1<List<PhotoDataEntity>>() {
                                    @Override
                                    public void call(List<PhotoDataEntity> photoDataEntityList) {
//                                        downloadImg(photoDomainEntities);
                                        Timber.d("bla");
//                                        sendPhotosToPresenter(photoDomainEntities);
                                        restCallback.onPhotoDataEntityListLoaded(photoDataEntityList);
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                }
                        )
        );
    }

    @Override
    public void getPhotoEntityList(int page, String query, PhotoDataEntityListCallback callback) {
        this.restCallback = callback;
        getPhotos(page, query);
    }

    class PhotosResponse {
        public String id;
        public String title;
    }

    /**
     * RxObservable for downloading a list of files from url's to local storage
     *
     * @param domainEntityList
     */
    private void downloadImg(List<PhotoDomainEntity> domainEntityList) {
        subscription.add(
                Observable.from(domainEntityList)
                        .flatMap(new Func1<PhotoDomainEntity, Observable<PhotoDataEntity>>() {
                            @Override
                            public Observable<PhotoDataEntity> call(final PhotoDomainEntity photoDomainEntity) {
                                return BitmapUtils.downloadBitmapFromUrl(photoDomainEntity.url).map(
                                        new Func1<String, PhotoDataEntity>() {
                                            @Override
                                            public PhotoDataEntity call(String s) {
                                                PhotoDataEntity photoDataEntity = new PhotoDataEntity();
                                                photoDataEntity.photo_id = photoDomainEntity.id;
                                                photoDataEntity.photo_title = photoDomainEntity.title;
                                                photoDataEntity.photo_url = photoDomainEntity.url;
                                                photoDataEntity.file_path = s;
                                                return photoDataEntity;
                                            }
                                        }
                                );
                            }
                        })
                        .toList()
                        .subscribeOn(Schedulers.computation())
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
                                    }
                                }
                        )
        );
    }
}