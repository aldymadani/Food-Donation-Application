package com.example.fooddonationapplication.donator.EventUserInterface;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddonationapplication.adapter.EventAdapter;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class EventFragment extends Fragment {

    private static final String TAG = "EventFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection("events");

    private EventAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView searchButton;
    private TextInputLayout searchInputLayout;
    private EditText searchKeyword;
    private FloatingActionButton refreshButton;
    private MaterialSpinner sortBy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container,false);
        recyclerView = rootView.findViewById(R.id.event_recyclerView);
        refreshButton = rootView.findViewById(R.id.refreshFloatingButton);

        Query query = eventRef.whereGreaterThanOrEqualTo("endDateInMillis", System.currentTimeMillis()).orderBy("endDateInMillis");
        setUpRecyclerView(query);

        sortBy = rootView.findViewById(R.id.spinner);
        sortBy.setItems("Almost end events", "Newest Events", "Past Events", "Show all events");
        sortBy.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Toast.makeText(getContext(), "Clicked " + item, Toast.LENGTH_SHORT).show();
                Query newQuery = null;
                if(item.equals("Show all events")) {
                    newQuery = eventRef;
                } else if (item.equals("Newest Events")) {
                    newQuery = eventRef.orderBy("timestamp", Query.Direction.DESCENDING);
                } else if (item.equals("Past Events")) {
                    newQuery = eventRef.whereLessThanOrEqualTo("endDateInMillis", System.currentTimeMillis());
                } else if (item.equals("Almost end events")) {
                    newQuery = eventRef.whereGreaterThan("endDateInMillis", System.currentTimeMillis()).orderBy("endDateInMillis");
                }
                setUpRecyclerView(newQuery);
                adapter.startListening();
            }
        });

        return rootView;
    }

    private void setUpRecyclerView(Query query) {
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();
        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        adapter = new EventAdapter(options, getContext());
        adapter.notifyDataSetChanged();
        Log.d(TAG, String.valueOf(options));
        Log.d(TAG, String.valueOf(adapter));
        Log.d(TAG, String.valueOf(recyclerView));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), gridColumnCount));
        recyclerView.setAdapter(adapter);
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
