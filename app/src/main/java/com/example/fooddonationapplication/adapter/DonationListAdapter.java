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

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donation;
import com.example.fooddonationapplication.ui.social_community.event.detail.DonationDetailActivity;
import com.example.fooddonationapplication.util.Util;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DecimalFormat;

public class DonationListAdapter extends FirestoreRecyclerAdapter<Donation, DonationListAdapter.DonatorHolder> {

    private Context context;

    public DonationListAdapter(@NonNull FirestoreRecyclerOptions<Donation> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull DonatorHolder holder, int position, @NonNull final Donation model) {
        DecimalFormat df = new DecimalFormat("#.###");
        String formattedTotalDonation = df.format(model.getTotalDonation());

        holder.donatorName.setText(model.getDonatorName());
        holder.totalDonation.setText(formattedTotalDonation + " kg");
        holder.donationDate.setText(Util.convertToFullDate(model.getDonationDate()));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DonationDetailActivity.class);
                intent.putExtra("Donator", model);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public DonatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
        return new DonatorHolder(v);
    }

    class DonatorHolder extends RecyclerView.ViewHolder {

        TextView donatorName;
        TextView totalDonation;
        TextView donationDate;
        CardView parentLayout;

        public DonatorHolder(@NonNull View itemView) {
            super(itemView);
            donatorName = itemView.findViewById(R.id.donatorName);
            totalDonation = itemView.findViewById(R.id.donatorTotalDonation);
            donationDate = itemView.findViewById(R.id.donatorDate);
            parentLayout = itemView.findViewById(R.id.donator_item_fragment_id);
        }
    }
}
