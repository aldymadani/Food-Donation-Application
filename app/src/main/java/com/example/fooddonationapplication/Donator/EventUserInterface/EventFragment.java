package com.example.fooddonationapplication.Donator.EventUserInterface;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class EventFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection("events");

    private EventAdapter adapter;
    RecyclerView recyclerView;
    ImageView searchButton;
    TextInputLayout searchInputLayout;
    EditText searchKeyword;
    FloatingActionButton refreshButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container,false);
        recyclerView = rootView.findViewById(R.id.event_recyclerView);
        Query query = eventRef.whereGreaterThanOrEqualTo("endDateInMillis", System.currentTimeMillis());
        setUpRecyclerView(query);
        searchKeyword = rootView.findViewById(R.id.search);
        searchInputLayout = rootView.findViewById(R.id.search_layout);
        searchButton = rootView.findViewById(R.id.search_button);
        refreshButton = rootView.findViewById(R.id.refreshFloatingButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), searchKeyword.getText().toString(), Toast.LENGTH_SHORT).show();
                Query newQuery = null;
                String search = searchKeyword.getText().toString();
                if (!search.isEmpty()) {
                    newQuery = eventRef.whereGreaterThanOrEqualTo("title", search).whereLessThanOrEqualTo("title",search + "z");
                } else if (search.isEmpty()) {
                    searchInputLayout.setError("Please fill in");
                    return;
                }
                searchInputLayout.setErrorEnabled(false);
                setUpRecyclerView(newQuery);
                adapter.startListening();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query newQuery = eventRef.whereGreaterThanOrEqualTo("endDateInMillis", System.currentTimeMillis());
                searchInputLayout.setErrorEnabled(false);
                searchKeyword.setText("");
                setUpRecyclerView(newQuery);
                adapter.startListening();
            }
        });
        return rootView;
    }

    private void setUpRecyclerView(Query query) {
//        Query query = eventRef.whereGreaterThanOrEqualTo("endDateInMillis", System.currentTimeMillis());
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();
        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        adapter = new EventAdapter(options, getContext());
        adapter.notifyDataSetChanged();
        Log.d("EventIniDong", String.valueOf(options));
        Log.d("EventIniDong", String.valueOf(adapter));
        Log.d("EventIniDong", String.valueOf(recyclerView));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), gridColumnCount));
        recyclerView.setAdapter(adapter);
    }

//    private void firestoreSearch (String title) {
//        Query query = eventRef.whereEqualTo("title", "Title 2");
//        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
//                .setQuery(query, Event.class)
//                .build();
//
//        adapter.notifyDataSetChanged();
//    }

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
