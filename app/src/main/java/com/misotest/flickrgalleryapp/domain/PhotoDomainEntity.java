package com.misotest.flickrgalleryapp.domain;

/**
 * Created by miso on 17.3.2015.
 */
public class PhotoDomainEntity {
    public String id;
    public String title;
    public String url;

    public PhotoDomainEntity(String id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }
}
