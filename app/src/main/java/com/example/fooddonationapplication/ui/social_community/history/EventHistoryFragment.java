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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.adapter.EventHistoryAdapter;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.model.SocialCommunity;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EventHistoryFragment extends Fragment {

    private static final String TAG = "EventHistoryFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection("events");

    private FirestorePagingAdapter eventHistoryAdapter;
    private View rootView;
    private TextInputLayout searchInputLayout;
    private EditText searchField;
    private TextView emptyHistoryText;
    private ImageView searchButton, emptyHistoryImage;
    private SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_history,container,false);

        recyclerView = rootView.findViewById(R.id.eventHistoryRecyclerView);
        searchField = rootView.findViewById(R.id.eventSearch);
        searchInputLayout = rootView.findViewById(R.id.eventSearchLayout);
        searchButton = rootView.findViewById(R.id.eventSearchButton);
        swipeLayout = rootView.findViewById(R.id.swipeLayout);
        emptyHistoryImage = rootView.findViewById(R.id.eventHistoryEmptyPicture);
        emptyHistoryText = rootView.findViewById(R.id.eventHistoryEmptyTextView);

        final String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, uuid);

        recyclerView.setVisibility(View.INVISIBLE);
        searchField.setVisibility(View.INVISIBLE);
        searchInputLayout.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        swipeLayout.setVisibility(View.INVISIBLE);
        emptyHistoryImage.setVisibility(View.INVISIBLE);
        emptyHistoryText.setVisibility(View.INVISIBLE);

        Query query = eventRef.whereEqualTo("socialCommunityID", uuid);
        setUpRecyclerViewEventHistory(query);

        // Retrieving data from activity
        FragmentActivity fragmentActivity = requireActivity();
        SocialCommunity socialCommunity = fragmentActivity.getIntent().getParcelableExtra(IntentNameExtra.SOCIAL_COMMUNITY_MODEL);

        if (socialCommunity == null) {
            Toast.makeText(fragmentActivity, "Data is NULL", Toast.LENGTH_SHORT).show();
            Util.backToLogin(fragmentActivity);
        }

        int totalEvent = socialCommunity.getTotalEventCreated();
        if (totalEvent > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            searchField.setVisibility(View.VISIBLE);
            searchInputLayout.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);
            swipeLayout.setVisibility(View.VISIBLE);
        } else {
            emptyHistoryImage.setVisibility(View.VISIBLE);
            emptyHistoryText.setVisibility(View.VISIBLE);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query newQuery = null;
                String search = searchField.getText().toString().toLowerCase();
                if (!search.isEmpty()) {
                    Util.hideKeyboard(requireActivity());
                    searchField.clearFocus();
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
                searchField.setText("");
                Util.hideKeyboard(requireActivity());
                searchField.clearFocus();
                setUpRecyclerViewEventHistory(newQuery);
                eventHistoryAdapter.refresh();
                swipeLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void setUpRecyclerViewEventHistory(Query query) {
        // Init Paging Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(1) // Remember that, the size you will pass to setPageSize() a method will load x3 items of that size at first load. Total initial is 3 https://medium.com/firebase-developers/firestore-pagination-in-android-using-firebaseui-library-1d7fe1a75704
                .build();

        // Init Adapter Configuration
        FirestorePagingOptions options = new FirestorePagingOptions.Builder<Event>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Event.class)
                .build();

        eventHistoryAdapter = new EventHistoryAdapter(options, requireActivity(), swipeLayout);
//        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
//                .setQuery(query, Event.class)
//                .build();

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

//        eventHistoryAdapter = new EventHistoryAdapter(options, getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.requireActivity(), gridColumnCount));
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
