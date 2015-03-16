package com.misotest.flickrgalleryapp.domain.interactor;

import com.misotest.flickrgalleryapp.Constants;
import com.misotest.flickrgalleryapp.data.datautils.BitmapUtils;
import com.misotest.flickrgalleryapp.data.entity.PhotoElement;
import com.misotest.flickrgalleryapp.data.entity.PhotoEntity;
import com.misotest.flickrgalleryapp.data.entity.PhotosEntity;
import com.misotest.flickrgalleryapp.data.entity.SizeElement;
import com.misotest.flickrgalleryapp.data.rest.photosApi.PhotosApi;
import com.misotest.flickrgalleryapp.presentation.presenters.PhotosListPresenter;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class GetPhotosUseCaseImpl implements IGetPhotosUseCase {

    private PhotosListPresenter listPresenter;
    private Subscription subscription = Subscriptions.empty();
    private List<String> strings = Collections.emptyList();

    public GetPhotosUseCaseImpl(PhotosListPresenter listPresenter) {
        this.listPresenter = listPresenter;
    }

    @Override
    public void getPhotos(int page, String query) {
        if (!query.isEmpty()) {
            subscription = PhotosApi.photosApi.getPhotos(Constants.FLICKR_API_KEY, query, Constants.NO_PRIVACY_FILTER,
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
                                    fillList(strings);
                                }
                            }
                    );
        } else {
            run();
        }
    }

    @Override
    public void sendPhotosToPresenter(List<String> strings) {
        this.strings = strings;
        listPresenter.onImagesSaved(this.strings);
    }

    private void fillList(List<String> ids) {
        Observable.from(ids)
                .flatMap(new Func1<String, Observable<PhotoEntity>>() {
                    @Override
                    public Observable<PhotoEntity> call(String s) {
                        return PhotosApi.photosApi.getPhotoData(Constants.FLICKR_API_KEY, s,
                                Constants.FLICKR_FORMAT, Constants.NO_JSONP_RESPONSE);
                    }
                })
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
                .flatMap(new Func1<SizeElement, Observable<String>>() {
                    @Override
                    public Observable<String> call(SizeElement sizeElement) {
                        return Observable.just(sizeElement.source);
                    }
                })
                .toList()
                .subscribeOn(Schedulers.computation())
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
                );
    }

    private void downloadImg(List<String> ids) {
        Observable.from(ids)
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return BitmapUtils.downloadBitmapFromUrl(s);
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<String>>() {
                            @Override
                            public void call(List<String> strings) {
                                sendPhotosToPresenter(strings);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                );
    }

    @Override
    public void stop() {
        subscription.unsubscribe();
    }

    @Override
    public void run() {
        getPhotos(0, "cats");
    }
}
