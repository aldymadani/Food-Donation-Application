package com.example.fooddonationapplication.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Donator implements Parcelable {
// TODO TIMESTAMP WATCH HERE https://www.youtube.com/watch?v=xnFnwbiDFuE
    public String name, phone, uuid, eventName, eventId, pickUpAddress, foodItems, pickUpDate, donationDate, imageURI;
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

    public String getPickUpAddress() {
        return pickUpAddress;
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

    protected Donator(Parcel in) {
        name = in.readString();
        phone = in.readString();
        uuid = in.readString();
        eventName = in.readString();
        eventId = in.readString();
        pickUpAddress = in.readString();
        foodItems = in.readString();
        pickUpDate = in.readString();
        donationDate = in.readString();
        imageURI = in.readString();
        totalDonation = in.readDouble();
    }

    public static final Creator<Donator> CREATOR = new Creator<Donator>() {
        @Override
        public Donator createFromParcel(Parcel in) {
            return new Donator(in);
        }

        @Override
        public Donator[] newArray(int size) {
            return new Donator[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(uuid);
        dest.writeString(eventName);
        dest.writeString(eventId);
        dest.writeString(pickUpAddress);
        dest.writeString(foodItems);
        dest.writeString(pickUpDate);
        dest.writeString(donationDate);
        dest.writeString(imageURI);
        dest.writeDouble(totalDonation);
    }

    public double getTotalDonation() {
        return totalDonation;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
