package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements Parcelable {
    private String name, phone, description, uuid, role, imageURI;
    private int totalEventCreated;
    private double totalDonation;

    public User(){
        // Empty constructor
    }

    public int getTotalEventCreated() {
        return totalEventCreated;
    }

    public void setTotalEventCreated(int totalEventCreated) {
        this.totalEventCreated = totalEventCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setTotalDonation(double totalDonation) {
        this.totalDonation = totalDonation;
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

    public double getTotalDonation() {
        return totalDonation;
    }

    public String getImageURI() {
        return imageURI;
    }

    public boolean isSameDonator(User user) {
        // TODO: Compare function
        boolean isSame = true;
        if (!name.equalsIgnoreCase(user.getName())) {
            isSame = false;
        }
        if (!phone.equalsIgnoreCase(user.getPhone())) {
            isSame = false;
        }
        return isSame;
    }

    public boolean isSameSocialCommunity(User user) {
        // TODO: Compare function
        boolean isSame = true;
        if (!phone.equalsIgnoreCase(user.getPhone())) {
            isSame = false;
        }
        if (!description.equalsIgnoreCase(user.getDescription())) {
            isSame = false;
        }
        return isSame;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    protected User(Parcel in) {
        name = in.readString();
        phone = in.readString();
        description = in.readString();
        uuid = in.readString();
        role = in.readString();
        totalDonation = in.readDouble();
        imageURI = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(description);
        dest.writeString(uuid);
        dest.writeString(role);
        dest.writeDouble(totalDonation);
        dest.writeString(imageURI);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
