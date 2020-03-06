package com.example.fooddonationapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.example.fooddonationapplication.Donator.EventUserInterface.EventDetailActivity;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class EventAdapter extends FirestoreRecyclerAdapter<Event, EventAdapter.EventHolder> {

    private Context context;

    public EventAdapter(@NonNull FirestoreRecyclerOptions<Event> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull final Event model) {
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewTotalDonation.setText("Total Donation : " + String.valueOf(model.getTotalDonation() + " / " + model.getTargetQuantity()) + " Kg");
        holder.textViewSocialCommunity.setText("End Date : " + model.getEndDate());

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
                }).error(R.drawable.ic_error_black_24dp).into(holder.imageViewEvent);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("Event", model);
                Log.d("CEK", model.getTotalDonation() + "\n" + model.getTargetQuantity());
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventHolder(v);
    }

    class EventHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewTotalDonation;
        TextView textViewSocialCommunity;
        ImageView imageViewEvent;
        CardView parentLayout;

        public EventHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.eventTitle);
            textViewTotalDonation = itemView.findViewById(R.id.eventTotalDonation);
            textViewSocialCommunity = itemView.findViewById(R.id.eventEndDate);
            imageViewEvent = itemView.findViewById(R.id.eventImage);
            parentLayout = itemView.findViewById(R.id.event_fragment_id);
        }
    }
}
