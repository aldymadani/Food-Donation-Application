package com.example.fooddonationapplication.Donator.EventUserInterface;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.MainMenuActivity;
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
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull Event model) {
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewTotalDonation.setText("Total Donation : " + String.valueOf(model.getTotalDonation()));
        holder.textViewSocialCommunity.setText("Conducted By: " + model.getSocialCommunityName());
        final String eventID = model.getEventID();

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainMenuActivity.class);
                intent.putExtra("eventID", eventID);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);

        // TODO : https://www.youtube.com/watch?v=lAGI6jGS4vs Minute 5:21
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
            textViewSocialCommunity = itemView.findViewById(R.id.eventSocialCommunity);
            parentLayout = itemView.findViewById(R.id.event_fragment_id);
        }
    }
}
