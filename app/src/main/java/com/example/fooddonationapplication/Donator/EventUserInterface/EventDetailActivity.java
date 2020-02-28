package com.example.fooddonationapplication.Donator.EventUserInterface;

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

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";

    TextView eventTitle, eventDescription, eventSocialCommunityName, eventTotalDonation, eventEndDate;
    Button eventDonateButton;
    ImageView eventImage;
    ProgressBar eventProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventTitle = findViewById(R.id.event_detail_title);
        eventDescription = findViewById(R.id.event_detail_description);
        eventSocialCommunityName = findViewById(R.id.event_detail_social_community);
        eventTotalDonation = findViewById(R.id.event_detail_total_donation);
        eventEndDate = findViewById(R.id.event_detail_end_date);
        eventDonateButton = findViewById(R.id.event_detail_donate_button);
        eventImage = findViewById(R.id.event_detail_image);
        eventDonateButton = findViewById(R.id.event_detail_donate_button);
        eventProgressBar = findViewById(R.id.event_detail_progressBar);

        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("Event");

        String eventTitleData = event.getTitle();
        String eventImageData = event.getImageURI();
        String eventDescriptionData = event.getDescription();
        String eventSocialCommunityNameData = event.getSocialCommunityName();
        String eventEndDateData = event.getEndDate();
        double eventTotalDonationData = event.getTotalDonation();

        eventTitle.setText(eventTitleData);
        // TODO SET IMAGE WITH A BETTER VIEW SIZE (CHECK YOUTUBE)
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
        eventDescription.setText(eventDescriptionData); // TODO SET XML TO MULTIPLE LINES MAYBE
        eventSocialCommunityName.setText(eventSocialCommunityNameData);
        eventTotalDonation.setText(String.valueOf(eventTotalDonationData));
        eventEndDate.setText(eventEndDateData);

        eventDonateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailActivity.this, DonateActivity.class);
                startActivity(intent);
            }
        });
    }


}
