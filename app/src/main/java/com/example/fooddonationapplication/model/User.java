package com.example.fooddonationapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String name, phone, uuid, role;

    public User(){
        // Empty constructor
    }

    public boolean isSame(User user) {
        boolean isSame = true;
        if (!name.equalsIgnoreCase(user.getName())) {
            isSame = false;
        }
        if (!phone.equalsIgnoreCase(user.getPhone())) {
            isSame = false;
        }
        return isSame;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRole() {
        return role;
    }
}
