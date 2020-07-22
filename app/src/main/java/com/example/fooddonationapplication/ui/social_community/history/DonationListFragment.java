package com.example.fooddonationapplication.ui.social_community.history;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.adapter.DonationListAdapter;
import com.example.fooddonationapplication.model.Donator;
import com.example.fooddonationapplication.model.Event;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jaredrummler.materialspinner.MaterialSpinner;

/**
 * A simple {@link Fragment} subclass.
 */
public class DonationListFragment extends Fragment {

    private static final String TAG = "DonationListFragment";
    private TextView titleTotalDonation;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference donatorRef = db.collection("donators");

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

//        Intent intent = getIntent().;
//        Event event = intent.getParcelableExtra("Event");
//        String eventID = getArguments().getString("eventID");
//        String totalDonation = getArguments().getString("totalDonation");

//        Bundle bundle = this.getArguments();
//        String eventID = bundle.getString("eventID");
//        String totalDonation = bundle.getString("totalDonation");

//        String eventID = getArguments().getString("eventID");
//        String totalDonation = getArguments().getString("totalDonation");

//        UpdateEventActivity updateEventActivity = (UpdateEventActivity) getActivity();
//        String eventID = updateEventActivity.getEventID();
//        String totalDonation = updateEventActivity.getTotalDonation();

        FragmentActivity fragmentActivity = requireActivity();
//        String eventID =  fragmentActivity.getIntent().getStringExtra("EventID");
//        String totalDonation = fragmentActivity.getIntent().getStringExtra("totalDonation");
        eventID = "";
        totalDonation = "";
        Event eventData = fragmentActivity.getIntent().getParcelableExtra("eventData");
        if (eventData != null) {
            eventID = eventData.getEventID();
            totalDonation = String.valueOf(eventData.getTotalDonation());
        }


        titleTotalDonation = rootView.findViewById(R.id.donationListTitle);
        titleTotalDonation.setText("People have donated " + totalDonation + " Kg of food");
        if (eventID != null) {
            Log.d(TAG, eventID);
        }
        Query query = donatorRef.whereEqualTo("eventId", eventID);
        setUpRecyclerViewDonator(query);

        sortBy.setItems("All", "On-Progress", "Completed");
        sortBy.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Toast.makeText(getContext(), "Clicked " + item, Toast.LENGTH_SHORT).show();
                Query newQuery = null;
                if(item.equals("All")) {
                    newQuery = donatorRef.whereEqualTo("eventId", eventID);
                } else if (item.equals("On-Progress")) {
                    newQuery = donatorRef.whereEqualTo("eventId", eventID).whereEqualTo("status", "on-progress");
                } else if (item.equals("Completed")) {
                    newQuery = donatorRef.whereEqualTo("eventId", eventID).whereEqualTo("status", "completed");
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
    }

    private void setUpRecyclerViewDonator(Query query) {
//        Query query = donatorRef.whereEqualTo("eventId", eventID);

        FirestoreRecyclerOptions<Donator> options = new FirestoreRecyclerOptions.Builder<Donator>()
                .setQuery(query, Donator.class)
                .build();

        RecyclerView recyclerView = rootView.findViewById(R.id.donationListRecyclerView);

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        donatorAdapter = new DonationListAdapter(options, getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), gridColumnCount));
        recyclerView.setAdapter(donatorAdapter);
    }
}
