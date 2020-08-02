package com.example.fooddonationapplication.ui.social_community.history;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donation;
import com.example.fooddonationapplication.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class DonationDetailActivity extends AppCompatActivity {

    private static final String TAG = "DonatorDetailActivity";
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView donatorNameTextView;
    private EditText donationDate, deliveryDate, deliveryTime, pickUpAddress, foodItems, donationQuantity, donationStatus;
    private ProgressBar imageLoadingProgressBar, deleteProgressBar, updateDonationStatusProgressBar;
    private ImageView foodImagePhoto;
    private Button callDonator, deleteDonation, updateDonationStatus;
    private String donationId, donatorId, eventId, imageURI;
    private double donationQuantityData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

        donatorNameTextView = findViewById(R.id.donationDetailDonatorByText);
        callDonator = findViewById(R.id.donationDetailCallDonatorButton);

        foodImagePhoto = findViewById(R.id.donationDetailFoodPhoto);
        imageLoadingProgressBar = findViewById(R.id.donationDetailFoodPhotoProgressBar);

        donationDate = findViewById(R.id.donationDetailDonationDate);
        deliveryDate = findViewById(R.id.donationDetailDeliveryDate);
        deliveryTime = findViewById(R.id.donationDetailDeliveryTime);
        pickUpAddress = findViewById(R.id.donationDetailPickUpAddress);
        foodItems = findViewById(R.id.donationDetailFoodItems);
        donationQuantity = findViewById(R.id.donationDetailTotalDonation);
        donationStatus = findViewById(R.id.donationDetailDonationStatus);

        deleteDonation = findViewById(R.id.donationDetailDeleteDonationButton);
        deleteProgressBar = findViewById(R.id.donatorDetailButtonProgressBar);

        updateDonationStatus = findViewById(R.id.donationDetailUpdateStatusButton);
        updateDonationStatusProgressBar = findViewById(R.id.donatorDetailUpdateStatusButtonProgressBar);

        Intent intent = getIntent();
        final Donation donation = intent.getParcelableExtra("Donator");
        donatorId = donation.getUuid();
        donationId = donation.getDonatorId();
        eventId = donation.getEventId();
        imageURI = donation.getImageURI();
        donationQuantityData = donation.getTotalDonation();

        donatorNameTextView.setText(donation.getName());

        Glide.with(this).load(imageURI)
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

        donationDate.setText(donation.getDonationDate());
        deliveryDate.setText(donation.getPickUpDate());
        deliveryTime.setText(donation.getPickUpTime());
        pickUpAddress.setText(donation.getPickUpAddress());
        foodItems.setText(donation.getFoodItems());
        donationQuantity.setText(String.valueOf(donation.getTotalDonation()));
        donationStatus.setText(donation.getStatus());

        disableAllTextField();

        if (donation.getStatus().equalsIgnoreCase("Completed")) {
            updateDonationStatus.setVisibility(View.GONE);
        }


        deleteDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Dialog
                setUpDeleteDialog();
            }
        });

        callDonator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openCallIntent(DonationDetailActivity.this, donation.getPhone());
            }
        });

        updateDonationStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO add disable all Edit Text
                deleteDonation.setEnabled(false);
                updateDonationStatus.setVisibility(View.INVISIBLE);
                updateDonationStatusProgressBar.setVisibility(View.VISIBLE);
                DocumentReference donationReference = db.collection("donators").document(donationId);
                donationReference.update("status", "Completed")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(DonationDetailActivity.this, "Status is updated to Completed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deleteDonation.setEnabled(true);
                        updateDonationStatus.setVisibility(View.VISIBLE);
                        updateDonationStatusProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }

    private void setUpDeleteDialog() {
        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setAnimation(R.raw.delete_animation)
                .setTitle("Delete Donation")
                .setMessage("Are you sure want to delete this donation?")
                .setCancelable(false)
                .setPositiveButton("Delete", R.drawable.ic_delete_forever_black_24dp, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // Hide the dialog again
                        dialogInterface.dismiss();

                        deleteDonation.setVisibility(View.INVISIBLE);
                        deleteProgressBar.setVisibility(View.VISIBLE);
                        updateDonationStatus.setEnabled(false);

                        // Delete Operation
                        WriteBatch batch = db.batch();
                        DocumentReference donatorReference = db.collection("donators").document(donationId);
                        batch.delete(donatorReference);

                        double decreaseTotalDonation = donationQuantityData * -1;
                        Log.d(TAG, String.valueOf(decreaseTotalDonation));

                        DocumentReference userReference = db.collection("users").document(donatorId);
                        batch.update(userReference, "totalDonation", FieldValue.increment(decreaseTotalDonation));
//                batch.update(userReference, "totalDonation", FieldValue.increment(decreaseTotalDonation), SetOptions.merge());


                        DocumentReference eventReference = db.collection("events").document(eventId);
                        batch.update(eventReference, "totalDonation", FieldValue.increment(decreaseTotalDonation));
//                batch.update(eventReference, "totalDonation", FieldValue.increment(decreaseTotalDonation), SetOptions.merge());

                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageURI);
                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                        Log.d(TAG, "onSuccess: deleted file");
                                        Toast.makeText(DonationDetailActivity.this, "Donation is successfully deleted", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(getApplicationContext(), MainSocialCommunityActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(intent);
                                        finish();
                                        deleteDonation.setVisibility(View.VISIBLE);
                                        deleteProgressBar.setVisibility(View.INVISIBLE);
                                        updateDonationStatus.setEnabled(true);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                        Log.d(TAG, "onFailure: did not delete file");
                                        deleteDonation.setVisibility(View.VISIBLE);
                                        deleteProgressBar.setVisibility(View.INVISIBLE);
                                        updateDonationStatus.setEnabled(true);
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", R.drawable.ic_cancel_black_24dp, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        mDialog.show();
    }

    private void disableAllTextField() {
        donationDate.setFocusable(false);
        donationDate.setFocusableInTouchMode(false);
        donationDate.setCursorVisible(false);

        deliveryDate.setFocusable(false);
        deliveryDate.setFocusableInTouchMode(false);
        deliveryDate.setCursorVisible(false);

        deliveryTime.setFocusable(false);
        deliveryTime.setFocusableInTouchMode(false);
        deliveryTime.setCursorVisible(false);

        pickUpAddress.setFocusable(false);
        pickUpAddress.setFocusableInTouchMode(false);
        pickUpAddress.setCursorVisible(false);

        foodItems.setFocusable(false);
        foodItems.setFocusableInTouchMode(false);
        foodItems.setCursorVisible(false);

        donationQuantity.setFocusable(false);
        donationQuantity.setFocusableInTouchMode(false);
        donationQuantity.setCursorVisible(false);

        donationStatus.setFocusable(false);
        donationStatus.setFocusableInTouchMode(false);
        donationStatus.setCursorVisible(false);
    }
}
