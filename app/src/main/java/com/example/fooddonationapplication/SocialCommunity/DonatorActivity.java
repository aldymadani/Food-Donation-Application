package com.example.fooddonationapplication.SocialCommunity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.fooddonationapplication.adapter.DonatorAdapter;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donator;
import com.example.fooddonationapplication.model.Event;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DonatorActivity extends AppCompatActivity {

    private static final String TAG = "DonatorActivity";
    private TextView titleTotalDonation;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference donatorRef = db.collection("donators");

    private DonatorAdapter donatorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donator);
        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("Event");
        String eventID = getIntent().getStringExtra("eventID");
        String totalDonation = getIntent().getStringExtra("totalDonation");
        if (Double.parseDouble(totalDonation) <= 0) {
            totalDonation = "0";
        }
        titleTotalDonation = findViewById(R.id.donatorTitleTotalDonation);
        titleTotalDonation.setText("People have donated " + totalDonation + " Kg of food");
        Log.d(TAG, eventID);
        setUpRecyclerViewDonator(eventID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        donatorAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        donatorAdapter.startListening();
    }

    private void setUpRecyclerViewDonator(String eventID) {
        Query query = donatorRef.whereEqualTo("eventId", eventID);

        FirestoreRecyclerOptions<Donator> options = new FirestoreRecyclerOptions.Builder<Donator>()
                .setQuery(query, Donator.class)
                .build();

        RecyclerView recyclerView = findViewById(R.id.donatorRecyclerView);

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        donatorAdapter = new DonatorAdapter(options, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridColumnCount));
        recyclerView.setAdapter(donatorAdapter);
    }
}
