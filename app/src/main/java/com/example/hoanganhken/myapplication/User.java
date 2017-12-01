package com.example.hoanganhken.myapplication;

/**
 * Created by Hoang Anh Ken on 12/1/2017.
 */

public class User {
    public String id;
    public String name;
    public String email;
    public String connecttion;
    public String cratedAt;
    public double latitude;
    public double longitude;

    public User() {
    }

    public User(String id, String name, String email, String connecttion, String cratedAt, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.connecttion = connecttion;
        this.cratedAt = cratedAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
