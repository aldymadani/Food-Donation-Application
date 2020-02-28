package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private String imageURI, eventID, title, description, socialCommunityID, socialCommunityName, endDate;
    private double totalDonation;
    private long endDateInMillis;

    public Event() {
        // Empty constructor required
    }

    public Event(String imageURI, String eventID, String title, String description, String socialCommunityID, String socialCommunityName, String endDate, int endDateInMillis, int totalDonation) {
        this.imageURI = imageURI;
        this.eventID = eventID;
        this.title = title;
        this.description = description;
        this.socialCommunityID = socialCommunityID;
        this.socialCommunityName = socialCommunityName;
        this.endDate = endDate;
        this.endDateInMillis = endDateInMillis;
        this.totalDonation = totalDonation;
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

    public String getSocialCommunityNameID() {
        return socialCommunityID;
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
        endDate = in.readString();
        totalDonation = in.readInt();
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
        dest.writeString(endDate);
        dest.writeDouble(totalDonation);
        dest.writeLong(endDateInMillis);
    }
}
