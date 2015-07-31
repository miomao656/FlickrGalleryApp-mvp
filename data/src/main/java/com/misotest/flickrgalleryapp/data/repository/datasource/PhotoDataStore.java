package com.misotest.flickrgalleryapp.data.repository.datasource;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;

import java.util.List;

import rx.Observable;

/**
 * Created by miomao on 7/26/15.
 */
public interface PhotoDataStore {

    /**
     * When search term not provided this is used
     */
    String DEFAULT_SEARCH_THERM = "akita";
    /**
     * Flickr rest api access key
     */
    String FLICKR_API_KEY = "90a9eddb63cbe3de1359aaf0e70778aa";
    /**
     * Flickr rest api response format
     */
    String FLICKR_FORMAT = "json";
    /**
     * Flickr rest api privacy filter off
     */
    int NO_PRIVACY_FILTER = 1;
    /**
     * Flickr rest api jsonp response off
     */
    int NO_JSONP_RESPONSE = 1;
    /**
     * Flickr rest api chosen image size
     */
    String IMAGE_SIZE = "Large";
    /**
     * Flickr rest api number of responses per page
     */
    int PHOTO_PER_PAGE = 30;


    Observable<List<PhotoDataEntity>> photos();

    Observable<PhotoDataEntity> photo();

}
