package com.misotest.flickrgalleryapp.data.rest;

import com.misotest.flickrgalleryapp.Constants;

import retrofit.RestAdapter;

/**
 * Base class for REST calls.
 */
public class BaseApi {

    protected static final RestAdapter.Builder baseRestAdapterBuilder = new RestAdapter.Builder()
            //enable to see full log in the console
//            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(Constants.FLICKR_API_URL);
}
