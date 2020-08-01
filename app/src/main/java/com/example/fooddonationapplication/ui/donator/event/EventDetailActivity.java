package com.example.fooddonationapplication.ui.donator.event;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.ui.donator.MainDonatorActivity;
import com.example.fooddonationapplication.ui.general.LoginActivity;
import com.example.fooddonationapplication.ui.social_community.MainSocialCommunityActivity;
import com.example.fooddonationapplication.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";

    private CardView socialCommunityLayout;

    private TextView eventTitle, eventSocialCommunityName, eventSocialCommunityTelephoneNumber;
    private EditText eventDescription, eventEndDate, eventTotalDonation;
    private Button eventDonateButton, callButton;
    private ImageView eventImage, socialCommunityLogo;
    private ProgressBar eventProgressBar, socialCommunityLogoProgressBar;
    private String socialCommunityNameData, socialCommunityTelephoneNumberData, socialCommunityDescription, socialCommunityImageURI;
    private int totalEventCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventTitle = findViewById(R.id.event_detail_title);
        eventImage = findViewById(R.id.event_detail_image);
        eventProgressBar = findViewById(R.id.event_detail_progressBar);

        eventDescription = findViewById(R.id.eventDetailEventDescription);
        eventEndDate = findViewById(R.id.eventDetailEventEndDate);
        eventTotalDonation = findViewById(R.id.eventDetailEventTotalDonation);

        socialCommunityLayout = findViewById(R.id.eventDetailSocialCommunityLayout);
        socialCommunityLogo = findViewById(R.id.eventDetailSocialCommunityLogo);
        socialCommunityLogoProgressBar = findViewById(R.id.eventDetailSocialCommunityLogoProgressBar);
        eventSocialCommunityName = findViewById(R.id.eventDetailSocialCommunityName);
        eventSocialCommunityTelephoneNumber = findViewById(R.id.eventDetailSocialCommunityPhoneNumber);

        eventDonateButton = findViewById(R.id.event_detail_donate_button);
        callButton = findViewById(R.id.eventDetailCallButton);

        // Disable all Event Information Edit Text
        eventDescription.setFocusable(false);
        eventDescription.setFocusableInTouchMode(false);
        eventDescription.setCursorVisible(false);
        eventEndDate.setFocusable(false);
        eventEndDate.setFocusableInTouchMode(false);
        eventEndDate.setCursorVisible(false);
        eventTotalDonation.setFocusable(false);
        eventTotalDonation.setFocusableInTouchMode(false);
        eventTotalDonation.setCursorVisible(false);

        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("Event");

        final String eventIDData = event.getEventID();
        final String eventTitleData = event.getTitle();
        String eventImageData = event.getImageURI();
        String eventDescriptionData = event.getDescription();
        final String eventSocialCommunityIdData = event.getSocialCommunityID();
        String eventEndDateData = event.getEndDate();
        double eventTargetDonationData = event.getTargetQuantity();
        double eventTotalDonationDataData = event.getTotalDonation();
        final long endDateInMillis = event.getEndDateInMillis();

        // Initialize Event Information Data on the Edit Text
        eventDescription.setText(eventDescriptionData);
        eventEndDate.setText(Util.convertToFullDate(eventEndDateData));
        eventTotalDonation.setText(eventTotalDonationDataData + " / " + eventTargetDonationData + " kg");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference socialCommunityRef = db.collection("users").document(eventSocialCommunityIdData);
        db.collection("users").document(eventSocialCommunityIdData).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            socialCommunityTelephoneNumberData = documentSnapshot.getString("phone");
                            socialCommunityNameData = documentSnapshot.getString("name");
                            socialCommunityDescription  = documentSnapshot.getString("description");
                            socialCommunityImageURI = documentSnapshot.getString("imageURI");
                            totalEventCreated = documentSnapshot.getLong("totalEventCreated").intValue();

                            // Initialize Data on Social Community Layout
                            eventSocialCommunityName.setText(socialCommunityNameData);
                            eventSocialCommunityTelephoneNumber.setText(socialCommunityTelephoneNumberData);
                            Glide.with(getApplicationContext()).load(socialCommunityImageURI)
                                    .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        socialCommunityLogoProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        socialCommunityLogoProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).error(R.drawable.ic_error_black_24dp).into(socialCommunityLogo);
                        }
                    }
                });

        DecimalFormat df = new DecimalFormat("#.###");
        String formattedTotalDonation = df.format(eventTotalDonationDataData);

        if (event.getEndDateInMillis() <= System.currentTimeMillis()) {
            eventDonateButton.setVisibility(View.GONE);
        }

        eventTitle.setText(eventTitleData.toUpperCase());
        Glide.with(this).load(eventImageData).override(300,200)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        eventProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        eventProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).error(R.drawable.ic_error_black_24dp).into(eventImage);


        eventDonateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailActivity.this, CreateDonationActivity.class);
                intent.putExtra("eventID", eventIDData);
                intent.putExtra("eventName", eventTitleData);
                intent.putExtra("socialCommunityId", eventSocialCommunityIdData);
                intent.putExtra("socialCommunityName", socialCommunityNameData);
                intent.putExtra("endDateInMillis", endDateInMillis);
                startActivity(intent);
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openCallIntent(EventDetailActivity.this, socialCommunityTelephoneNumberData);
            }
        });

        socialCommunityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(EventDetailActivity.this);
                View socialCommunityDialog = getLayoutInflater().inflate(R.layout.fragment_profile_social_community, null);
                mBuilder.setView(socialCommunityDialog);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                ImageView socialCommunityProfile = socialCommunityDialog.findViewById(R.id.socialCommunityProfilePhoto);
                ImageView pencilIcon = socialCommunityDialog.findViewById(R.id.socialCommunityProfilePencilIcon);
                EditText socialCommunityName = socialCommunityDialog.findViewById(R.id.socialCommunityProfileFullName);
                EditText socialCommunityTelephoneNumber = socialCommunityDialog.findViewById(R.id.socialCommunityProfileTelephoneNumber);
                EditText socialCommunityDescriptionField = socialCommunityDialog.findViewById(R.id.socialCommunityProfileDescription);
                EditText socialCommunityTotalEventCreated = socialCommunityDialog.findViewById(R.id.socialCommunityProfileTotalEventCreated);
                Button updateCredentialButton = socialCommunityDialog.findViewById(R.id.socialCommunityProfileUpdateCredentialButton);
                Button updateProfileButton = socialCommunityDialog.findViewById(R.id.socialCommunityProfileUpdateButton);
                Button logOutButton = socialCommunityDialog.findViewById(R.id.socialCommunityProfileLogOutButton);
                final ProgressBar socialCommunityProfileProgressBar = socialCommunityDialog.findViewById(R.id.socialCommunityProfilePhotoProgressBar);
                ProgressBar updateCredentialProgressBar = socialCommunityDialog.findViewById(R.id.socialCommunityProfileUpdateCredentialProgressBar);
                ProgressBar updateProfileProgressBar = socialCommunityDialog.findViewById(R.id.socialCommunityProfileProgressBar);

                pencilIcon.setVisibility(View.GONE);
                updateCredentialButton.setVisibility(View.GONE);
                updateProfileButton.setVisibility(View.GONE);
                logOutButton.setVisibility(View.GONE);
                updateCredentialProgressBar.setVisibility(View.GONE);
                updateProfileProgressBar.setVisibility(View.GONE);

                Glide.with(getApplicationContext()).load(socialCommunityImageURI)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                socialCommunityProfileProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                socialCommunityProfileProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).error(R.drawable.ic_error_black_24dp).into(socialCommunityProfile);
                
                socialCommunityName.setText(socialCommunityNameData);
                socialCommunityTelephoneNumber.setText(socialCommunityTelephoneNumberData);
                socialCommunityDescriptionField.setText(socialCommunityDescription);
                socialCommunityTotalEventCreated.setText(String.valueOf(totalEventCreated));

                // Disable the Edit Text
                socialCommunityName.setFocusable(false);
                socialCommunityName.setFocusableInTouchMode(false);
                socialCommunityName.setCursorVisible(false);
                socialCommunityTelephoneNumber.setFocusable(false);
                socialCommunityTelephoneNumber.setFocusableInTouchMode(false);
                socialCommunityTelephoneNumber.setCursorVisible(false);
                socialCommunityDescriptionField.setFocusable(false);
                socialCommunityDescriptionField.setFocusableInTouchMode(false);
                socialCommunityDescriptionField.setCursorVisible(false);
                socialCommunityTotalEventCreated.setFocusable(false);
                socialCommunityTotalEventCreated.setFocusableInTouchMode(false);
                socialCommunityTotalEventCreated.setCursorVisible(false);
            }
        });
    }
}
