package com.example.fooddonationapplication.ui.donator.history;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donation;
import com.example.fooddonationapplication.util.Util;

import java.text.DecimalFormat;

public class DonationHistoryDetailActivity extends AppCompatActivity {

    private static final String TAG = "HistoryDetail";

    private TextView eventName, socialCommunityName, socialCommunityTelephoneNumber;
    private EditText pickUpAddress, foodItems, pickUpDate, pickUpTime, totalDonation, donationDate;
    private ImageView foodPhoto;
    private ProgressBar foodImageProgressBar;
    private Button callButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_history_detail);

        eventName = findViewById(R.id.historyDetailEventNameText);
        socialCommunityName = findViewById(R.id.historyDetailOrganizedByText);

        pickUpAddress = findViewById(R.id.historyDetailPickUpAddress);
        foodItems = findViewById(R.id.historyDetailFoodItems);
        pickUpDate = findViewById(R.id.historyDetailDate);
        pickUpTime = findViewById(R.id.historyDetailTime);
        totalDonation = findViewById(R.id.historyDetailTotalDonation);
        donationDate = findViewById(R.id.historyDetailDonationDate);

        foodPhoto = findViewById(R.id.donationDetailFoodPhoto);
        foodImageProgressBar = findViewById(R.id.donationDetailFoodPhotoProgressBar);

        callButton = findViewById(R.id.eventDetailCallButton);

        Intent intent = getIntent();
        final Donation donation = intent.getParcelableExtra("Donator");
//        String socialCommunityName = donation.getSocialCommunityName();
//        String donatorPickUpAddress = donation.getPickUpAddress();
//        String foodItems = donation.getFoodItems();
//        String pickupDate = donation.getPickUpDate();
//        String pickUpTime = donation.getPickUpTime();
//        String donationDate = donation.getDonationDate();
//        String foodPhotoData = donation.getImageURI();
        double totalDonationData = donation.getTotalDonation();
        if (totalDonationData <= 0) {
            totalDonationData = 0;
        }

        eventName.setText(donation.getEventName());
        socialCommunityName.setText(donation.getSocialCommunityName());

        pickUpAddress.setText(donation.getPickUpAddress());
        foodItems.setText(donation.getFoodItems());
        pickUpDate.setText(Util.convertToFullDate(donation.getPickUpDate()));
        pickUpTime.setText(donation.getPickUpTime());
        DecimalFormat df = new DecimalFormat("#.###");
        final String formattedTotalDonation = df.format(totalDonationData);
        totalDonation.setText(formattedTotalDonation);
        donationDate.setText(Util.convertToFullDate(donation.getDonationDate()));

        Glide.with(this).load(donation.getImageURL())
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

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openCallIntent(DonationHistoryDetailActivity.this, donation.getSocialCommunityPhoneNumber());
            }
        });
    }
}
