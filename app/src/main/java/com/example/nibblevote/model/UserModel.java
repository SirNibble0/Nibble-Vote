package com.example.nibblevote.model;

public class UserModel {
    private String name, nationalId, email, password, image, uid;

    public UserModel() {
    }

    public UserModel(String name, String nationalId, String email, String password, String image, String uid) {
        this.name = name;
        this.nationalId = nationalId;
        this.email = email;
        this.password = password;
        this.image = image;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}