package com.example.fooddonationapplication.ui.social_community.history;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.adapter.DonationListAdapter;
import com.example.fooddonationapplication.model.Donation;
import com.example.fooddonationapplication.model.Event;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class DonationListFragment extends Fragment {

    private static final String TAG = "DonationListFragment";
    private TextView titleTotalDonation, donationListEmptyTextView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference donatorRef = db.collection("donations");
    private ImageView donationListEmptyImage;
    private RecyclerView recyclerView;

    private DonationListAdapter donatorAdapter;

    private View rootView;

    private String eventID;
    private String totalDonation;
    private MaterialSpinner sortBy;

    public DonationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_donation_list, container, false);
        sortBy = rootView.findViewById(R.id.donationListSpinner);
        donationListEmptyImage = rootView.findViewById(R.id.donationListEmptyImage);
        donationListEmptyTextView = rootView.findViewById(R.id.donationListEmptyTextView);
        titleTotalDonation = rootView.findViewById(R.id.donationListTitle);
        recyclerView = rootView.findViewById(R.id.donationListRecyclerView);

        sortBy.setVisibility(View.INVISIBLE);
        titleTotalDonation.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        donationListEmptyImage.setVisibility(View.INVISIBLE);
        donationListEmptyTextView.setVisibility(View.INVISIBLE);

        eventID = "";
        totalDonation = "";

        FragmentActivity fragmentActivity = requireActivity();
        Event eventData = fragmentActivity.getIntent().getParcelableExtra("eventData");
        if (eventData != null) {
            eventID = eventData.getEventID();
            Log.d(TAG, eventID);
        }

        Query query = donatorRef.whereEqualTo("eventId", eventID);
        setUpRecyclerViewDonator(query);

        sortBy.setItems("All", "On-Progress", "Completed");
        sortBy.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Toast.makeText(getContext(), "Clicked " + item, Toast.LENGTH_SHORT).show();
                Query newQuery = null;
                if (item.equals("All")) {
                    newQuery = donatorRef.whereEqualTo("eventId", eventID);
                } else if (item.equals("On-Progress")) {
                    newQuery = donatorRef.whereEqualTo("eventId", eventID).whereEqualTo("status", "On-Progress");
                } else if (item.equals("Completed")) {
                    newQuery = donatorRef.whereEqualTo("eventId", eventID).whereEqualTo("status", "Completed");
                }
                setUpRecyclerViewDonator(newQuery);
                donatorAdapter.startListening();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        donatorAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        donatorAdapter.startListening();
        CollectionReference eventRef = db.collection("events");
        eventRef.document(eventID).addSnapshotListener(requireActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.getDouble("totalDonation") <= 0) {
                        donationListEmptyImage.setVisibility(View.VISIBLE);
                        donationListEmptyTextView.setVisibility(View.VISIBLE);
                    } else {
                        sortBy.setVisibility(View.VISIBLE);
                        titleTotalDonation.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        DecimalFormat df = new DecimalFormat("#.###");
                        String formattedTotalDonation = df.format(documentSnapshot.getDouble("totalDonation"));
                        titleTotalDonation.setText("People have donated " + formattedTotalDonation + " Kg of food");
                    }
                }
            }
        });
    }

    private void setUpRecyclerViewDonator(Query query) {

        FirestoreRecyclerOptions<Donation> options = new FirestoreRecyclerOptions.Builder<Donation>()
                .setQuery(query, Donation.class)
                .build();

        recyclerView = rootView.findViewById(R.id.donationListRecyclerView);

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        donatorAdapter = new DonationListAdapter(options, getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.requireActivity(), gridColumnCount));
        recyclerView.setAdapter(donatorAdapter);
    }
}
