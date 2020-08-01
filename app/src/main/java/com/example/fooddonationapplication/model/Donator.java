package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Donator implements Parcelable {
    private String name, phone, uuid;
    private int totalDonation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getTotalDonation() {
        return totalDonation;
    }

    public void setTotalDonation(int totalDonation) {
        this.totalDonation = totalDonation;
    }

    protected Donator(Parcel in) {
    }

    public boolean isSameDonator(User user) {
        boolean isSame = true;
        if (!name.equalsIgnoreCase(user.getName())) {
            isSame = false;
        }
        if (!phone.equalsIgnoreCase(user.getPhone())) {
            isSame = false;
        }
        return isSame;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
