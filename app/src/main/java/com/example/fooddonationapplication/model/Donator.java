package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Donator extends User implements Parcelable {
    private double totalDonation;

    public Donator() {
        // Empty constructor
    }

    public void setTotalDonation(int totalDonation) {
        this.totalDonation = totalDonation;
    }

    public double getTotalDonation() {
        return totalDonation;
    }

    public boolean isSame(Donator user) {
        boolean isSame = super.isSame(user);
        // Check any same data
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
        super.setName(in.readString());
        super.setPhone(in.readString());
        super.setUuid(in.readString());
        totalDonation = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(super.getName());
        dest.writeString(super.getPhone());
        dest.writeString(super.getUuid());
        dest.writeDouble(totalDonation);
    }
}
