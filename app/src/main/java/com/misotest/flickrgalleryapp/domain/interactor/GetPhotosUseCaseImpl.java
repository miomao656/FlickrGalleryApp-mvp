package com.misotest.flickrgalleryapp.domain.interactor;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.data.repository.PhotoDataRepository;
import com.misotest.flickrgalleryapp.domain.PhotoDomainEntity;
import com.misotest.flickrgalleryapp.domain.exception.ErrorBundle;
import com.misotest.flickrgalleryapp.domain.repository.IPhotosRepository;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * This class is an implementation of {@link IGetPhotosUseCase} that represents a use case for
 * retrieving a collection of all {@link PhotoDomainEntity}.
 */
public class GetPhotosUseCaseImpl implements IGetPhotosUseCase {

    private CompositeSubscription subscriptions = new CompositeSubscription();

//    private static final String DEFAULT_SEARCH_THERM = "akita";

    int page;
    String query;

    private PhotoDataRepository photoDataRepository = new PhotoDataRepository();

    private Callback callback;

    private IPhotosRepository.PhotoListCallback repositoryCallback = new IPhotosRepository.PhotoListCallback() {
        @Override
        public void onPhotoListLoaded(List<PhotoDataEntity> photoCollection) {
            notifyGetUserListSuccessfully(photoCollection);
        }

        @Override
        public void onError(ErrorBundle errorBundle) {
            notifyError(errorBundle);
        }
    };

    public GetPhotosUseCaseImpl() {
    }

//    /**
//     * Get photo id's from rest service
//     *  @param page
//     * @param query
//     * @param callback
//     */
//    @Override
//    public void requestPhotos(int page, String query, Callback callback) {
//        if (!query.isEmpty()) {
//            subscriptions.add(
//                    PhotosApi.photosApi.getPhotos(Constants.FLICKR_API_KEY, query, Constants.NO_PRIVACY_FILTER,
//                            Constants.PHOTO_PER_PAGE, page, Constants.FLICKR_FORMAT, Constants.NO_JSONP_RESPONSE)
//                            .flatMap(new Func1<PhotosEntity, Observable<PhotoElement>>() {
//                                @Override
//                                public Observable<PhotoElement> call(PhotosEntity photosEntity) {
//                                    return Observable.from(photosEntity.photos.photo);
//                                }
//                            })
//                            .map(new Func1<PhotoElement, PhotosResponse>() {
//
//                                @Override
//                                public PhotosResponse call(PhotoElement photoElement) {
//                                    PhotosResponse photosResponse = new PhotosResponse();
//                                    photosResponse.id = photoElement.id;
//                                    photosResponse.title = photoElement.title;
//                                    return photosResponse;
//                                }
//                            })
//                            .toList()
//                            .subscribeOn(Schedulers.computation())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(
//                                    new Action1<List<PhotosResponse>>() {
//                                        @Override
//                                        public void call(List<PhotosResponse> photosResponseList) {
//                                            Timber.e(photosResponseList.toString());
//                                            getPhotosUrlList(photosResponseList);
//                                        }
//                                    }
//                            )
//            );
//        } else {
//            //if search term not set use default one
//            run();
//        }
//    }
//
//    class PhotosResponse {
//        public String id;
//        public String title;
//    }
//
//    /**
//     * Rest call for retrieving photo resources url list
//     *
//     * @param entities
//     */
//    private void getPhotosUrlList(List<PhotosResponse> entities) {
//        Subscription bla = Observable.from(entities)
//                .flatMap(new Func1<PhotosResponse, Observable<PhotoDomainEntity>>() {
//                    @Override
//                    public Observable<PhotoDomainEntity> call(final PhotosResponse response) {
//                        return PhotosApi.photosApi.getPhotoData(Constants.FLICKR_API_KEY, response.id,
//                                Constants.FLICKR_FORMAT, Constants.NO_JSONP_RESPONSE)
//                                .flatMap(new Func1<PhotoEntity, Observable<SizeElement>>() {
//                                    @Override
//                                    public Observable<SizeElement> call(PhotoEntity photoEntity) {
//                                        return Observable.from(photoEntity.sizes.size);
//                                    }
//                                })
//                                .filter(new Func1<SizeElement, Boolean>() {
//                                    @Override
//                                    public Boolean call(SizeElement sizeElement) {
//                                        return sizeElement.label.equals(Constants.IMAGE_SIZE);
//                                    }
//                                })
//                                .map(new Func1<SizeElement, PhotoDomainEntity>() {
//                                    @Override
//                                    public PhotoDomainEntity call(SizeElement sizeElement) {
//                                        return new PhotoDomainEntity(response.id, response.title, sizeElement.source);
//                                    }
//                                });
//                    }
//                })
//                .toList()
//                .subscribe(
//                        new Action1<List<PhotoDomainEntity>>() {
//                            @Override
//                            public void call(List<PhotoDomainEntity> photoDomainEntities) {
//                                downloadImg(photoDomainEntities);
//                                Timber.d("bla");
////                                sendPhotosToPresenter(photoDomainEntities);
//                            }
//                        },
//                        new Action1<Throwable>() {
//                            @Override
//                            public void call(Throwable throwable) {
//                                throwable.printStackTrace();
//                            }
//                        }
//                );
//    }
//
//    /**
//     * RxObservable for downloading a list of files from url's to local storage
//     *
//     * @param domainEntityList
//     */
//    private void downloadImg(List<PhotoDomainEntity> domainEntityList) {
//        subscriptions.add(
//                Observable.from(domainEntityList)
//                        .flatMap(new Func1<PhotoDomainEntity, Observable<PhotoDataEntity>>() {
//                            @Override
//                            public Observable<PhotoDataEntity> call(final PhotoDomainEntity photoDomainEntity) {
//                                return BitmapUtils.downloadBitmapFromUrl(photoDomainEntity.url).map(
//                                        new Func1<String, PhotoDataEntity>() {
//                                            @Override
//                                            public PhotoDataEntity call(String s) {
//                                                PhotoDataEntity photoDataEntity = new PhotoDataEntity();
//                                                photoDataEntity.photo_id = photoDomainEntity.id;
//                                                photoDataEntity.photo_title = photoDomainEntity.title;
//                                                photoDataEntity.photo_url = photoDomainEntity.url;
//                                                photoDataEntity.file_path = s;
//                                                return photoDataEntity;
//                                            }
//                                        }
//                                );
//                            }
//                        })
//                        .toList()
//                        .subscribeOn(Schedulers.computation())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                new Action1<List<PhotoDataEntity>>() {
//                                    @Override
//                                    public void call(List<PhotoDataEntity> uriList) {
////                                        listPresenter.onImagesUpdated(uriList);
//                                    }
//                                },
//                                new Action1<Throwable>() {
//                                    @Override
//                                    public void call(Throwable throwable) {
//                                        throwable.printStackTrace();
//                                    }
//                                }
//                        )
//        );
//    }

    /**
     * Stops RxObservables execution
     */
    @Override
    public void unregister() {
        subscriptions.unsubscribe();
    }

    /**
     * Default action to perform
     */
    @Override
    public void run() {
        photoDataRepository.getPhotoList(page, query, repositoryCallback);
    }

    private void notifyGetUserListSuccessfully(final List<PhotoDataEntity> photoDataEntityList) {
                callback.onPhotoListLoaded(photoDataEntityList);
    }

    private void notifyError(final ErrorBundle errorBundle) {
                callback.onError(errorBundle);
    }

    @Override
    public void requestPhotos(int page, String query, Callback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Interactor callback cannot be null!!!");
        }
        this.callback = callback;
        photoDataRepository.getPhotoList(page, query, repositoryCallback);
    }
}
