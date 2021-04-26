package com.hieu.doan.flashchat.Models;

public class User {
    String id, email, phone, image, name, token;

    public User() {

    }

    public User(String id, String email, String phone, String image, String name, String token) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.name = name;
        this.token = token;
    }
    public User(String id, String email, String phone, String image, String name) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.name = name;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
