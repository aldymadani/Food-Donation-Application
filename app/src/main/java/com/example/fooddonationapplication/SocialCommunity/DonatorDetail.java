package com.example.fooddonationapplication.SocialCommunity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donator;

public class DonatorDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donator_detail);

        Intent intent = getIntent();
        Donator donator = intent.getParcelableExtra("Donator"); //TODO GET PICK UP ADDRESS ALSO AND PICKUP TIME
        String donatorName = donator.getName();
        String donatorPhoneNumber = donator.getPhone();
        String foodItems = donator.getFoodItems();
        String pickupDate = donator.getPickUpDate();
        String donationDate = donator.getDonationDate();
        String foodPhoto = donator.getImageURI();
        double totalDonation = donator.getTotalDonation();

        Log.d("CEK", donatorName);

        Toast.makeText(this, "This is donator detail interface", Toast.LENGTH_SHORT).show();
    }
}
