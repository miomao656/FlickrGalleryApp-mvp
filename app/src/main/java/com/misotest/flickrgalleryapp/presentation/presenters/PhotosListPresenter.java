package com.misotest.flickrgalleryapp.presentation.presenters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.misotest.flickrgalleryapp.data.database.PhotosContentProvider;
import com.misotest.flickrgalleryapp.data.database.PhotosTable;
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

    public PhotosListPresenter(PhotoGridView photoGridView) {
        this.photoGridView = photoGridView;
    }

    public void onImagesSaved(List<String> urls) {
        Observable.from(urls)
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return Observable.just(s);
                    }
                })
                .map(new Func1<String, ContentValues>() {
                    @Override
                    public ContentValues call(String s) {
                        ContentValues values = new ContentValues();
                        values.put(PhotosTable.KEY_FILE_URI_LARGE, s);
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
                                List<String> urls = getUrisFromDb();
                                photoGridView.showItemsFromDiskUrl(urls);
                                photoGridView.hideLoading();
                            }
                        }
                );
    }

    private List<String> getUrisFromDb() {
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
                    urls.add(cursor.getString(3));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return urls;
    }

    @Override
    public void start() {
        photoGridView.showLoading();
        if (getUrisFromDb().isEmpty()) {
            useCase = new GetPhotosUseCaseImpl(this);
            useCase.getPhotos(0, "akita");
        } else {
            photoGridView.showItemsFromDiskUrl(getUrisFromDb());
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
        useCase.getPhotos(page, "akita");
    }
}
