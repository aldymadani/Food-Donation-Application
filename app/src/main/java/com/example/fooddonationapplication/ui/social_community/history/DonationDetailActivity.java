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
import com.example.fooddonationapplication.model.Donator;
import com.example.fooddonationapplication.ui.social_community.MainSocialCommunityActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class DonationDetailActivity extends AppCompatActivity {

    private static final String TAG = "DonatorDetailActivity";
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView donatorNameTextView, donatorPhoneNumberTextView, donatorPickUpAddressTextView, donatorFoodItemsTextView, donatorPickUpDateTextView, donatorPickUpTimeTextView, donatorTotalDonationTextView, donatorDonationDateTextView;
    private ProgressBar imageLoadingProgressBar, buttonProgressBar;
    private ImageView foodImagePhoto;
    private Button deleteDonation;

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
        final Donator donator = intent.getParcelableExtra("Donator");
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
                        buttonProgressBar.setVisibility(View.VISIBLE);

                        // Delete Operation
                        WriteBatch batch = db.batch();
                        DocumentReference donatorReference = db.collection("donators").document(donator.getDonatorId());
                        batch.delete(donatorReference);

                        double decreaseTotalDonation = totalDonation * -1;
                        Log.d(TAG, String.valueOf(decreaseTotalDonation));

                        DocumentReference userReference = db.collection("users").document(donator.getUuid());
                        batch.update(userReference, "totalDonation", FieldValue.increment(decreaseTotalDonation));
//                batch.update(userReference, "totalDonation", FieldValue.increment(decreaseTotalDonation), SetOptions.merge());


                        DocumentReference eventReference = db.collection("events").document(donator.getEventId());
                        batch.update(eventReference, "totalDonation", FieldValue.increment(decreaseTotalDonation));
//                batch.update(eventReference, "totalDonation", FieldValue.increment(decreaseTotalDonation), SetOptions.merge());

                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(DonationDetailActivity.this, "Donation is successfully deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainSocialCommunityActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                deleteDonation.setVisibility(View.VISIBLE);
                                buttonProgressBar.setVisibility(View.INVISIBLE);
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
    }
}
