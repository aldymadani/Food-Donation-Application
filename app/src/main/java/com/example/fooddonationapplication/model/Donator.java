package com.example.fooddonationapplication.model;

import android.net.Uri;

public class Donator {

    public String name, phone, uuid, eventName, eventId, foodItems, pickUpDate;
    public Uri photo;
    public Donator(){
        // Empty constructor
    }

    public Donator(String name, String phone, String uuid) {
        this.name = name;
        this.phone = phone;
        this.uuid = uuid;
    }
}
