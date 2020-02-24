package com.example.fooddonationapplication.model;

public class Event {
    private String imageURI, eventID, title, description, socialCommunityID, socialCommunityName, endDate;
    private int totalDonation;
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

    public int getTotalDonation() {
        return totalDonation;
    }

    public String getEndDate() {
        return endDate;
    }

    public long getEndDateInMillis() {
        return endDateInMillis;
    }
}
