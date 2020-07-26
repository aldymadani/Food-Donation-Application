package com.example.fooddonationapplication.viewmodel;

import androidx.lifecycle.ViewModel;

public class MainSocialCommunityViewModel extends ViewModel {
    private int lastSeen, position;

    public int getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(int lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
