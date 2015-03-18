package com.misotest.flickrgalleryapp.presentation.entity;

/**
 * Model used for presenting photo in ui
 */
public class PhotoPresentationModel {

    public String photo_id;
    public String photo_title;
    public String photo_url;
    public String photo_file_path;

    public PhotoPresentationModel(String photo_id, String photo_title, String photo_url, String photo_file_path) {
        this.photo_id = photo_id;
        this.photo_title = photo_title;
        this.photo_url = photo_url;
        this.photo_file_path = photo_file_path;
    }

    public PhotoPresentationModel() {
    }
}
