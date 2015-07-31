package com.misotest.flickrgalleryapp.domain.interactor;

import com.misotest.flickrgalleryapp.domain.repository.PhotoRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by miomao on 7/26/15.
 */
public class GetPhotosList extends UseCase {

    private final PhotoRepository photoRepository;

    @Inject
    public GetPhotosList(PhotoRepository repository) {
        this.photoRepository = repository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return this.photoRepository.photos();
    }
}
