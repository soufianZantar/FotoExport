package com.example.sofian.fotoexport.model;

/**
 * Created by sofian on 18/03/2017.
 */
public class Album {
    String id;
    String name;
    int count;
    String photo;

    public Album(String id, String name, String photo,int count) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.count=count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
