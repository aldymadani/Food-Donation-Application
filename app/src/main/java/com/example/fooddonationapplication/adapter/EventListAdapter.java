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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fooddonationapplication.ui.donator.event.EventDetailActivity;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.util.Util;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;

import java.text.DecimalFormat;

public class EventListAdapter extends FirestorePagingAdapter<Event, EventListAdapter.EventViewHolder> {
    private static final String TAG = "EventListAdapter";

    private Context context;
    private SwipeRefreshLayout swipeLayout;

    public EventListAdapter(@NonNull FirestorePagingOptions<Event> options, Context context, SwipeRefreshLayout swipeRefresh) {
        super(options);
        this.context = context;
        this.swipeLayout = swipeRefresh;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventViewHolder viewHolder, int i, @NonNull Event event) {
        // Bind to ViewHolder
        viewHolder.bind(event);
    }

    @Override
    protected void onError(@NonNull Exception e) {
        super.onError(e);
        Log.e(TAG, e.getMessage());
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state) {
            case LOADING_INITIAL:
            case LOADING_MORE:
                swipeLayout.setRefreshing(true);
                break;

            case LOADED:
                swipeLayout.setRefreshing(false);
                break;

            case ERROR:
                Toast.makeText(
                        context,
                        "Error Occurred!",
                        Toast.LENGTH_SHORT
                ).show();

                swipeLayout.setRefreshing(false);
                retry();
                break;

            case FINISHED:
                swipeLayout.setRefreshing(false);
                break;
        }
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewTotalDonation;
        TextView textViewEndDate;
        ImageView imageViewEvent;
        CardView parentLayout;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.eventTitle);
            textViewTotalDonation = itemView.findViewById(R.id.eventTotalDonationText);
            textViewEndDate = itemView.findViewById(R.id.eventEndDateText);
            imageViewEvent = itemView.findViewById(R.id.eventImage);
            parentLayout = itemView.findViewById(R.id.eventLayout);
        }

        void bind(final Event event) {
            DecimalFormat df = new DecimalFormat("#.###");
            String formattedTotalDonation = df.format(event.getTotalDonation());
            String formattedTargetQuantity = df.format(event.getTargetQuantity());

            textViewTitle.setText(event.getTitle());
            textViewTotalDonation.setText(formattedTotalDonation + " / " + formattedTargetQuantity + " kg");
            textViewEndDate.setText(Util.convertToFullDate(event.getEndDate()));
            Glide.with(itemView.getContext()).load(event.getImageURI()).override(300,200)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
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
}
