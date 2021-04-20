package com.hieu.doan.flashchat.Models;

public class Group {
    String id,name,imageUri;

    public Group(String id, String name, String imageUri) {
        this.id = id;
        this.name = name;
        this.imageUri = imageUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
