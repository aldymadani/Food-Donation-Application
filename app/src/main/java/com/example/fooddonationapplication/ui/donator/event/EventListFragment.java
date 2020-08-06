package com.example.fooddonationapplication.ui.donator.event;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.example.fooddonationapplication.adapter.EventListAdapter;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class EventListFragment extends Fragment {
    private static final String TAG = "EventListFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection("events");

    private EventListAdapter mAdapter;
    private RecyclerView recyclerView;
    private ImageView searchButton, emptyEventImage;
    private TextView emptyEventTextView;
    private TextInputLayout searchKeywordLayout;
    private EditText searchKeyword;
    private SwipeRefreshLayout swipeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_list, container,false);
        recyclerView = rootView.findViewById(R.id.event_recyclerView);
        swipeLayout = rootView.findViewById(R.id.eventSwipeLayout);

        searchKeyword = rootView.findViewById(R.id.eventSearch);
        searchKeywordLayout = rootView.findViewById(R.id.eventSearchLayout);
        searchButton = rootView.findViewById(R.id.eventSearchButton);
        emptyEventImage = rootView.findViewById(R.id.eventEmptyPicture);
        emptyEventTextView =rootView.findViewById(R.id.eventEmptyTextView);

        searchKeyword.setVisibility(View.INVISIBLE);
        searchKeywordLayout.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        swipeLayout.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        emptyEventImage.setVisibility(View.INVISIBLE);
        emptyEventTextView.setVisibility(View.INVISIBLE);

        final Query query;
        // Retrieving data from activity
        FragmentActivity fragmentActivity = requireActivity();
        String eventIdFromNotification = fragmentActivity.getIntent().getStringExtra(IntentNameExtra.NOTIFICATION_EVENT_ID);
        if (eventIdFromNotification != null) {
            query = eventRef.whereEqualTo("eventId", eventIdFromNotification);
        } else {
            query = eventRef.whereGreaterThanOrEqualTo("endDateInMillis", System.currentTimeMillis()).orderBy("endDateInMillis");
        }
        setUpRecyclerView(query);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        emptyEventImage.setVisibility(View.VISIBLE);
                        emptyEventTextView.setVisibility(View.VISIBLE);
                    } else {
                        searchKeyword.setVisibility(View.VISIBLE);
                        searchKeywordLayout.setVisibility(View.VISIBLE);
                        searchButton.setVisibility(View.VISIBLE);
                        swipeLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        searchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchEvent();
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEvent();
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Query newQuery = eventRef.whereGreaterThanOrEqualTo("endDateInMillis", System.currentTimeMillis()).orderBy("endDateInMillis");
                searchKeywordLayout.setErrorEnabled(false);
                searchKeyword.setText("");
                Util.hideKeyboard(requireActivity());
                searchKeyword.clearFocus();
                setUpRecyclerView(newQuery);
                mAdapter.refresh();
                swipeLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void searchEvent() {
        Query newQuery = null;
        String search = searchKeyword.getText().toString().toLowerCase();
        if (!searchKeyword.getText().toString().isEmpty()) {
            Util.hideKeyboard(requireActivity());
            searchKeyword.clearFocus();
            newQuery = eventRef.whereGreaterThanOrEqualTo("titleForSearch", search).whereLessThanOrEqualTo("titleForSearch",search + "z");
        } else {
            return;
        }
        setUpRecyclerView(newQuery);
        mAdapter.startListening();;
    }

    private void setUpRecyclerView(Query query) {
        // Init Paging Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(3) // Remember that, the size you will pass to setPageSize() a method will load x3 items of that size at first load. Total initial is 9 https://medium.com/firebase-developers/firestore-pagination-in-android-using-firebaseui-library-1d7fe1a75704
                .build();

        // Init Adapter Configuration
        FirestorePagingOptions options = new FirestorePagingOptions.Builder<Event>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Event.class)
                .build();

        mAdapter = new EventListAdapter(options, requireActivity(), swipeLayout);

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.requireActivity(), gridColumnCount));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter.stopListening();
    }
}
