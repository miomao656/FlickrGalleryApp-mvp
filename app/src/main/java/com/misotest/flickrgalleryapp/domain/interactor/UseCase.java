package com.misotest.flickrgalleryapp.domain.interactor;

/**
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 *
 * By convention each Interactor implementation will return the result using a Callback
 **/
public interface UseCase {

    void run();
}
