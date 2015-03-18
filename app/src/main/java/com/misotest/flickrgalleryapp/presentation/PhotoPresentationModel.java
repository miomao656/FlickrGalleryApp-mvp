package com.misotest.flickrgalleryapp.presentation;

/**
 * Created by miso on 17.3.2015.
 */
public class PhotoPresentationModel {

    public int id;
    public String photo_id;
    public String photo_url;
    public String photo_title;
    public String file_path;

    public PhotoPresentationModel(int id, String photo_id, String photo_url, String photo_title, String file_path) {
        this.id = id;
        this.photo_id = photo_id;
        this.photo_url = photo_url;
        this.photo_title = photo_title;
        this.file_path = file_path;
    }

    public PhotoPresentationModel(String photo_id, String photo_url, String photo_title, String file_path) {
        this.photo_id = photo_id;
        this.photo_url = photo_url;
        this.photo_title = photo_title;
        this.file_path = file_path;
    }

    public PhotoPresentationModel() {
    }
}
