package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SocialCommunity extends User implements Parcelable {
    private String description, imageURI;
    private int totalEventCreated;
    private long notificationAvailabilityInMillis;

    public SocialCommunity() {
        // Empty constructor
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

    public boolean isSame(SocialCommunity user) {
        boolean isSame = super.isSame(user);
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

    protected SocialCommunity(Parcel in) {
        super.setName(in.readString());
        super.setPhone(in.readString());
        super.setUuid(in.readString());
        description = in.readString();
        imageURI = in.readString();
        totalEventCreated = in.readInt();
        notificationAvailabilityInMillis = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(super.getName());
        dest.writeString(super.getPhone());
        dest.writeString(super.getUuid());
        dest.writeString(description);
        dest.writeString(imageURI);
        dest.writeInt(totalEventCreated);
        dest.writeLong(notificationAvailabilityInMillis);
    }
}
