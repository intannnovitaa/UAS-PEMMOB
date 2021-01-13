package com.intan.datapenduduk.model;

public class User {
    int id;
    String name,email,phone,image;

    public User(int id, String name, String email, String phone,String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }
}
