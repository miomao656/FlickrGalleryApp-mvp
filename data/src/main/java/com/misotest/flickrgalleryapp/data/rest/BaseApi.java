package com.misotest.flickrgalleryapp.data.rest;

import retrofit.RestAdapter;

/**
 * Base class for REST calls.
 */
public class BaseApi {

    private static final String FLICKR_API_URL = "https://api.flickr.com/services/rest";

    protected static final RestAdapter.Builder baseRestAdapterBuilder = new RestAdapter.Builder()
            //enable to see full log in the console
//            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(FLICKR_API_URL);
}
