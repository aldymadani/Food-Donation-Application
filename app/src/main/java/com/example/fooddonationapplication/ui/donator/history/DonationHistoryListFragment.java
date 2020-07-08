package com.example.fooddonationapplication.ui.donator.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddonationapplication.adapter.DonationHistoryAdapter;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donator;
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

    private TextView totalDonationTextView;

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
        setUpRecyclerViewDonationHistory();

        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String formattedTotalDonation;
                            DecimalFormat df = new DecimalFormat("#.###");
                            if (documentSnapshot.getDouble("totalDonation") > 0) {
                                formattedTotalDonation = df.format(documentSnapshot.getDouble("totalDonation"));
                            } else {
                                // TODO: Tambahkan gambar apabila belum ada donasi
                                formattedTotalDonation = "0";
                            }
                            totalDonationTextView.setText("You have donated " + formattedTotalDonation + " Kg of food");
                        }
                    }
                });
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
        Log.d(TAG, uuid);

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
