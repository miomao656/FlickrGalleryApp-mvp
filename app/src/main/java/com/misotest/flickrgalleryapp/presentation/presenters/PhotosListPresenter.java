package com.misotest.flickrgalleryapp.presentation.presenters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.misotest.flickrgalleryapp.data.database.PhotosContentProvider;
import com.misotest.flickrgalleryapp.data.database.PhotoFilesTable;
import com.misotest.flickrgalleryapp.domain.interactor.GetPhotosUseCaseImpl;
import com.misotest.flickrgalleryapp.presentation.viewinterfaces.PhotoGridView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class PhotosListPresenter extends Presenter {

    private final PhotoGridView photoGridView;
    GetPhotosUseCaseImpl useCase;
    private String query;

    public PhotosListPresenter(PhotoGridView photoGridView) {
        this.photoGridView = photoGridView;
    }

    public void onImagesSaved(List<String> uriList) {
        Observable.from(uriList)
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String uri) {
                        return Observable.just(uri);
                    }
                })
                .map(new Func1<String, ContentValues>() {
                    @Override
                    public ContentValues call(String uri) {
                        ContentValues values = new ContentValues();
                        values.put(PhotoFilesTable.KEY_FILE_URI, uri);
                        return values;
                    }
                })
                .toList()
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
                                List<String> uriList = getUriListFromDb();
                                photoGridView.showItemsFromDiskUrl(uriList);
                                photoGridView.hideLoading();
                            }
                        }
                );
    }

    private List<String> getUriListFromDb() {
        List<String> urls = new ArrayList<String>();
        ContentResolver resolver = photoGridView.getContext().getContentResolver();
        String[] projection = PhotosContentProvider.PROJECTION;
        Cursor cursor =
                resolver.query(PhotosContentProvider.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                urls = new ArrayList<String>(cursor.getCount());
                do {
                    urls.add(cursor.getString(1));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return urls;
    }

    public void setQuery(String query){
        this.query = query;
    }

    @Override
    public void startPresenting() {
        photoGridView.showLoading();
        if (getUriListFromDb().isEmpty()) {
            useCase = new GetPhotosUseCaseImpl(this);
            useCase.getPhotos(0, query);
        } else {
            photoGridView.showItemsFromDiskUrl(getUriListFromDb());
            photoGridView.hideLoading();
        }
    }

    @Override
    public void stop() {
        if (useCase!=null) {
            useCase.stop();
        }
    }

    public void getPage(int page) {
        useCase.getPhotos(page, query);
    }
}
