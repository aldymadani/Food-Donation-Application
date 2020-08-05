package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Event implements Parcelable {
    private String imageURI, eventId, title, description, socialCommunityId, endDate, titleForSearch;
    private double targetQuantity, totalDonation;
    private long endDateInMillis;
    private @ServerTimestamp Date timestamp;

    public String getTitleForSearch() {
        return titleForSearch;
    }

    public void setTitleForSearch(String titleForSearch) {
        this.titleForSearch = titleForSearch;
    }


    public Event() {
        // Empty constructor required
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSocialCommunityId(String socialCommunityId) {
        this.socialCommunityId = socialCommunityId;
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

    public Event(String imageURI, String eventid, String title, String description, String socialCommunityID, String socialCommunityName, String socialCommunityTelephoneNumber, String endDate, long endDateInMillis, double targetQuantity, double totalDonation, Date timestamp) {
        this.imageURI = imageURI;
        this.eventId = eventid;
        this.title = title;
        this.description = description;
        this.socialCommunityId = socialCommunityID;
        this.endDate = endDate;
        this.endDateInMillis = endDateInMillis;
        this.targetQuantity = targetQuantity;
        this.totalDonation = totalDonation;
        this.timestamp = timestamp;
    }

    public String getSocialCommunityId() {
        return socialCommunityId;
    }

    public double getTargetQuantity() {
        return targetQuantity;
    }

    public String getImageURI() {
        return imageURI;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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

    public boolean isSame(Event event) {
        boolean isSame = true;
        if (!description.equalsIgnoreCase(event.getDescription())) {
            isSame=  false;
        }

        if (!endDate.equalsIgnoreCase(event.getEndDate())) {
            isSame = false;
        }

        if (targetQuantity != event.getTargetQuantity()) {
            isSame = false;
        }

        return isSame;
    }

    protected Event(Parcel in) {
        imageURI = in.readString();
        eventId = in.readString();
        title = in.readString();
        description = in.readString();
        socialCommunityId = in.readString();
        endDate = in.readString();
        targetQuantity = in.readDouble();
        totalDonation = in.readDouble();
        endDateInMillis = in.readLong();
        titleForSearch = in.readString();
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
        dest.writeString(eventId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(socialCommunityId);
        dest.writeString(endDate);
        dest.writeDouble(targetQuantity);
        dest.writeDouble(totalDonation);
        dest.writeLong(endDateInMillis);
        dest.writeString(titleForSearch);
    }
}
