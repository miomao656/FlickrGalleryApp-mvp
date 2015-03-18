package com.misotest.flickrgalleryapp.data.entity;

/**
 * Main data model class when working with rest and database
 *
 */
public class PhotoDataEntity {
    public int id;
    public String photo_id;
    public String photo_url;
    public String photo_title;
    public String file_path;

    public PhotoDataEntity(int id, String photo_id, String photo_url, String photo_title, String file_path) {
        this.id = id;
        this.photo_id = photo_id;
        this.photo_url = photo_url;
        this.photo_title = photo_title;
        this.file_path = file_path;
    }

    public PhotoDataEntity() {
    }
}
