package com.example.fooddonationapplication.SocialCommunity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class DonatorDetail extends AppCompatActivity {

    TextView donatorNameTextView, donatorPhoneNumberTextView, donatorPickUpAddressTextView, donatorFoodItemsTextView, donatorPickUpDateTextView, donatorPickUpTimeTextView, donatorTotalDonationTextView, donatorDonationDateTextView;
    ProgressBar imageLoadingProgressBar, buttonProgressBar;
    ImageView foodImagePhoto;
    Button deleteDonation;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donator_detail);

        donatorNameTextView = findViewById(R.id.donatorDetailName);
        donatorPhoneNumberTextView = findViewById(R.id.donatorDetailPhoneNumber);
        donatorPickUpAddressTextView = findViewById(R.id.donatorDetailPickUpAddress);
        donatorFoodItemsTextView = findViewById(R.id.donatorDetailFoodItems);
        donatorPickUpDateTextView = findViewById(R.id.donatorDetailDate);
        donatorPickUpTimeTextView = findViewById(R.id.donatorDetailTime);
        donatorTotalDonationTextView = findViewById(R.id.donatorDetailTotalDonation);
        donatorDonationDateTextView = findViewById(R.id.donatorDetailDonationDate);
        imageLoadingProgressBar = findViewById(R.id.donatorDetailImageProgressBar);
        foodImagePhoto = findViewById(R.id.donatorDetailFoodImage);
        buttonProgressBar = findViewById(R.id.donatorDetailButtonProgressBar);
        deleteDonation = findViewById(R.id.donatorDetailButton);

        buttonProgressBar.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        final Donator donator = intent.getParcelableExtra("Donator"); //TODO GET PICK UP ADDRESS ALSO AND PICKUP TIME
        String donatorName = donator.getName();
        String donatorPhoneNumber = donator.getPhone();
        String donatorPickUpAddress = donator.getPickUpAddress();
        String foodItems = donator.getFoodItems();
        String pickupDate = donator.getPickUpDate();
        String pickUpTime = donator.getPickUpTime();
        String donationDate = donator.getDonationDate();
        String foodPhoto = donator.getImageURI();
        final double totalDonation = donator.getTotalDonation();

        donatorNameTextView.setText(donatorName);
        donatorPhoneNumberTextView.setText(donatorPhoneNumber);
        donatorPickUpAddressTextView.setText(donatorPickUpAddress);
        donatorFoodItemsTextView.setText(foodItems);
        donatorPickUpDateTextView.setText(pickupDate);
        donatorPickUpTimeTextView.setText(pickUpTime);
        donatorTotalDonationTextView.setText(String.valueOf(totalDonation));
        donatorDonationDateTextView.setText(donationDate);

        Glide.with(this).load(foodPhoto)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageLoadingProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageLoadingProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).error(R.drawable.ic_error_black_24dp).into(foodImagePhoto);

        deleteDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDonation.setVisibility(View.INVISIBLE);
                buttonProgressBar.setVisibility(View.VISIBLE);
                WriteBatch batch = db.batch();
                DocumentReference donatorReference = db.collection("donators").document(donator.getDonatorId());
                batch.delete(donatorReference);

                DocumentReference userReference = db.collection("users").document(donator.getUuid());
                batch.update(userReference, "totalDonation", FieldValue.increment(totalDonation * -1));

                DocumentReference eventReference = db.collection("events").document(donator.getEventId());
                batch.update(eventReference, "totalDonation", FieldValue.increment(totalDonation * -1));

                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(DonatorDetail.this, "Donation is successfully deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainSocialCommunityActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        deleteDonation.setVisibility(View.VISIBLE);
                        buttonProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }
}
