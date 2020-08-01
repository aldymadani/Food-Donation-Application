package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Donator implements Parcelable {
    private String name, telephoneNumber, uuid;
    private double totalDonation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getTotalDonation() {
        return totalDonation;
    }

    public void setTotalDonation(int totalDonation) {
        this.totalDonation = totalDonation;
    }

    public boolean isSameDonator(User user) {
        boolean isSame = true;
        if (!name.equalsIgnoreCase(user.getName())) {
            isSame = false;
        }
        if (!telephoneNumber.equalsIgnoreCase(user.getPhone())) {
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

    protected Donator(Parcel in) {
        name = in.readString();
        telephoneNumber = in.readString();
        uuid = in.readString();
        totalDonation = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(telephoneNumber);
        dest.writeString(uuid);
        dest.writeDouble(totalDonation);
    }
}
