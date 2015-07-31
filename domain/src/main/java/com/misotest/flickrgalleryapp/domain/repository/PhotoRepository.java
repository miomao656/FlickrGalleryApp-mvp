package com.misotest.flickrgalleryapp.domain.repository;

import com.misotest.flickrgalleryapp.domain.entity.PhotoDomainEntity;

import java.util.List;

import rx.Observable;

/**
 * Created by miomao on 7/26/15.
 */
public interface PhotoRepository {

    /**
     * Get an {@link rx.Observable} which will emit a List of {@link PhotoDomainEntity}.
     */
    Observable<List<PhotoDomainEntity>> photos();

    /**
     * Get an {@link rx.Observable} which will emit a {@link PhotoDomainEntity}.
     *
     * @param userId The user id used to retrieve user data.
     */
    Observable<PhotoDomainEntity> photo(final int userId);
}
