package com.example.fooddonationapplication.ui.social_community.event.list.child;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;

public class ChildEventListFragment extends Fragment {
    private static final String TAG = "EventHistoryFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection("events");

    private EventHistoryAdapter eventHistoryAdapter;
    private View rootView;
    private TextInputLayout searchInputLayout;
    private EditText searchField;
    private TextView emptyHistoryText;
    private ImageView searchButton, emptyHistoryImage;
    private SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;
    private SocialCommunity socialCommunity;
    private String eventArgs;

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

        final String socialCommunityId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        eventArgs = getArguments().getString(IntentNameExtra.EVENT_LIST_ARGUMENT, "");

        Log.d("SocialEventListFragment", "List Event Fragment initiated!");
        Log.d("SocialEventListFragment", "Event arguments: " + eventArgs);

        Query query = null;
        if (eventArgs.equalsIgnoreCase("currentEvent")) {
            query = eventRef.whereGreaterThan("endDateInMillis", System.currentTimeMillis()).whereEqualTo("socialCommunityId", uuid).orderBy("endDateInMillis", Query.Direction.ASCENDING);
            setUpRecyclerViewEventHistory(query, "current");
        } else if (eventArgs.equalsIgnoreCase("pastEvent")) {
            query = eventRef.whereLessThan("endDateInMillis", System.currentTimeMillis()).whereEqualTo("socialCommunityId", uuid).orderBy("endDateInMillis", Query.Direction.DESCENDING);
            setUpRecyclerViewEventHistory(query, "past");
        }

        // Retrieving data from activity
//        FragmentActivity fragmentActivity = requireActivity();
//        socialCommunity = fragmentActivity.getIntent().getParcelableExtra(IntentNameExtra.SOCIAL_COMMUNITY_MODEL);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        emptyHistoryImage.setVisibility(View.VISIBLE);
                        emptyHistoryText.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        searchField.setVisibility(View.VISIBLE);
                        searchInputLayout.setVisibility(View.VISIBLE);
                        searchButton.setVisibility(View.VISIBLE);
                        swipeLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchEvent(socialCommunityId);
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEvent(socialCommunityId);
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Query newQuery = null;
                searchInputLayout.setErrorEnabled(false);
                searchField.setText("");
                Util.hideKeyboard(requireActivity());
                searchField.clearFocus();
                if (eventArgs.equalsIgnoreCase("currentEvent")) {
                    newQuery = eventRef.whereGreaterThan("endDateInMillis", System.currentTimeMillis()).whereEqualTo("socialCommunityId", uuid).orderBy("endDateInMillis", Query.Direction.ASCENDING);
                    setUpRecyclerViewEventHistory(newQuery, "current");
                } else if (eventArgs.equalsIgnoreCase("pastEvent")) {
                    newQuery = eventRef.whereLessThan("endDateInMillis", System.currentTimeMillis()).whereEqualTo("socialCommunityId", uuid).orderBy("endDateInMillis", Query.Direction.DESCENDING);
                    setUpRecyclerViewEventHistory(newQuery, "past");
                }
                eventHistoryAdapter.refresh();
                swipeLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void searchEvent(String uuid) {
        Query newQuery = null;
        String search = searchField.getText().toString().toLowerCase();
        if (!search.isEmpty()) {
            Util.hideKeyboard(requireActivity());
            searchField.clearFocus();
            if (eventArgs.equalsIgnoreCase("currentEvent")) {
                newQuery = eventRef.whereLessThanOrEqualTo("titleForSearch",search + "z").whereGreaterThanOrEqualTo("titleForSearch", search).whereEqualTo("socialCommunityId", uuid);
                setUpRecyclerViewEventHistory(newQuery, "current");
                eventHistoryAdapter.startListening();
            } else if (eventArgs.equalsIgnoreCase("pastEvent")) {
                newQuery = eventRef.whereLessThanOrEqualTo("titleForSearch",search + "z").whereGreaterThanOrEqualTo("titleForSearch", search).whereEqualTo("socialCommunityId", uuid);
                setUpRecyclerViewEventHistory(newQuery, "past");
                eventHistoryAdapter.startListening();
            }
        }
    }

    private void setUpRecyclerViewEventHistory(Query query, String status) {
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

        eventHistoryAdapter = new EventHistoryAdapter(options, requireActivity(), swipeLayout, status);

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

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
