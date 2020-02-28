package com.example.fooddonationapplication.Donator.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddonationapplication.Donator.HistoryUserInterface.HistoryDetail;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.SocialCommunity.DonatorActivity;
import com.example.fooddonationapplication.SocialCommunity.DonatorDetail;
import com.example.fooddonationapplication.model.Donator;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class DonatorAdapter extends FirestoreRecyclerAdapter<Donator, DonatorAdapter.DonatorHolder> {

    private Context context;

    public DonatorAdapter(@NonNull FirestoreRecyclerOptions<Donator> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull DonatorHolder holder, int position, @NonNull Donator model) {
        holder.donatorName.setText(model.getName());
        holder.totalDonation.setText(String.valueOf(model.getTotalDonation()));
        holder.donationDate.setText(model.getDonationDate());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DonatorDetail.class);
                intent.putExtra("Donator", "Smtg"); // TODO add parcelable later on
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public DonatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.donator_item, parent, false);
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
