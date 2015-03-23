package com.misotest.flickrgalleryapp.data.datautils;

import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import rx.Observable;
import rx.Subscriber;

/**
 * Util class for handling image related actions
 */
public class BitmapUtils {

    /**
     * Observable to download image from url on seperate thread and return path to temp file
     *
     * @param downUrl
     * @return
     */
    public static synchronized Observable<String> downloadBitmapFromUrl(final String downUrl) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(downloadFileFromUrl(downUrl, FileUtils.getFileNameFromUrl(downUrl)));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * Used to download bitmap image from url
     *
     * @param downloadUrl
     * @param fileName
     * @return
     */
    public static String downloadFileFromUrl(String downloadUrl, String fileName) {
        try {
            URL url = new URL(downloadUrl); //you can write here any link
           /* Open a connection to that URL. */
            URLConnection urlConnection = url.openConnection();
           /*
            * Define InputStreams to read from the URLConnection.
            */
            InputStream is = urlConnection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

           /*
            * Read bytes to the Buffer until there is nothing more to read(-1).
            */
            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            File image = FileUtils.getOutputMediaFile(fileName);
            if (fileName != null && image != null) {
           /* Convert the Bytes read to a String. */
                FileOutputStream fos = new FileOutputStream(image);
                fos.write(baf.toByteArray());
                fos.flush();
                fos.close();
                return image.getPath();
            }
            bis.close();
            baf.clear();
            is.close();
        } catch (IOException e) {
            Log.d("DownloadManager", "Error: " + e);
        }
        return null;
    }

}
