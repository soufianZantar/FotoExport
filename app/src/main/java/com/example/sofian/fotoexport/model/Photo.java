package com.example.sofian.fotoexport.model;

/**
 * Created by sofian on 19/03/2017.
 */
public class Photo {
    String id;
    String url;
    boolean checked;

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public boolean isChecked() {
        return checked;
    }

    public Photo(String id, String url, boolean checked) {
        this.id = id;
        this.url = url;
        this.checked = checked;
    }

    public void setId(String id) {
        this.id = id;

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
