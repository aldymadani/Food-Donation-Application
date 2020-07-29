package com.example.fooddonationapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.example.fooddonationapplication.ui.donator.event.EventDetailActivity;

import java.text.DecimalFormat;

public class EventViewHolder extends RecyclerView.ViewHolder {

    TextView textViewTitle;
    TextView textViewTotalDonation;
    TextView textViewSocialCommunity;
    ImageView imageViewEvent;
    CardView parentLayout;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewTitle = itemView.findViewById(R.id.eventTitle);
        textViewTotalDonation = itemView.findViewById(R.id.eventTotalDonation);
        textViewSocialCommunity = itemView.findViewById(R.id.eventEndDate);
        imageViewEvent = itemView.findViewById(R.id.eventImage);
        parentLayout = itemView.findViewById(R.id.event_fragment_id);
    }

    public void bind (final Event event) {
        DecimalFormat df = new DecimalFormat("#.###");
        String formattedTotalDonation = df.format(event.getTotalDonation());

        textViewTitle.setText(event.getTitle());
        textViewTotalDonation.setText("Total Donation : " + formattedTotalDonation + " / " + event.getTargetQuantity() + " Kg");
        textViewSocialCommunity.setText("End Date : " + event.getEndDate());
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
                }).error(R.drawable.ic_error_black_24dp).into(imageViewEvent);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), EventDetailActivity.class);
                intent.putExtra("Event", event);
                Log.d("CEK", event.getTotalDonation() + "\n" + event.getTargetQuantity());
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
