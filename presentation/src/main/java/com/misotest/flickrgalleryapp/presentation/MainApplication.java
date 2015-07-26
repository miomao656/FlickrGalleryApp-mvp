package com.misotest.flickrgalleryapp.presentation;

import android.app.Application;
import android.content.Context;

import com.misotest.flickrgalleryapp.BuildConfig;

import timber.log.Timber;

/**
 * Root application class
 */
public class MainApplication extends Application {

    private static Context context;

    /**
     * Getter for root application context
     *
     */
    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
