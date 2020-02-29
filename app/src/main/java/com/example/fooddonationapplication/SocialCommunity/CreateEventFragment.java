package com.example.fooddonationapplication.SocialCommunity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fooddonationapplication.Donator.EventUserInterface.DonateActivity;
import com.example.fooddonationapplication.MainMenuActivity;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.RegisterActivity;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEventFragment extends Fragment {

    private static final String TAG = "CreateEventFragment";

    private View rootView;

    EditText eventName, eventDescription, eventEndDate, targetQuantity;
    TextInputLayout eventNameLayout, eventDescriptionLayout, eventEndDateLayout, targetQuantityLayout;
    ImageView eventPhoto;
    Button createEventConfirmation;
    ProgressBar progressBar;

    // For Date Picker
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    String chosenDate;
    Long chosenDateInMillis;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_event,container,false);

        eventName = rootView.findViewById(R.id.create_event_name);
        eventDescription = rootView.findViewById(R.id.create_event_description_);
        eventEndDate = rootView.findViewById(R.id.create_event_end_date);
        targetQuantity = rootView.findViewById(R.id.create_event_target_quantity);

        eventNameLayout = rootView.findViewById(R.id.create_event_name_layout);
        eventDescriptionLayout = rootView.findViewById(R.id.create_event_description_layout);
        eventEndDateLayout = rootView.findViewById(R.id.create_event_end_date_layout);
        targetQuantityLayout = rootView.findViewById(R.id.create_event_target_quantity_layout);

        createEventConfirmation = rootView.findViewById(R.id.create_event_confirm_event);
        eventPhoto = rootView.findViewById(R.id.create_event_image);
        progressBar = rootView.findViewById(R.id.create_event_progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        eventEndDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { // TODO Change to Perform Click
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    calendar = Calendar.getInstance();

                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            chosenDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                            eventEndDate.setText(chosenDate);
                            chosenDate += " 23:59:59";
                            try {
                                chosenDateInMillis = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(chosenDate).getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "Chosen Date" + chosenDate); // TODO Erase Later
                            Log.d(TAG, String.valueOf(chosenDateInMillis));
                            Log.d(TAG, String.valueOf(System.currentTimeMillis()));
                        }
                    }, year, month, day);
                    long now = System.currentTimeMillis() - 1000;
                    datePickerDialog.getDatePicker().setMinDate(now);
                    calendar.add(Calendar.YEAR, 0); // TODO CHECK LATER WHY NEEDED
                    datePickerDialog.show();
                    return true;
                } return false;
            }
        });

        createEventConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventNameData = eventName.getText().toString();
                String eventDescriptionData = eventDescription.getText().toString();
                String endDateData = eventEndDate.getText().toString();
                String targetQuantityData = targetQuantity.getText().toString();
                InputValidation(eventNameData, eventDescriptionData, endDateData, targetQuantityData);
            }
        });

        return rootView;
    }

    private void InputValidation(String eventNameData, String eventDescriptionData, String eventEndDateData, String targetQuantityData) {
        if (eventNameData.isEmpty()) {
            eventNameLayout.setError("Please fill in the event name");
        } else {
            eventNameLayout.setErrorEnabled(false);
        }

        if (eventDescriptionData.isEmpty()) {
            eventDescriptionLayout.setError("Please fill in the event description");
        } else {
            eventDescriptionLayout.setErrorEnabled(false);
        }

        if (eventEndDateData.isEmpty()) {
            eventEndDateLayout.setError("Please fill in the event end date");
        } else {
            eventEndDateLayout.setErrorEnabled(false);
        }

        if (targetQuantityData.isEmpty()) {
            targetQuantityLayout.setError("Please fill in the target quantity");
        } else {
            targetQuantityLayout.setErrorEnabled(false);
        }

        if (eventNameData.isEmpty() && eventDescriptionData.isEmpty() && eventEndDateData.isEmpty() && targetQuantityData.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all the information", Toast.LENGTH_SHORT).show();
        } else if (!eventNameData.isEmpty() && !eventDescriptionData.isEmpty() && !eventEndDateData.isEmpty() && !targetQuantityData.isEmpty()) {
            CreateEvent(eventNameData, eventDescriptionData, eventEndDateData, Double.parseDouble(targetQuantityData));
        } else {
            Toast.makeText(getContext(), "Error occurred, please try again", Toast.LENGTH_SHORT).show();
        } // TODO don't forget image
    }

    private void CreateEvent(String eventNameData, String eventDescriptionData, String eventEndDateData, double targetQuantityData) {
        progressBar.setVisibility(View.VISIBLE);
        createEventConfirmation.setVisibility(View.INVISIBLE);

        String socialCommunityID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newEventRef = db.collection("Event").document();
        String smtg = db.collection("smtg").document().getId();

        Event event = new Event(); // TODO ADD THE CONSTRUCTOR
        // TODO WATCH THIS TO COMPLETE https://www.youtube.com/watch?v=xnFnwbiDFuE
//        user.put("docId", newEventRef);
//
//        Log.d("Main", docRef);
//        Log.d("Main", "Get ID: " + smtg);
//
//        user.put("anotherDocId", smtg);
////                newCityRef.set(user);
//        db.collection("cities").document(smtg)
//                .set(user)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("Main", "DocumentSnapshot successfully written!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("Main", "Error writing document", e);
//                    }
//                });

//        Reference when user register
//        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        User user = new User(fullName, telephoneNumber, uuid, "donator", 0);
//        db.collection("Users").document(uuid)
//                .set(user)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("RegisterActivity", "DocumentSnapshot successfully written!\nThe Unique ID of user is : " + FirebaseAuth.getInstance().getCurrentUser().getUid());
//                        updateUserName(fullName);
//                        Intent intent = new Intent(RegisterActivity.this, MainMenuActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        Toast.makeText(RegisterActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("Main", "Error writing document", e);
//                    }
//                });

    }
}
