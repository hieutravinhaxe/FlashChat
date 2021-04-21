package com.hieu.doan.flashchat.Models;

import java.util.ArrayList;

public class Friends {
    String id;
    String name;
    String image;
    String email;

    public Friends() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Friends(String name, String image, String id,String email) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.email = email;
    }

}
