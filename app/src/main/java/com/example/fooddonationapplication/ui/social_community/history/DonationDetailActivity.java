package com.example.fooddonationapplication.ui.social_community.history;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donation;
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
    private TextView donatorNameTextView, donatorPhoneNumberTextView, donatorPickUpAddressTextView, donatorFoodItemsTextView, donatorPickUpDateTextView, donatorPickUpTimeTextView, donatorTotalDonationTextView, donatorDonationDateTextView, donatorDonationStatusEditText;
    private ProgressBar imageLoadingProgressBar, deleteProgressBar, updateDonationStatusProgressBar;
    private ImageView foodImagePhoto;
    private Button deleteDonation, updateDonationStatus;
    String donatorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

        donatorNameTextView = findViewById(R.id.donatorDetailName);
        donatorPhoneNumberTextView = findViewById(R.id.donatorDetailPhoneNumber);
        donatorPickUpAddressTextView = findViewById(R.id.donatorDetailPickUpAddress);
        donatorFoodItemsTextView = findViewById(R.id.donatorDetailFoodItems);
        donatorPickUpDateTextView = findViewById(R.id.donatorDetailDate);
        donatorPickUpTimeTextView = findViewById(R.id.donatorDetailTime);
        donatorTotalDonationTextView = findViewById(R.id.donatorDetailTotalDonation);
        donatorDonationDateTextView = findViewById(R.id.donatorDetailDonationDate);
        donatorDonationStatusEditText = findViewById(R.id.donatorDetailDonationStatus);
        imageLoadingProgressBar = findViewById(R.id.donatorDetailImageProgressBar);
        updateDonationStatusProgressBar = findViewById(R.id.donatorDetailUpdateStatusButtonProgressBar);
        foodImagePhoto = findViewById(R.id.donatorDetailFoodImage);
        deleteProgressBar = findViewById(R.id.donatorDetailButtonProgressBar);
        deleteDonation = findViewById(R.id.donatorDetailButton);
        updateDonationStatus = findViewById(R.id.donatorDetailUpdateStatusButton);

        deleteProgressBar.setVisibility(View.INVISIBLE);
        updateDonationStatusProgressBar.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        final Donation donation = intent.getParcelableExtra("Donator");
        String donatorName = donation.getName();
        String donatorPhoneNumber = donation.getPhone();
        String donatorPickUpAddress = donation.getPickUpAddress();
        String foodItems = donation.getFoodItems();
        String pickupDate = donation.getPickUpDate();
        String pickUpTime = donation.getPickUpTime();
        String donationDate = donation.getDonationDate();
        String foodPhoto = donation.getImageURI();
        final double totalDonation = donation.getTotalDonation();
        String donationStatus = donation.getStatus();
        donatorId = donation.getDonatorId();

        donatorNameTextView.setText(donatorName);
        donatorPhoneNumberTextView.setText(donatorPhoneNumber);
        donatorPickUpAddressTextView.setText(donatorPickUpAddress);
        donatorFoodItemsTextView.setText(foodItems);
        donatorPickUpDateTextView.setText(pickupDate);
        donatorPickUpTimeTextView.setText(pickUpTime);
        donatorTotalDonationTextView.setText(String.valueOf(totalDonation));
        donatorDonationDateTextView.setText(donationDate);
        donatorDonationStatusEditText.setText(donationStatus);
        disableAllTextField();

        if (donationStatus.equalsIgnoreCase("Completed")) {
            updateDonationStatus.setVisibility(View.GONE);
        }

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

        final MaterialDialog mDialog = new MaterialDialog.Builder(this)
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
                        DocumentReference donatorReference = db.collection("donators").document(donation.getDonatorId());
                        batch.delete(donatorReference);

                        double decreaseTotalDonation = totalDonation * -1;
                        Log.d(TAG, String.valueOf(decreaseTotalDonation));

                        DocumentReference userReference = db.collection("users").document(donation.getUuid());
                        batch.update(userReference, "totalDonation", FieldValue.increment(decreaseTotalDonation));
//                batch.update(userReference, "totalDonation", FieldValue.increment(decreaseTotalDonation), SetOptions.merge());


                        DocumentReference eventReference = db.collection("events").document(donation.getEventId());
                        batch.update(eventReference, "totalDonation", FieldValue.increment(decreaseTotalDonation));
//                batch.update(eventReference, "totalDonation", FieldValue.increment(decreaseTotalDonation), SetOptions.merge());

                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(donation.getImageURI());
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

        deleteDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show Dialog
                LottieAnimationView animationView = mDialog.getAnimationView();
                mDialog.show();

//                final DocumentReference userDocumentReference = db.collection("users").document(donator.getUuid());
//                final DocumentReference eventDocumentReference = db.collection("events").document(donator.getEventId());
//                final DocumentReference donatorReference = db.collection("donators").document(donator.getDonatorId());

//                db.runTransaction(new Transaction.Function<Void>() {
//                    @Override
//                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
//                        Log.d(TAG, String.valueOf(totalDonation));
//
//                        DocumentSnapshot snapshotUser = transaction.get(userDocumentReference);
//
//                        // Note: this could be done without a transaction
//                        //       by updating the population using FieldValue.increment()
//                        double newUserTotalDonation = snapshotUser.getDouble("totalDonation") - totalDonation;
//                        transaction.update(userDocumentReference, "totalDonation", newUserTotalDonation);
//                        Log.d(TAG, "newUserTotalDonation : " + newUserTotalDonation);
//
//                        DocumentSnapshot snapshotEvent = transaction.get(eventDocumentReference);
//                        double newEventTotalDonation = snapshotEvent.getDouble("totalDonation") - totalDonation;
//                        transaction.update(eventDocumentReference, "totalDonation", newEventTotalDonation);
//                        Log.d(TAG, "newEventTotalDonation : " + newEventTotalDonation);
//
//                        transaction.delete(donatorReference);
//                        // Success
//                        return null;
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "Transaction success!");
//                        Toast.makeText(DonatorDetail.this, "Donation is successfully deleted", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(), MainSocialCommunityActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        deleteDonation.setVisibility(View.VISIBLE);
//                        buttonProgressBar.setVisibility(View.INVISIBLE);
//                    }
//                })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Transaction failure.", e);
//                            }
//                        });



            }
        });

        updateDonationStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO add animation later on
                // TODO add disable all Edit Text
                deleteDonation.setEnabled(false);
                updateDonationStatus.setVisibility(View.INVISIBLE);
                updateDonationStatusProgressBar.setVisibility(View.VISIBLE);
                DocumentReference donationReference = db.collection("donators").document(donatorId);
                donationReference.update("status", "Completed")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // TODO backtrack to the previous activity
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

    private void disableAllTextField() {
        donatorNameTextView.setFocusable(false);
        donatorNameTextView.setFocusableInTouchMode(false);
        donatorNameTextView.setCursorVisible(false);

        donatorPhoneNumberTextView.setFocusable(false);
        donatorPhoneNumberTextView.setFocusableInTouchMode(false);
        donatorPhoneNumberTextView.setCursorVisible(false);

        donatorPickUpAddressTextView.setFocusable(false);
        donatorPickUpAddressTextView.setFocusableInTouchMode(false);
        donatorPickUpAddressTextView.setCursorVisible(false);

        donatorFoodItemsTextView.setFocusable(false);
        donatorFoodItemsTextView.setFocusableInTouchMode(false);
        donatorFoodItemsTextView.setCursorVisible(false);

        donatorPickUpDateTextView.setFocusable(false);
        donatorPickUpDateTextView.setFocusableInTouchMode(false);
        donatorPickUpDateTextView.setCursorVisible(false);

        donatorPickUpTimeTextView.setFocusable(false);
        donatorPickUpTimeTextView.setFocusableInTouchMode(false);
        donatorPickUpTimeTextView.setCursorVisible(false);

        donatorTotalDonationTextView.setFocusable(false);
        donatorTotalDonationTextView.setFocusableInTouchMode(false);
        donatorTotalDonationTextView.setCursorVisible(false);

        donatorDonationDateTextView.setFocusable(false);
        donatorDonationDateTextView.setFocusableInTouchMode(false);
        donatorDonationDateTextView.setCursorVisible(false);

        donatorDonationStatusEditText.setFocusable(false);
        donatorDonationStatusEditText.setFocusableInTouchMode(false);
        donatorDonationStatusEditText.setCursorVisible(false);
    }
}
