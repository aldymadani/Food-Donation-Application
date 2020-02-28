package com.example.fooddonationapplication.model;

import android.net.Uri;

public class Donator {

    public String name, phone, uuid, eventName, eventId, foodItems, pickUpDate, donationDate, imageURI;
    double totalDonation;

    public Donator(){
        // Empty constructor
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

    public String getEventName() {
        return eventName;
    }

    public String getEventId() {
        return eventId;
    }

    public String getFoodItems() {
        return foodItems;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public String getDonationDate() {
        return donationDate;
    }

    public String getImageURI() {
        return imageURI;
    }

    public double getTotalDonation() {
        return totalDonation;
    }
}
