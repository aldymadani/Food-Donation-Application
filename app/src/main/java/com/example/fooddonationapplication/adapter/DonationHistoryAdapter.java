package com.example.fooddonationapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddonationapplication.ui.donator.history.DonationHistoryDetailActivity;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donator;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DecimalFormat;

public class DonationHistoryAdapter extends FirestoreRecyclerAdapter <Donator, DonationHistoryAdapter.DonationHistoryHolder>{

    private Context context;

    public DonationHistoryAdapter(@NonNull FirestoreRecyclerOptions<Donator> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull DonationHistoryHolder holder, int position, @NonNull final Donator model) {
        DecimalFormat df = new DecimalFormat("#.###");
        String formattedTotalDonation = df.format(model.getTotalDonation());

        holder.eventName.setText("Event Name : " + model.getEventName());
        holder.totalDonation.setText("Total Donation : " + formattedTotalDonation + " Kg");
        holder.donationDate.setText("Donation Date : " + model.getDonationDate());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DonationHistoryDetailActivity.class);
                intent.putExtra("Donator", model);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public DonationHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_history_item, parent, false);
        return new DonationHistoryHolder(v);
    }

    class DonationHistoryHolder extends RecyclerView.ViewHolder {

        TextView eventName;
        TextView totalDonation;
        TextView donationDate;
        CardView parentLayout;

        public DonationHistoryHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.donationHistoryEventName);
            totalDonation = itemView.findViewById(R.id.donatorTitleTotalDonation);
            donationDate = itemView.findViewById(R.id.donationHistoryDate);
            parentLayout = itemView.findViewById(R.id.donation_history_fragment_id);
        }
    }
}
