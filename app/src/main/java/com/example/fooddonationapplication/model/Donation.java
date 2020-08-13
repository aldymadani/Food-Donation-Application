package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Donation implements Parcelable {
    private String donatorName;
    private String donatorPhone;
    private String donatorId;
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
    private String imageURL;
    private String donationId;
    private String status;
    private double totalDonation;
    private @ServerTimestamp Date timestamp;

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

    public void setDonatorName(String donatorName) {
        this.donatorName = donatorName;
    }

    public void setDonatorPhone(String donatorPhone) {
        this.donatorPhone = donatorPhone;
    }

    public void setDonatorId(String donatorId) {
        this.donatorId = donatorId;
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

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setTotalDonation(double totalDonation) {
        this.totalDonation = totalDonation;
    }

    public void setDonationId(String donationId) {
        this.donationId = donationId;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDonatorName() {
        return donatorName;
    }

    public String getDonatorPhone() {
        return donatorPhone;
    }

    public String getDonatorId() {
        return donatorId;
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

    public String getImageURL() {
        return imageURL;
    }

    public String getDonationId() {
        return donationId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    protected Donation(Parcel in) {
        donatorName = in.readString();
        donatorPhone = in.readString();
        donatorId = in.readString();
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
        imageURL = in.readString();
        totalDonation = in.readDouble();
        donationId = in.readString();
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
        dest.writeString(donatorName);
        dest.writeString(donatorPhone);
        dest.writeString(donatorId);
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
        dest.writeString(imageURL);
        dest.writeDouble(totalDonation);
        dest.writeString(donationId);
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
