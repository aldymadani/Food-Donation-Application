package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Donation implements Parcelable {
    public String name;
    public String phone;
    public String uuid;
    private String socialCommunityId;
    private String socialCommunityName;
    private String socialCommunityPhoneNumber;
    private String eventName;
    private String eventId;
    private String pickUpAddress;
    private String foodItems;
    private String pickUpDate;
    private String pickUpTime;
    private String donationDate;
    private String imageURI;
    private String donatorId;
    private String status;
    private double totalDonation;

    public String getSocialCommunityPhoneNumber() {
        return socialCommunityPhoneNumber;
    }

    public void setSocialCommunityPhoneNumber(String socialCommunityPhoneNumber) {
        this.socialCommunityPhoneNumber = socialCommunityPhoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Donation(){
        // Empty constructor
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setSocialCommunityId(String socialCommunityId) {
        this.socialCommunityId = socialCommunityId;
    }

    public void setSocialCommunityName(String socialCommunityName) {
        this.socialCommunityName = socialCommunityName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setPickUpAddress(String pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public void setFoodItems(String foodItems) {
        this.foodItems = foodItems;
    }

    public void setPickUpDate(String pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public void setDonationDate(String donationDate) {
        this.donationDate = donationDate;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setTotalDonation(double totalDonation) {
        this.totalDonation = totalDonation;
    }

    public void setDonatorId(String donatorId) {
        this.donatorId = donatorId;
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

    public String getSocialCommunityId() {
        return socialCommunityId;
    }

    public String getSocialCommunityName() {
        return socialCommunityName;
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

    public String getPickUpTime() {
        return pickUpTime;
    }

    public String getDonationDate() {
        return donationDate;
    }

    public String getImageURI() {
        return imageURI;
    }

    public String getDonatorId() {
        return donatorId;
    }

    protected Donation(Parcel in) {
        name = in.readString();
        phone = in.readString();
        uuid = in.readString();
        socialCommunityId = in.readString();
        socialCommunityName = in.readString();
        socialCommunityPhoneNumber = in.readString();
        eventName = in.readString();
        eventId = in.readString();
        pickUpAddress = in.readString();
        foodItems = in.readString();
        pickUpDate = in.readString();
        pickUpTime = in.readString();
        donationDate = in.readString();
        imageURI = in.readString();
        totalDonation = in.readDouble();
        donatorId = in.readString();
        status = in.readString();
    }

    public static final Creator<Donation> CREATOR = new Creator<Donation>() {
        @Override
        public Donation createFromParcel(Parcel in) {
            return new Donation(in);
        }

        @Override
        public Donation[] newArray(int size) {
            return new Donation[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(uuid);
        dest.writeString(socialCommunityId);
        dest.writeString(socialCommunityName);
        dest.writeString(socialCommunityPhoneNumber);
        dest.writeString(eventName);
        dest.writeString(eventId);
        dest.writeString(pickUpAddress);
        dest.writeString(foodItems);
        dest.writeString(pickUpDate);
        dest.writeString(pickUpTime);
        dest.writeString(donationDate);
        dest.writeString(imageURI);
        dest.writeDouble(totalDonation);
        dest.writeString(donatorId);
        dest.writeString(status);
    }

    public double getTotalDonation() {
        return totalDonation;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
