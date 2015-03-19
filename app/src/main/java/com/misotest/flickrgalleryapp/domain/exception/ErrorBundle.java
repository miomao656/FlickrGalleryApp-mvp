package com.misotest.flickrgalleryapp.domain.exception;

/**
 * Interface to represent a wrapper around an {@link Throwable} to manage errors.
 */
public interface ErrorBundle {

    Throwable getException();

    String getErrorMessage();
}
