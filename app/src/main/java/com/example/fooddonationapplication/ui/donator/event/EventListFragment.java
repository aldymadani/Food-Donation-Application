package com.example.fooddonationapplication.ui.donator.event;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fooddonationapplication.adapter.EventListAdapter;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class EventListFragment extends Fragment {

    private static final String TAG = "EventFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection("events");

    private EventListAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView searchButton, emptyEventImage;
    private TextView emptyEventTextView;
    private TextInputLayout searchKeywordLayout;
    private EditText searchKeyword;
    private SwipeRefreshLayout swipeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container,false);
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

        Query query = eventRef.whereGreaterThanOrEqualTo("endDateInMillis", System.currentTimeMillis()).orderBy("endDateInMillis");
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query newQuery = null;
                String search = searchKeyword.getText().toString().toLowerCase();
                if (!searchKeyword.getText().toString().isEmpty()) {
                    newQuery = eventRef.whereGreaterThanOrEqualTo("titleForSearch", search).whereLessThanOrEqualTo("titleForSearch",search + "z");
                } else {
                    return;
                }
                setUpRecyclerView(newQuery);
                adapter.startListening();;
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Query newQuery = eventRef.whereGreaterThanOrEqualTo("endDateInMillis", System.currentTimeMillis()).orderBy("endDateInMillis");
                searchKeywordLayout.setErrorEnabled(false);
                searchKeyword.setText("");
                hideKeyboard(requireActivity());
                searchKeyword.clearFocus();
                setUpRecyclerView(newQuery);
                adapter.startListening();
                swipeLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void setUpRecyclerView(Query query) {
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();
        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        adapter = new EventListAdapter(options, getContext());
        adapter.notifyDataSetChanged();
        Log.d(TAG, String.valueOf(options));
        Log.d(TAG, String.valueOf(adapter));
        Log.d(TAG, String.valueOf(recyclerView));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.requireActivity(), gridColumnCount));
        recyclerView.setAdapter(adapter);
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
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.stopListening();
    }
}
