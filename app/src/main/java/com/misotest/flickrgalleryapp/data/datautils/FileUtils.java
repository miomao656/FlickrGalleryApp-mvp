package com.misotest.flickrgalleryapp.data.datautils;

import android.os.Environment;

import java.io.File;

import timber.log.Timber;

/**
 * Helper class for handling file related actions
 */
public class FileUtils {

    private static final String APP_NAME = "FlickrGallery";

    /**
     * Check if file with certain file-name already exists.
     *
     * @param filename
     * @return
     */
    public static boolean isExistingFile(String filename) {
        File file = new File(filename);
        return file.exists();
    }

    /**
     * Deletes all files from given directory
     *
     * @param dir
     */
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            // The directory is now empty so delete it
            return dir.delete();
        }
        return false;
    }

    /**
     * Deletes file
     *
     * @param fileToDelete
     */
    public static boolean deleteFile(File fileToDelete) {
        return fileToDelete != null && fileToDelete.delete();
    }

    /**
     * Create output file
     *
     * @return
     */
    public static File getOutputMediaFile(String name) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                APP_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Timber.d(APP_NAME, "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String fileName = mediaStorageDir.getPath() + File.separator
                + name;
        mediaFile = new File(fileName);
        return mediaFile;
    }

    /**
     * Returns a file name by parsing url string
     *
     * @param extUrl
     * @return
     */
    public static String getFileNameFromUrl(String extUrl) {
        //URL: "http://photosaaaaa.net/photos-ak-snc1/v315/224/13/659629384/s659629384_752969_4472.jpg"
        String filename = "";
        //PATH: /photos-ak-snc1/v315/224/13/659629384/s659629384_752969_4472.jpg
        //Checks for both forward and/or backslash
        //NOTE:**While backslashes are not supported in URL's
        //most browsers will autoreplace them with forward slashes
        //So technically if you're parsing an html page you could run into
        //a backslash , so i'm accounting for them here;
        String[] pathContents = extUrl.split("[\\\\/]");
        if (pathContents != null) {
            int pathContentsLength = pathContents.length;
            System.out.println("Path Contents Length: " + pathContentsLength);
            for (int i = 0; i < pathContents.length; i++) {
                System.out.println("Path " + i + ": " + pathContents[i]);
            }
            //lastPart: s659629384_752969_4472.jpg
            String lastPart = pathContents[pathContentsLength - 1];
            String[] lastPartContents = lastPart.split("\\.");
            if (lastPartContents != null && lastPartContents.length > 1) {
                int lastPartContentLength = lastPartContents.length;
                System.out.println("Last Part Length: " + lastPartContentLength);
                //filenames can contain . , so we assume everything before
                //the last . is the name, everything after the last . is the
                //extension
                String name = "";
                for (int i = 0; i < lastPartContentLength; i++) {
                    System.out.println("Last Part " + i + ": " + lastPartContents[i]);
                    if (i < (lastPartContents.length - 1)) {
                        name += lastPartContents[i];
                        if (i < (lastPartContentLength - 2)) {
                            name += ".";
                        }
                    }
                }
                String extension = lastPartContents[lastPartContentLength - 1];
                filename = name + "." + extension;
            }
        }
        return filename;
    }
}
