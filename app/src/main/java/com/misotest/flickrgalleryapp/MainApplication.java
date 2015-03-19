package com.misotest.flickrgalleryapp;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.misotest.flickrgalleryapp.presentation.utils.RxBus;

import timber.log.Timber;

/**
 * Root application class
 */
public class MainApplication extends Application {

    private static RxBus mRxBus;

    private static Context context;

    //todo use rx bus as an event bus for communication between components in app
    public static RxBus getRxBusSingleton() {
        if (mRxBus == null) {
            mRxBus = new RxBus();
        }
        return mRxBus;
    }

    /**
     * Getter for root application context
     *
     * @return
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
        //debuging tool from facebook
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
