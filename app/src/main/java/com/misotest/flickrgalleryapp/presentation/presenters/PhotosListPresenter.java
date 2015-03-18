package com.misotest.flickrgalleryapp.presentation.presenters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.misotest.flickrgalleryapp.MainApplication;
import com.misotest.flickrgalleryapp.data.database.PhotoFilesTable;
import com.misotest.flickrgalleryapp.data.database.PhotosContentProvider;
import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;
import com.misotest.flickrgalleryapp.domain.PhotoDomainEntity;
import com.misotest.flickrgalleryapp.domain.exception.ErrorBundle;
import com.misotest.flickrgalleryapp.domain.interactor.GetPhotosUseCaseImpl;
import com.misotest.flickrgalleryapp.domain.interactor.IGetPhotosUseCase;
import com.misotest.flickrgalleryapp.presentation.PhotoPresentationModel;
import com.misotest.flickrgalleryapp.presentation.mapper.PhotoPresentationModelMapper;
import com.misotest.flickrgalleryapp.presentation.viewinterfaces.PhotoGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * {@link Presenter} that controls communication between views and models of the presentation
 * layer.
 */
public class PhotosListPresenter extends Presenter {

    private final PhotoGridView photoGridView;
    private GetPhotosUseCaseImpl getPhotosUseCase;
    private String query;
    private CompositeSubscription subscription = new CompositeSubscription();
    private PhotoPresentationModelMapper mapper = new PhotoPresentationModelMapper();

    private final IGetPhotosUseCase.Callback callback = new IGetPhotosUseCase.Callback() {
        @Override
        public void onPhotoListLoaded(List<PhotoDataEntity> photosCollection) {
            photoGridView.hideLoading();
            showPhotoListInView(photosCollection);
        }

        @Override
        public void onError(ErrorBundle errorBundle) {
            photoGridView.hideLoading();
            photoGridView.showError(errorBundle.getErrorMessage());
        }
    };

    public PhotosListPresenter(PhotoGridView photoGridView) {
        this.photoGridView = photoGridView;
    }

    public void onImagesSaved(List<PhotoDomainEntity> uriList) {
        subscription.add(Observable.from(uriList)
                        .flatMap(new Func1<PhotoDomainEntity, Observable<PhotoDomainEntity>>() {
                            @Override
                            public Observable<PhotoDomainEntity> call(PhotoDomainEntity photoDomainEntity) {
                                return Observable.just(photoDomainEntity);
                            }
                        })
                        .map(new Func1<PhotoDomainEntity, ContentValues>() {
                            @Override
                            public ContentValues call(PhotoDomainEntity uri) {
                                ContentValues values = new ContentValues();
                                values.put(PhotoFilesTable.KEY_PHOTO_ID, uri.id);
                                values.put(PhotoFilesTable.KEY_PHOTO_TITLE, uri.title);
                                values.put(PhotoFilesTable.KEY_PHOTO_URL, uri.url);
//                                values.put(PhotoFilesTable.KEY_PHOTO_PATH, uri.);
                                return values;
                            }
                        })
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<List<ContentValues>>() {
                                    @Override
                                    public void call(List<ContentValues> contentValues) {
                                        photoGridView.getContext().getContentResolver()
                                                .bulkInsert(PhotosContentProvider.CONTENT_URI,
                                                        contentValues.toArray(new ContentValues[contentValues.size()])
                                                );
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                },
                                new Action0() {
                                    @Override
                                    public void call() {
                                        photoGridView.presentPhotoItems(getUriListFromDb());
                                        photoGridView.hideLoading();
                                    }
                                }
                        )
        );
    }

    private List<PhotoPresentationModel> getUriListFromDb() {
        List<PhotoPresentationModel> urls = Collections.emptyList();
        ContentResolver resolver = MainApplication.getContext().getContentResolver();
        String[] projection = PhotosContentProvider.PROJECTION;
        Cursor cursor =
                resolver.query(PhotosContentProvider.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                urls = new ArrayList<PhotoPresentationModel>(cursor.getCount());
                do {
                    urls.add(new PhotoPresentationModel(cursor.getInt(0),
                                    cursor.getString(1),
                                    cursor.getString(2),
                                    cursor.getString(3),
                                    cursor.getString(4)
                            )
                    );
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return urls;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public void startPresenting() {
        photoGridView.showLoading();
        if (getUriListFromDb().isEmpty()) {
            getPhotosUseCase = new GetPhotosUseCaseImpl();
            getPage(0, query);
        } else {
            photoGridView.presentPhotoItems(getUriListFromDb());
            photoGridView.hideLoading();
        }
    }

    @Override
    public void stop() {
        if (getPhotosUseCase != null) {
            getPhotosUseCase.unregister();
        }
    }

    private void showPhotoListInView(List<PhotoDataEntity> usersCollection) {
        final List<PhotoPresentationModel> userModelsCollection =
                this.mapper.transform(usersCollection);
        photoGridView.presentPhotoItems(userModelsCollection);
    }

    public void getPage(int page, String query) {
        getPhotosUseCase.execute(callback);
        getPhotosUseCase.requestPhotos(page, query, callback);
    }
}
