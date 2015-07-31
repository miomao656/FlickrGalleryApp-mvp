package com.misotest.flickrgalleryapp.data.database;

import com.misotest.flickrgalleryapp.data.entity.PhotoDataEntity;

import java.util.List;

import rx.Observable;

/**
 * Created by miomao on 7/26/15.
 */
public interface PhotosDbCache {

    /**
     * Gets an {@link rx.Observable} which will emit a {@link PhotoDataEntity}.
     *
     * @param photoId The user id to retrieve data.
     */
    Observable<PhotoDataEntity> get(final String photoId);

    Observable<List<PhotoDataEntity>> getList();

    /**
     * Puts and element into the cache.
     *
     * @param photoDataEntity Element to insert in the cache.
     */
    void put(PhotoDataEntity photoDataEntity);

    void putList(List<PhotoDataEntity> photoDataEntities);

    /**
     * Checks if an element (User) exists in the cache.
     *
     * @param photoId The id used to look for inside the cache.
     * @return true if the element is cached, otherwise false.
     */
    boolean isCached(final String photoId);

//    /**
//     * Checks if the cache is expired.
//     *
//     * @return true, the cache is expired, otherwise false.
//     */
//    boolean isExpired();

    /**
     * Evict all elements of the cache.
     */
    void evictAll();

    boolean isEmpty();
}
