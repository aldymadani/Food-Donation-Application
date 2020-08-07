package com.example.fooddonationapplication.ui.donator.history.child;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.adapter.DonationHistoryAdapter;
import com.example.fooddonationapplication.model.Donation;
import com.example.fooddonationapplication.model.Donator;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;

public class ChildDonationHistoryListFragment extends Fragment {

    private static final String TAG = "DonationHistoryFragment";

    private TextView totalDonationTextView, emptyDonationTextView;
    private ImageView emptyDonationImage;
    private RecyclerView recyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference donatorRef = db.collection("donations");
    private DonationHistoryAdapter donationHistoryAdapter;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private View rootView;
    private String donationArgs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_donation_history,container,false);
        emptyDonationImage = rootView.findViewById(R.id.donationEmptyLogo);
        recyclerView = rootView.findViewById(R.id.donationHistoryRecyclerView);
        emptyDonationTextView = rootView.findViewById(R.id.donationEmptyLogoText);

        recyclerView.setVisibility(View.INVISIBLE);
        emptyDonationImage.setVisibility(View.INVISIBLE);
        emptyDonationTextView.setVisibility(View.INVISIBLE);

        donationArgs = getArguments().getString(IntentNameExtra.DONATION_LIST_ARGUMENT, "");

        Log.d("SocialEventListFragment", "List Donation Fragment initiated!");
        Log.d("SocialEventListFragment", "Donation arguments: " + donationArgs);

        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = null;
        if (donationArgs.equalsIgnoreCase("onProgressDonations")) {
            query = donatorRef.whereEqualTo("donatorId", uuid).whereEqualTo("status", "On-Progress").orderBy("timestamp", Query.Direction.DESCENDING);
        } else if (donationArgs.equalsIgnoreCase("completedDonations")) {
            query = donatorRef.whereEqualTo("donatorId", uuid).whereEqualTo("status", "Completed").orderBy("timestamp", Query.Direction.DESCENDING);
        }

        setUpRecyclerViewDonationHistory(query);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        emptyDonationTextView.setVisibility(View.VISIBLE);
                        emptyDonationImage.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        donationHistoryAdapter.startListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        donationHistoryAdapter.stopListening();
    }

    private void setUpRecyclerViewDonationHistory(Query query) {
        FirestoreRecyclerOptions<Donation> options = new FirestoreRecyclerOptions.Builder<Donation>()
                .setQuery(query, Donation.class)
                .build();

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        donationHistoryAdapter = new DonationHistoryAdapter(options, getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.requireActivity(), gridColumnCount));
        recyclerView.setAdapter(donationHistoryAdapter);
    }
}
