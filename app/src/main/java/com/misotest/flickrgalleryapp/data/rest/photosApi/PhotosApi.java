package com.misotest.flickrgalleryapp.data.rest.photosApi;


import com.misotest.flickrgalleryapp.data.rest.BaseApi;

import retrofit.RestAdapter;

/**
 * Created by miomao on 3/13/15.
 */
public class PhotosApi extends BaseApi {

    private static RestAdapter restAdapter = baseRestAdapterBuilder.build();
    public static IPhotosApi photosApi = restAdapter.create(IPhotosApi.class);
}