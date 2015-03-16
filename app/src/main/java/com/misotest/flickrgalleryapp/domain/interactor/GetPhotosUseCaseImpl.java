package com.misotest.flickrgalleryapp.domain.interactor;

import com.misotest.flickrgalleryapp.Constants;
import com.misotest.flickrgalleryapp.data.datautils.BitmapUtils;
import com.misotest.flickrgalleryapp.data.entity.PhotoElement;
import com.misotest.flickrgalleryapp.data.entity.PhotoEntity;
import com.misotest.flickrgalleryapp.data.entity.PhotosEntity;
import com.misotest.flickrgalleryapp.data.entity.SizeElement;
import com.misotest.flickrgalleryapp.data.rest.photosApi.PhotosApi;
import com.misotest.flickrgalleryapp.presentation.presenters.PhotosListPresenter;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class GetPhotosUseCaseImpl implements IGetPhotosUseCase {

    private PhotosListPresenter listPresenter;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    private static final String DEFAULT_SEARCH_THERM = "akita";

    public GetPhotosUseCaseImpl(PhotosListPresenter listPresenter) {
        this.listPresenter = listPresenter;
    }

    /**
     * Get photo id's from rest service
     *
     * @param page
     * @param query
     */
    @Override
    public void getPhotos(int page, String query) {
        if (!query.isEmpty()) {
            subscriptions.add(
                    PhotosApi.photosApi.getPhotos(Constants.FLICKR_API_KEY, query, Constants.NO_PRIVACY_FILTER,
                            Constants.PHOTO_PER_PAGE, page, Constants.FLICKR_FORMAT, Constants.NO_JSONP_RESPONSE)
                            .flatMap(new Func1<PhotosEntity, Observable<PhotoElement>>() {
                                @Override
                                public Observable<PhotoElement> call(PhotosEntity photosEntity) {
                                    return Observable.from(photosEntity.photos.photo);
                                }
                            })
                            .map(new Func1<PhotoElement, String>() {

                                @Override
                                public String call(PhotoElement photoElement) {
                                    return photoElement.id;
                                }
                            })
                            .toList()
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    new Action1<List<String>>() {
                                        @Override
                                        public void call(List<String> strings) {
                                            Timber.e(strings.toString());
                                            getPhotosUrlList(strings);
                                        }
                                    }
                            )
            );
        } else {
            //if search term not set use default one
            run();
        }
    }

    /**
     * Passes a list of file uris to presenter
     *
     * @param uriList
     */
    @Override
    public void sendUrisToPresenter(List<String> uriList) {
        listPresenter.onImagesSaved(uriList);
    }

    /**
     * Rest call for retrieving photo resources url list
     *
     * @param ids
     */
    private void getPhotosUrlList(List<String> ids) {
        subscriptions.add(
                Observable.from(ids)
                        .flatMap(new Func1<String, Observable<PhotoEntity>>() {
                            @Override
                            public Observable<PhotoEntity> call(String s) {
                                //call get photo details for each id
                                return PhotosApi.photosApi.getPhotoData(Constants.FLICKR_API_KEY, s,
                                        Constants.FLICKR_FORMAT, Constants.NO_JSONP_RESPONSE);
                            }
                        })
                        .flatMap(new Func1<PhotoEntity, Observable<SizeElement>>() {
                            @Override
                            public Observable<SizeElement> call(PhotoEntity photoEntity) {
                                //return only image sizes from response
                                return Observable.from(photoEntity.sizes.size);
                            }
                        })
                        .filter(new Func1<SizeElement, Boolean>() {
                            @Override
                            public Boolean call(SizeElement sizeElement) {
                                //filter photos with certain image size
                                return sizeElement.label.equals(Constants.IMAGE_SIZE);
                            }
                        })
                        .flatMap(new Func1<SizeElement, Observable<String>>() {
                            @Override
                            public Observable<String> call(SizeElement sizeElement) {
                                //return only image url
                                return Observable.just(sizeElement.source);
                            }
                        })
                        .toList()
                        //async call
                        .subscribeOn(Schedulers.computation())
                        //return result on main thread
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<List<String>>() {

                                    @Override
                                    public void call(List<String> sizeElements) {
                                        downloadImg(sizeElements);
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
     * RxObservable for downloading a list of files from url's to local storage
     *
     * @param urlList
     */
    private void downloadImg(List<String> urlList) {
        subscriptions.add(
                Observable.from(urlList)
                        .flatMap(new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String url) {
                                return BitmapUtils.downloadBitmapFromUrl(url);
                            }
                        })
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<List<String>>() {
                                    @Override
                                    public void call(List<String> uriList) {
                                        sendUrisToPresenter(uriList);
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
     * Stops RxObservables execution
     *
     */
    @Override
    public void stop() {
        subscriptions.unsubscribe();
    }

    /**
     * Default action to perform
     *
     */
    @Override
    public void run() {
        getPhotos(0, DEFAULT_SEARCH_THERM);
    }
}
