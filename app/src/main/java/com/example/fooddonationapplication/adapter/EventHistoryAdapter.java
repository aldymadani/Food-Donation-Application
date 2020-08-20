package com.example.fooddonationapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.ui.social_community.event.detail.UpdateEventActivity;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.util.constant.Constant;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class EventHistoryAdapter extends FirestorePagingAdapter<Event, EventHistoryAdapter.EventHistoryViewHolder> {

    private static final String TAG = "EventHistoryAdapter";

    private String status;
    private Context context;
    private SwipeRefreshLayout swipeLayout;

    public EventHistoryAdapter(@NonNull FirestorePagingOptions<Event> options, Context context, SwipeRefreshLayout swipeRefresh, String status) {
        super(options);
        this.context = context;
        this.swipeLayout = swipeRefresh;
        this.status = status;
    }

    @NonNull
    @Override
    public EventHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_history, parent, false);
        return new EventHistoryViewHolder(view, status);
    }

    @Override
    protected void onError(@NonNull Exception e) {
        super.onError(e);
        Log.e(TAG, e.getMessage());
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHistoryViewHolder viewHolder, int position, @NonNull Event event) {
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
        String status;

        EventHistoryViewHolder(@NonNull View itemView, String status) {
            super(itemView);
            this.status = status;
            eventImage = itemView.findViewById(R.id.eventHistoryImage);
            eventTitle = itemView.findViewById(R.id.eventHistoryTitle);
            eventTotalDonation = itemView.findViewById(R.id.eventHistoryTotalDonationText);
            eventEndDate = itemView.findViewById(R.id.eventHistoryEndDateText);
            parentLayout = itemView.findViewById(R.id.eventHistoryLayout);
        }

        void bind(final Event event) {
            Log.d("EventHistoryAdapter", "getPosition: " + getAdapterPosition() + ", status: " + status + ", event title: " + event.getTitle());
            if (status.equalsIgnoreCase(Constant.CURRENT_EVENT)) {
                if (event.getEndDateInMillis() > System.currentTimeMillis()) {
                    setupData(event);
                } else {
                    Util.hide(parentLayout);
                    parentLayout.setLayoutParams(new RecyclerView.LayoutParams(1, 1));
                }
            } else if (status.equalsIgnoreCase(Constant.PAST_EVENT)) {
                if (event.getEndDateInMillis() < System.currentTimeMillis()) {
                    setupData(event);
                } else {
                    Util.hide(parentLayout);
                    parentLayout.setLayoutParams(new RecyclerView.LayoutParams(1, 1));
                }
            }
        }

        void setupData(final Event event) {
            DecimalFormat df = new DecimalFormat("#.###");
            final String formattedTotalDonation = df.format(event.getTotalDonation());
            final String formattedTargetQuantity = df.format(event.getTargetQuantity());

            eventTitle.setText(event.getTitle());
            eventTotalDonation.setText(formattedTotalDonation + " / " + formattedTargetQuantity + " kg");
            eventEndDate.setText(Util.convertToFullDate(event.getEndDate()));
            Picasso.get().load(event.getImageURL()).error(R.drawable.ic_error_black_24dp).into(eventImage);

            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), UpdateEventActivity.class);
                    intent.putExtra(IntentNameExtra.EVENT_DATA, event);
                    itemView.getContext().startActivity(intent);
                }
            });

             // Glide implementation for Future Reference
//            Glide.with(itemView.getContext()).load(event.getImageURL()).override(300,200)
//                    .listener(new RequestListener<Drawable>() {
//                        @Override
//                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            return false;
//                        }
//                    }).error(R.drawable.ic_error_black_24dp).into(eventImage);
        }
    }
}
