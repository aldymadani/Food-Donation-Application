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
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.ui.social_community.event.detail.UpdateEventActivity;
import com.example.fooddonationapplication.util.Util;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;

import java.text.DecimalFormat;

public class EventHistoryAdapter extends FirestorePagingAdapter<Event, EventHistoryAdapter.EventHistoryViewHolder> {

    private static final String TAG = "EventHistoryAdapter";

    private Context context;
    private SwipeRefreshLayout swipeLayout;

    public EventHistoryAdapter(@NonNull FirestorePagingOptions<Event> options, Context context, SwipeRefreshLayout swipeRefresh) {
        super(options);
        this.context = context;
        this.swipeLayout = swipeRefresh;
    }

    @NonNull
    @Override
    public EventHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_history, parent, false);
        return new EventHistoryViewHolder(view);
    }

    @Override
    protected void onError(@NonNull Exception e) {
        super.onError(e);
        Log.e(TAG, e.getMessage());
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHistoryViewHolder viewHolder, int position, @NonNull Event event) {
        // Bind to ViewHolder
        viewHolder.bind(event);
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

    static class EventHistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventTitle;
        TextView eventTotalDonation;
        TextView eventEndDate;
        CardView parentLayout;

        EventHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventHistoryImage);
            eventTitle = itemView.findViewById(R.id.eventHistoryTitle);
            eventTotalDonation = itemView.findViewById(R.id.eventHistoryTotalDonationText);
            eventEndDate = itemView.findViewById(R.id.eventHistoryEndDateText);
            parentLayout = itemView.findViewById(R.id.eventHistoryLayout);
        }

        void bind(final Event event) {
            DecimalFormat df = new DecimalFormat("#.###");
            final String formattedTotalDonation = df.format(event.getTotalDonation());

            eventTitle.setText(event.getTitle());
            eventTotalDonation.setText(formattedTotalDonation + " / " + event.getTargetQuantity() + " kg");
            eventEndDate.setText(Util.convertToFullDate(event.getEndDate()));
            // Picasso.get().load(event.getImageURI()).error(R.drawable.ic_error_black_24dp).into(eventPhoto);
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
}
