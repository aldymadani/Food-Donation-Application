package com.example.fooddonationapplication.ui.social_community.history;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fooddonationapplication.adapter.EventHistoryAdapter;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Event;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DecimalFormat;

public class EventHistoryFragment extends Fragment {

    private static final String TAG = "EventHistoryFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection("events");

    private EventHistoryAdapter eventHistoryAdapter;
    private View rootView;
    private TextInputLayout searchInputLayout;
    private EditText searchKeyword;
    private TextView emptyHistoryText;
    private ImageView searchButton, emptyHistoryImage;
    private SwipeRefreshLayout swipeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_history,container,false);

        searchKeyword = rootView.findViewById(R.id.search);
        searchInputLayout = rootView.findViewById(R.id.search_layout);
        searchButton = rootView.findViewById(R.id.search_button);
        swipeLayout = rootView.findViewById(R.id.swipeLayout);
        emptyHistoryImage = rootView.findViewById(R.id.eventHistoryEmptyPicture);
        emptyHistoryText = rootView.findViewById(R.id.eventHistoryEmptyTextView);

        final String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, uuid);
        Query query = eventRef.whereEqualTo("socialCommunityID", uuid);
        setUpRecyclerViewEventHistory(query);

        searchKeyword.setVisibility(View.INVISIBLE);
        searchInputLayout.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        swipeLayout.setVisibility(View.INVISIBLE);
        emptyHistoryImage.setVisibility(View.INVISIBLE);
        emptyHistoryText.setVisibility(View.INVISIBLE);

        db.collection("users").document(uuid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.getDouble("totalEventCreated") > 0) {
                                searchKeyword.setVisibility(View.VISIBLE);
                                searchInputLayout.setVisibility(View.VISIBLE);
                                searchButton.setVisibility(View.VISIBLE);
                                swipeLayout.setVisibility(View.VISIBLE);
                            } else {
                                emptyHistoryImage.setVisibility(View.VISIBLE);
                                emptyHistoryText.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query newQuery = null;
                String search = searchKeyword.getText().toString().toLowerCase();
                if (!search.isEmpty()) {
                    newQuery = eventRef.whereGreaterThanOrEqualTo("titleForSearch", search).whereLessThanOrEqualTo("titleForSearch",search + "z");
                } else {
                    return;
                }
                setUpRecyclerViewEventHistory(newQuery);
                eventHistoryAdapter.startListening();
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Query newQuery = eventRef.whereEqualTo("socialCommunityID", uuid);
                searchInputLayout.setErrorEnabled(false);
                searchKeyword.setText("");
                hideKeyboard(requireActivity());
                searchKeyword.clearFocus();
                setUpRecyclerViewEventHistory(newQuery);
                eventHistoryAdapter.startListening();
                swipeLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void setUpRecyclerViewEventHistory(Query query) {
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        RecyclerView recyclerView = rootView.findViewById(R.id.eventHistoryRecyclerView);

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        eventHistoryAdapter = new EventHistoryAdapter(options, getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.requireActivity(), gridColumnCount));
        recyclerView.setAdapter(eventHistoryAdapter);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventHistoryAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        eventHistoryAdapter.startListening();
    }
}
