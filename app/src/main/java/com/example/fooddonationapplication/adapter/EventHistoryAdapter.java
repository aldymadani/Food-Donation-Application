package com.example.fooddonationapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.fooddonationapplication.SocialCommunity.DonatorActivity;
import com.example.fooddonationapplication.model.Event;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class EventHistoryAdapter extends FirestoreRecyclerAdapter<Event, EventHistoryAdapter.EventHistoryHolder> {

    private Context context;

    public EventHistoryAdapter(@NonNull FirestoreRecyclerOptions<Event> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHistoryHolder holder, int position, @NonNull final Event model) {
        holder.eventTitle.setText(model.getTitle());
        holder.eventTotalDonation.setText("Total Donation : " + String.valueOf(model.getTotalDonation()) + " / " + String.valueOf(model.getTargetQuantity()));
        holder.eventEndDate.setText("End Date : " + model.getEndDate());
        holder.eventDescription.setText("Description : " + model.getDescription());
        Glide.with(context).load(model.getImageURI()).override(300,200)
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
                }).error(R.drawable.ic_error_black_24dp).into(holder.eventImage);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DonatorActivity.class);
                intent.putExtra("eventID", model.getEventID());
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public EventHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_history_item, parent, false);
        return new EventHistoryHolder(v);
    }

    class EventHistoryHolder extends RecyclerView.ViewHolder {

        ImageView eventImage;
        TextView eventTitle;
        TextView eventTotalDonation;
        TextView eventEndDate;
        TextView eventDescription;
        CardView parentLayout;

        public EventHistoryHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventHistoryImage);
            eventTitle = itemView.findViewById(R.id.eventHistoryTitle);
            eventTotalDonation = itemView.findViewById(R.id.eventHistoryTotalDonation);
            eventEndDate = itemView.findViewById(R.id.eventHistoryEndDate);
            eventDescription = itemView.findViewById(R.id.eventHistoryDescription);
            parentLayout = itemView.findViewById(R.id.event_history_fragment_id);
        }
    }
}
