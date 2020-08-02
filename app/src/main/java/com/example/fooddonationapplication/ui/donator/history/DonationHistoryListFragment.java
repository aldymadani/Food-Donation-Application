package com.example.fooddonationapplication.ui.donator.history;

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

import com.example.fooddonationapplication.adapter.DonationHistoryAdapter;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donation;
import com.example.fooddonationapplication.model.Donator;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DecimalFormat;

public class DonationHistoryListFragment extends Fragment {

    private static final String TAG = "DonationHistoryFragment";

    private TextView totalDonationTextView, emptyDonationTextView;
    private ImageView emptyDonationImage;
    private RecyclerView recyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference donatorRef = db.collection("donators");
    private DonationHistoryAdapter donationHistoryAdapter;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_donation_history,container,false);
        totalDonationTextView = rootView.findViewById(R.id.donatorTitleTotalDonation);
        emptyDonationImage = rootView.findViewById(R.id.donationEmptyLogo);
        recyclerView = rootView.findViewById(R.id.donationHistoryRecyclerView);
        emptyDonationTextView = rootView.findViewById(R.id.donationEmptyLogoText);

        recyclerView.setVisibility(View.INVISIBLE);
        emptyDonationImage.setVisibility(View.INVISIBLE);
        totalDonationTextView.setVisibility(View.INVISIBLE);
        emptyDonationTextView.setVisibility(View.INVISIBLE);

        setUpRecyclerViewDonationHistory();

        // Retrieving data from activity
        FragmentActivity fragmentActivity = requireActivity();
        Donator donator = fragmentActivity.getIntent().getParcelableExtra(IntentNameExtra.DONATOR_MODEL);
        if (donator.getTotalDonation() > 0) {
            DecimalFormat df = new DecimalFormat("#.###");
            String formattedTotalDonation = df.format(donator.getTotalDonation());
            totalDonationTextView.setText("You have donated " + formattedTotalDonation + " kg of food");
            recyclerView.setVisibility(View.VISIBLE);
            totalDonationTextView.setVisibility(View.VISIBLE);
        } else {
            emptyDonationTextView.setVisibility(View.VISIBLE);
            emptyDonationImage.setVisibility(View.VISIBLE);
        }
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

    private void setUpRecyclerViewDonationHistory() {
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = donatorRef.whereEqualTo("uuid", uuid);
        Log.d(TAG, uuid);

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
