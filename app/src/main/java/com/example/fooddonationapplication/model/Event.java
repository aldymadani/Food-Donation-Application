package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Event implements Parcelable {
    private String imageURI, eventID, title, description, socialCommunityID, socialCommunityName, socialCommunityTelephoneNumber, endDate;
    private double targetQuantity, totalDonation;
    private long endDateInMillis;
    private @ServerTimestamp Date timestamp;

    public void setSocialCommunityTelephoneNumber(String socialCommunityTelephoneNumber) {
        this.socialCommunityTelephoneNumber = socialCommunityTelephoneNumber;
    }

    public Event() {
        // Empty constructor required
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSocialCommunityID(String socialCommunityID) {
        this.socialCommunityID = socialCommunityID;
    }

    public void setSocialCommunityName(String socialCommunityName) {
        this.socialCommunityName = socialCommunityName;
    }

    public String getSocialCommunityTelephoneNumber() {
        return socialCommunityTelephoneNumber;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setTargetQuantity(double targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    public void setTotalDonation(double totalDonation) {
        this.totalDonation = totalDonation;
    }

    public void setEndDateInMillis(long endDateInMillis) {
        this.endDateInMillis = endDateInMillis;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Event(String imageURI, String eventID, String title, String description, String socialCommunityID, String socialCommunityName, String socialCommunityTelephoneNumber, String endDate, long endDateInMillis, double targetQuantity, double totalDonation, Date timestamp) {
        this.imageURI = imageURI;
        this.eventID = eventID;
        this.title = title;
        this.description = description;
        this.socialCommunityID = socialCommunityID;
        this.socialCommunityName = socialCommunityName;
        this.socialCommunityTelephoneNumber = socialCommunityTelephoneNumber;
        this.endDate = endDate;
        this.endDateInMillis = endDateInMillis;
        this.targetQuantity = targetQuantity;
        this.totalDonation = totalDonation;
        this.timestamp = timestamp;
    }

    public String getSocialCommunityID() {
        return socialCommunityID;
    }

    public double getTargetQuantity() {
        return targetQuantity;
    }

    public String getImageURI() {
        return imageURI;
    }

    public String getEventID() {
        return eventID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSocialCommunityName() {
        return socialCommunityName;
    }

    public double getTotalDonation() {
        return totalDonation;
    }

    public String getEndDate() {
        return endDate;
    }

    public long getEndDateInMillis() {
        return endDateInMillis;
    }

    protected Event(Parcel in) {
        imageURI = in.readString();
        eventID = in.readString();
        title = in.readString();
        description = in.readString();
        socialCommunityID = in.readString();
        socialCommunityName = in.readString();
        socialCommunityTelephoneNumber = in.readString();
        endDate = in.readString();
        targetQuantity = in.readDouble();
        totalDonation = in.readDouble();
        endDateInMillis = in.readLong();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageURI);
        dest.writeString(eventID);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(socialCommunityID);
        dest.writeString(socialCommunityName);
        dest.writeString(socialCommunityTelephoneNumber);
        dest.writeString(endDate);
        dest.writeDouble(targetQuantity);
        dest.writeDouble(totalDonation);
        dest.writeLong(endDateInMillis);
    }
}
