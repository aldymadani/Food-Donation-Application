package com.example.fooddonationapplication.ui.donator.event;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Event;

import java.text.DecimalFormat;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";

    private TextView eventTitle, eventDescription, eventSocialCommunityName, eventTotalDonation, eventEndDate, eventSocialCommunityTelephoneNumber;
    private Button eventDonateButton;
    private ImageView eventImage;
    private ProgressBar eventProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventTitle = findViewById(R.id.event_detail_title);
        eventDescription = findViewById(R.id.event_detail_description);
        eventSocialCommunityName = findViewById(R.id.event_detail_social_community);
        eventSocialCommunityTelephoneNumber = findViewById(R.id.event_detail_social_community_telephone_number);
        eventTotalDonation = findViewById(R.id.event_detail_total_donation);
        eventEndDate = findViewById(R.id.event_detail_end_date);
        eventDonateButton = findViewById(R.id.event_detail_donate_button);
        eventImage = findViewById(R.id.event_detail_image);
        eventDonateButton = findViewById(R.id.event_detail_donate_button);
        eventProgressBar = findViewById(R.id.event_detail_progressBar);

        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("Event");

        final String eventIDData = event.getEventID();
        final String eventTitleData = event.getTitle();
        String eventImageData = event.getImageURI();
        String eventDescriptionData = event.getDescription();
        final String eventSocialCommunityIdData = event.getSocialCommunityID();
        final String eventSocialCommunityNameData = event.getSocialCommunityName();
        String eventSocialCommunityTelephoneNumberData = event.getSocialCommunityTelephoneNumber();
        String eventEndDateData = event.getEndDate();
        double eventTargetDonationData = event.getTargetQuantity();
        double eventTotalDonationDataData = event.getTotalDonation();

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
        eventDescription.setText("Event Description :\n" + eventDescriptionData);
        eventSocialCommunityName.setText( "Conductor: " + eventSocialCommunityNameData);
        eventSocialCommunityTelephoneNumber.setText("Telephone number : " + eventSocialCommunityTelephoneNumberData);
        eventTotalDonation.setText(String.valueOf("Total Donation : " + formattedTotalDonation + " / " + eventTargetDonationData) + " Kg");
        eventEndDate.setText("Event End Date : " + eventEndDateData);

        eventDonateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailActivity.this, EventDonateActivity.class);
                intent.putExtra("eventID", eventIDData);
                intent.putExtra("eventName", eventTitleData);
                intent.putExtra("socialCommunityId", eventSocialCommunityIdData);
                intent.putExtra("socialCommunityName", eventSocialCommunityNameData);
                startActivity(intent);
            }
        });
    }


}
