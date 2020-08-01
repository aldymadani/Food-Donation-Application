package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SocialCommunity implements Parcelable {
    private String name, phone, description, imageURI, uuid;
    private int totalEventCreated;
    private long notificationAvailabilityInMillis;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getTotalEventCreated() {
        return totalEventCreated;
    }

    public void setTotalEventCreated(int totalEventCreated) {
        this.totalEventCreated = totalEventCreated;
    }

    public long getNotificationAvailabilityInMillis() {
        return notificationAvailabilityInMillis;
    }

    public void setNotificationAvailabilityInMillis(long notificationAvailabilityInMillis) {
        this.notificationAvailabilityInMillis = notificationAvailabilityInMillis;
    }

    protected SocialCommunity(Parcel in) {
    }

    public boolean isSameSocialCommunity(User user) {
        boolean isSame = true;
        if (!phone.equalsIgnoreCase(user.getPhone())) {
            isSame = false;
        }
        if (!description.equalsIgnoreCase(user.getDescription())) {
            isSame = false;
        }
        return isSame;
    }

    public static final Creator<SocialCommunity> CREATOR = new Creator<SocialCommunity>() {
        @Override
        public SocialCommunity createFromParcel(Parcel in) {
            return new SocialCommunity(in);
        }

        @Override
        public SocialCommunity[] newArray(int size) {
            return new SocialCommunity[size];
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
