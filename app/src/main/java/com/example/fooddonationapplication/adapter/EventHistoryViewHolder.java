package com.example.fooddonationapplication.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.ui.social_community.history.UpdateEventActivity;

import java.text.DecimalFormat;

public class EventHistoryViewHolder extends RecyclerView.ViewHolder {

    ImageView eventImage;
    TextView eventTitle;
    TextView eventTotalDonation;
    TextView eventEndDate;
    TextView eventDescription;
    CardView parentLayout;

    public EventHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        eventImage = itemView.findViewById(R.id.eventHistoryImage);
        eventTitle = itemView.findViewById(R.id.eventHistoryTitle);
        eventTotalDonation = itemView.findViewById(R.id.eventHistoryTotalDonation);
        eventEndDate = itemView.findViewById(R.id.eventHistoryEndDate);
        eventDescription = itemView.findViewById(R.id.eventHistoryDescription);
        parentLayout = itemView.findViewById(R.id.event_history_fragment_id);
    }

    public void bind (final Event event) {
        DecimalFormat df = new DecimalFormat("#.###");
        final String formattedTotalDonation = df.format(event.getTotalDonation());

        eventTitle.setText(event.getTitle());
        eventTotalDonation.setText("Total Donation : " + formattedTotalDonation + " / " + event.getTargetQuantity() + " Kg");
        eventEndDate.setText("End Date : " + event.getEndDate());
        eventDescription.setText("Description : " + event.getDescription());
        // Picasso.get().load(event.getImageURI()).error(R.drawable.ic_error_black_24dp).into(eventPhoto);
        Glide.with(itemView.getContext()).load(event.getImageURI()).override(300,200)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        eventProgressBar.setVisibility(View.GONE); // TODO add the loading with progressbar if have enough time
                        return false;
                    }
                }).error(R.drawable.ic_error_black_24dp).into(eventImage);

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), UpdateEventActivity.class);
//                intent.putExtra("eventID", model.getEventID());
//                intent.putExtra("totalDonation", formattedTotalDonation);
                intent.putExtra("eventData", event);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
