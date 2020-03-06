package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements Parcelable {
    private String name, phone, uuid, role;
    private double totalDonation;

    public User(){
        // Empty constructor
    }

    public User(String name, String phone, String uuid, String role, double totalDonation) {
        this.name = name;
        this.phone = phone;
        this.uuid = uuid;
        this.role = role;
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
        uuid = in.readString();
        role = in.readString();
        totalDonation = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(uuid);
        dest.writeString(role);
        dest.writeDouble(totalDonation);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
