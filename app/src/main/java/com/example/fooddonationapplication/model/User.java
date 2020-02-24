package com.example.fooddonationapplication.model;

public class User {
    public String name, phone, uuid, role;

    public User(){
        // Empty constructor
    }

    public User(String name, String phone, String uuid) {
        this.name = name;
        this.phone = phone;
        this.uuid = uuid;
        role = "donator";
    }

}
