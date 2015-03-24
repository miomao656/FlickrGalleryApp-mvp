package com.misotest.flickrgalleryapp.presentation.mvp.presenters;

/**
 * Interface that represents a Presenter in the model view presenter Pattern
 * defines methods to manage the Activity / Fragment lifecycle
 */
public abstract class Presenter {

    /**
     * Called when the presenter is initialized
     */
    public abstract void startPresenting();

    /**
     * Called when the presenter is unregister, i.e when an activity
     * or a fragment finishes
     */
    public abstract void stop();
}
