package com.misotest.flickrgalleryapp.presentation.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Helper class that provides Internet connection utility methods.
 *
 */
public final class ConnectionUtils {

    private ConnectionUtils() {
        // hide default constructor
    }

    /**
     * Method checks if network is available.
     *
     * @param ctx context to check in
     * @return true if network is available, otherwise false
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
