package com.example.fooddonationapplication.model;

public class User {
    private String name, phone, uuid, role;
    private int totalDonation;

    public User(){
        // Empty constructor
    }

    public User(String name, String phone, String uuid) {
        this.name = name;
        this.phone = phone;
        this.uuid = uuid;
        role = "donator";
        totalDonation = 0;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRole() {
        return role;
    }

    public int getTotalDonation() {
        return totalDonation;
    }
}
