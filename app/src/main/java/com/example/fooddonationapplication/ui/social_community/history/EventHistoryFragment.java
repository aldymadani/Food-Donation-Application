package com.example.fooddonationapplication.ui.social_community.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EventHistoryFragment extends Fragment {

    private static final String TAG = "EventHistoryFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection("events");

    private EventHistoryAdapter eventHistoryAdapter;
    private View rootView;
    private TextInputLayout searchInputLayout;
    private EditText searchKeyword;
    private ImageView searchButton;
    private SwipeRefreshLayout swipeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_history,container,false);

        searchKeyword = rootView.findViewById(R.id.search);
        searchInputLayout = rootView.findViewById(R.id.search_layout);
        searchButton = rootView.findViewById(R.id.search_button);
        swipeLayout = rootView.findViewById(R.id.swipeLayout);
        final String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, uuid);
        Query query = eventRef.whereEqualTo("socialCommunityID", uuid);
        setUpRecyclerViewEventHistory(query);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query newQuery = null;
                String search = searchKeyword.getText().toString().toLowerCase();
                if (!search.isEmpty()) {
                    newQuery = eventRef.whereGreaterThanOrEqualTo("title", search).whereLessThanOrEqualTo("title",search + "z");
                } else {
                    searchInputLayout.setError("Please fill in");
                    return;
                }
                searchInputLayout.setErrorEnabled(false);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), gridColumnCount));
        recyclerView.setAdapter(eventHistoryAdapter);
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
