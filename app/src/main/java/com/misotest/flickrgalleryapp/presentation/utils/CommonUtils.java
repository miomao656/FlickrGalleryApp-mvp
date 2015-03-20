package com.misotest.flickrgalleryapp.presentation.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

/**
 * Helper class that provides Internet connection utility methods.
 */
public final class CommonUtils {

    private CommonUtils() {
        // hide default constructor
    }

    /**
     * Share image with other apps
     *
     * @param image_path
     */
    public static void shareImage(Activity activity, String image_path) {
        Intent share = new Intent(Intent.ACTION_SEND);
        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image/*");
        File imageFileToShare = new File(image_path);
        if (imageFileToShare.exists()) {
            Uri uri = Uri.fromFile(imageFileToShare);
            share.putExtra(Intent.EXTRA_STREAM, uri);

            activity.startActivity(Intent.createChooser(share, "Share Image!"));
        } else {
            Toast.makeText(activity, "Unable to find image!", Toast.LENGTH_SHORT).show();
        }
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
