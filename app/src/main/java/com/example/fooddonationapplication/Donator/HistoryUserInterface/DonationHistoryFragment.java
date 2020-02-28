package com.example.fooddonationapplication.Donator.HistoryUserInterface;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddonationapplication.Donator.adapter.DonationHistoryAdapter;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donator;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DonationHistoryFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference donatorRef = db.collection("Donators");

    private DonationHistoryAdapter donationHistoryAdapter;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_donation_history,container,false);
        setUpRecyclerViewDonationHistory();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        donationHistoryAdapter.startListening();;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        donationHistoryAdapter.stopListening();
    }

    private void setUpRecyclerViewDonationHistory() {
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = donatorRef.whereEqualTo("uuid", uuid);
        Log.d("CEK", uuid);

        FirestoreRecyclerOptions<Donator> options = new FirestoreRecyclerOptions.Builder<Donator>()
                .setQuery(query, Donator.class)
                .build();

        RecyclerView recyclerView = rootView.findViewById(R.id.donationHistoryRecyclerView);

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        donationHistoryAdapter = new DonationHistoryAdapter(options, getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), gridColumnCount));
        recyclerView.setAdapter(donationHistoryAdapter);
    }
}
