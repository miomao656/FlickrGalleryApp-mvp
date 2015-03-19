package com.misotest.flickrgalleryapp.data.rest.photosApi;

import com.misotest.flickrgalleryapp.data.entity.PhotoEntity;
import com.misotest.flickrgalleryapp.data.entity.PhotosEntity;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Retrofit interface with declared methods for interacting with flickr api
 */
public interface IPhotosApi {

    @GET("/?method=flickr.photos.search")
    Observable<PhotosEntity> getPhotos(@Query("api_key") String apiKey,
                                       @Query("tags") String tags,
                                       @Query("privacy_filter") int privacyFilter,
                                       @Query("per_page") int perPage,
                                       @Query("page") int page,
                                       @Query("format") String format,
                                       @Query("nojsoncallback") int nojsoncallback
    );

    @GET("/?method=flickr.photos.getSizes")
    Observable<PhotoEntity> getPhotoData(@Query("api_key") String apiKey,
                                         @Query("photo_id") String photo_id,
                                         @Query("format") String format,
                                         @Query("nojsoncallback") int nojsoncallback
    );
}
