package com.misotest.flickrgalleryapp.data.entity;

/**
 * Main data model class when working with rest and database
 */
public class PhotoDataEntity {
    public String photo_id;
    public String photo_title;
    public String photo_url;
    public String photo_file_path;

    public PhotoDataEntity(String photo_id, String photo_title, String photo_url, String photo_file_path) {
        this.photo_id = photo_id;
        this.photo_title = photo_title;
        this.photo_url = photo_url;
        this.photo_file_path = photo_file_path;
    }

    public PhotoDataEntity() {
    }
}
