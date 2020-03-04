package com.example.fooddonationapplication.Donator.HistoryUserInterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donator;

public class HistoryDetail extends AppCompatActivity {

    TextView eventNameTextView, socialCommunityNameTextView, pickUpAddressTextView, foodItemsTextView, pickUpDateTextView, pickUpTimeTextView, totalDonationTextView, donationDateTextView;
    ImageView foodPhoto;
    ProgressBar foodImageProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        eventNameTextView = findViewById(R.id.historyDetailEventName);
        socialCommunityNameTextView = findViewById(R.id.historyDetailSocialCommunityName);
        pickUpAddressTextView = findViewById(R.id.historyDetailPickUpAddress);
        foodItemsTextView = findViewById(R.id.historyDetailFoodItems);
        pickUpDateTextView = findViewById(R.id.historyDetailDate);
        pickUpTimeTextView = findViewById(R.id.historyDetailTime);
        totalDonationTextView = findViewById(R.id.historyDetailTotalDonation);
        donationDateTextView = findViewById(R.id.historyDetailDonationDate);
        foodPhoto = findViewById(R.id.historyDetailFoodPhoto);
        foodImageProgressBar = findViewById(R.id.historyDetailFoodImageProgressBar);

        Intent intent = getIntent();
        Donator donator = intent.getParcelableExtra("Donator"); //TODO GET PICK UP ADDRESS ALSO AND PICKUP TIME
        String eventName = donator.getEventName();
        String socialCommunityName = donator.getSocialCommunityName();
        String donatorPickUpAddress = donator.getPickUpAddress();
        String foodItems = donator.getFoodItems();
        String pickupDate = donator.getPickUpDate();
        String pickUpTime = donator.getPickUpTime();
        String donationDate = donator.getDonationDate();
        String foodPhotoData = donator.getImageURI();
        double totalDonation = donator.getTotalDonation();

        eventNameTextView.setText(eventName);
        socialCommunityNameTextView.setText(socialCommunityName);
        pickUpAddressTextView.setText(donatorPickUpAddress);
        foodItemsTextView.setText(foodItems);
        pickUpDateTextView.setText(pickupDate);
        pickUpTimeTextView.setText(pickUpTime);
        totalDonationTextView.setText(String.valueOf(totalDonation));
        donationDateTextView.setText(donationDate);

        Glide.with(this).load(foodPhotoData)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        foodImageProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        foodImageProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).error(R.drawable.ic_error_black_24dp).into(foodPhoto);

        Toast.makeText(this, "History Detail Activity", Toast.LENGTH_SHORT).show();
    }
}
