package com.example.fooddonationapplication.ui.social_community.event.detail;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donation;
import com.example.fooddonationapplication.services.MySingleton;
import com.example.fooddonationapplication.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DonationDetailActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private static final String TAG = "DonatorDetailActivity";
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView donatorNameTextView;
    private TextInputLayout deliveryDateLayout, deliveryTimeLayout, foodItemsLayout, donationQuantityLayout;
    private EditText donationDate, deliveryDate, deliveryTime, pickUpAddress, foodItems, donationQuantity, donationStatus;
    private ProgressBar imageLoadingProgressBar, deleteProgressBar, updateDonationStatusProgressBar;
    private ImageView foodImagePhoto;
    private Button callDonator, deleteDonation, updateDonationStatus;
    private String donationId, donatorId, eventId, imageURL, chosenDate;
    private double donationQuantityData;
    private Donation donation;

    // DatePicker and TimePicker
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    // Send Notification
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA_kQBhCI:APA91bFc0c2ZXDUJdQRzTIESv_qs2SLJocXUPqPII0WSjaeTEZgshflKKBOk74lJAWkoFBCcz7THIBlXEmDoCaHzfaMOwv4ZEh-5ZiQP1GBAREorM8mypdYwvvbxx87aY3ZuVLzib_tJ";
    final private String contentType = "application/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);
        initializeComponents();

        // To hide the keyboard when user click in the date and time text field
        deliveryDate.setInputType(InputType.TYPE_NULL);
        deliveryTime.setInputType(InputType.TYPE_NULL);

        Intent intent = getIntent();
        donation = intent.getParcelableExtra("Donator");
        donatorId = donation.getDonatorId();
        donationId = donation.getDonationId();
        eventId = donation.getEventId();
        imageURL = donation.getImageURL();
        donationQuantityData = donation.getTotalDonation();
        chosenDate = donation.getPickUpDate();

        donatorNameTextView.setText(donation.getDonatorName());

        Glide.with(this).load(imageURL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageLoadingProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageLoadingProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).error(R.drawable.ic_error_black_24dp).into(foodImagePhoto);

        donationDate.setText(Util.convertToFullDate(donation.getDonationDate()));
        deliveryDate.setText(Util.convertToFullDate(chosenDate));
        deliveryTime.setText(donation.getPickUpTime());
        pickUpAddress.setText(donation.getPickUpAddress());
        foodItems.setText(donation.getFoodItems());
        donationQuantity.setText(String.valueOf(donation.getTotalDonation()));
        donationStatus.setText(donation.getStatus());

        disableTextField("On-Progress");

        if (donation.getStatus().equalsIgnoreCase("Completed")) {
            updateDonationStatus.setVisibility(View.GONE);
            deleteDonation.setVisibility(View.GONE);
            disableTextField("Completed");
        }


        deleteDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Dialog
                setUpDeleteDialog();
            }
        });

        callDonator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openCallIntent(DonationDetailActivity.this, donation.getDonatorPhone());
            }
        });

        updateDonationStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasEmptyField()) {
                    deleteDonation.setEnabled(false);
                    updateDonationStatus.setVisibility(View.INVISIBLE);
                    updateDonationStatusProgressBar.setVisibility(View.VISIBLE);
                    completeDonationStatus();
                }
            }
        });
    }

    private boolean hasEmptyField() {
        boolean hasEmpty = true;

        // Checking food items field
        boolean foodItemsValidation = false;
        if (foodItems.getText().toString().isEmpty()) {
            foodItemsLayout.setError("Please input the donated food");
        } else {
            foodItemsLayout.setErrorEnabled(false);
            foodItemsValidation = true;
        }

        // Checking donation quantity field
        boolean donationQuantityValidation = false;
        if (donationQuantity.getText().toString().isEmpty()) {
            donationQuantityLayout.setError("Please input the donation quantity");
        } else if (Double.parseDouble(donationQuantity.getText().toString()) < 1) {
            donationQuantityLayout.setError("Minimum donation is 1 Kg");
        } else {
            donationQuantityLayout.setErrorEnabled(false);
            donationQuantityValidation = true;
        }

        if (foodItemsValidation && donationQuantityValidation) {
            hasEmpty = false;
        }

        return hasEmpty;
    }

    private void completeDonationStatus() {
        double totalDonation = Double.parseDouble(donationQuantity.getText().toString());
        WriteBatch batch = db.batch();

        // Update donations collection
        DocumentReference donationDocumentReference = db.collection("donations").document(donationId);
        batch.update(donationDocumentReference, "totalDonation", totalDonation);
        batch.update(donationDocumentReference, "status", "Completed");
        batch.update(donationDocumentReference, "foodItems", foodItems.getText().toString());
        batch.update(donationDocumentReference, "pickUpDate", chosenDate);
        batch.update(donationDocumentReference, "pickUpTime", deliveryTime.getText().toString());

        // Update users collection
        DocumentReference userDocumentReference = db.collection("users").document(donatorId);
        batch.update(userDocumentReference, "totalDonation", FieldValue.increment(totalDonation));

        // Update events collection
        DocumentReference eventDocumentReference = db.collection("events").document(eventId);
        batch.update(eventDocumentReference, "totalDonation", FieldValue.increment(totalDonation));

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setUpNotificationData("Received");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DonationDetailActivity.this, "Donation is failed to update", Toast.LENGTH_SHORT).show();
                deleteDonation.setEnabled(true);
                updateDonationStatus.setVisibility(View.VISIBLE);
                updateDonationStatusProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setUpDeleteDialog() {
        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setAnimation(R.raw.delete_animation)
                .setTitle("Delete Donation")
                .setMessage("Are you sure want to delete this donation?")
                .setCancelable(false)
                .setPositiveButton("Delete", R.drawable.ic_delete_forever_black_24dp, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // Hide the dialog again
                        dialogInterface.dismiss();

                        deleteDonation.setVisibility(View.INVISIBLE);
                        deleteProgressBar.setVisibility(View.VISIBLE);
                        updateDonationStatus.setEnabled(false);

                        // Delete Operation
                        db.collection("donations").document(donationId).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        deleteImageFromFirebaseStorage(imageURL);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Did not delete the event: " + e);
                                Toast.makeText(DonationDetailActivity.this, "Failed to delete the donation", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", R.drawable.ic_cancel_black_24dp, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        mDialog.show();
    }

    private void deleteImageFromFirebaseStorage(String imageURI) {
        // Delete from Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageURI);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "onSuccess: deleted file");
                setUpNotificationData("Rejected");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "onFailure: did not delete file: " + exception);
                Toast.makeText(DonationDetailActivity.this, "Failed to delete the donation", Toast.LENGTH_SHORT).show();
                deleteDonation.setVisibility(View.VISIBLE);
                deleteProgressBar.setVisibility(View.INVISIBLE);
                updateDonationStatus.setEnabled(true);
            }
        });
    }

    private void setUpNotificationData(String status) {
        String TOPIC = "/topics/" + donation.getDonatorId(); //topic must match with what the receiver subscribed to
        String NOTIFICATION_TITLE = "Your donation on " + donation.getEventName() + " has been " + status;
        String NOTIFICATION_MESSAGE = "Your " + foodItems.getText().toString() +  " donation has been " + status +". Your donation date is on " + donationDate.getText().toString() + ".";

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(notification, status);
    }

    private void sendNotification(JSONObject notification, String status) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        if (status.equalsIgnoreCase("Approved")) {
            Toast.makeText(DonationDetailActivity.this, "Status is updated to Completed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DonationDetailActivity.this, "Donation is successfully deleted", Toast.LENGTH_SHORT).show();
        }
        deleteDonation.setVisibility(View.VISIBLE);
        deleteProgressBar.setVisibility(View.INVISIBLE);
        updateDonationStatus.setEnabled(true);
        finish();
    }

    private void setupCalendar() {
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(DonationDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                chosenDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                deliveryDate.setText(Util.convertToFullDate(chosenDate));
                deliveryDate.clearFocus();
            }
        }, year, month, day);
        long now = System.currentTimeMillis() - 1000;
        datePickerDialog.getDatePicker().setMinDate(now);
        calendar.add(Calendar.YEAR, 0);
        datePickerDialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(android.content.DialogInterface dialog) {
                deliveryDate.clearFocus();
            }
        });
        datePickerDialog.show();
    }

    private void setupClock() {
        timePickerDialog = new TimePickerDialog(DonationDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                String amPm;
                if (hourOfDay >= 12) {
                    amPm = " PM";
                } else {
                    amPm = " AM";
                }
                deliveryTime.clearFocus();
                deliveryTime.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
            }
        }, 0, 0, false);
        timePickerDialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(android.content.DialogInterface dialog) {
                deliveryTime.clearFocus();
            }
        });
        timePickerDialog.show();
    }

    private void disableTextField(String status) {
        donationDate.setFocusable(false);
        donationDate.setFocusableInTouchMode(false);
        donationDate.setCursorVisible(false);

        pickUpAddress.setFocusable(false);
        pickUpAddress.setFocusableInTouchMode(false);
        pickUpAddress.setCursorVisible(false);

        donationStatus.setFocusable(false);
        donationStatus.setFocusableInTouchMode(false);
        donationStatus.setCursorVisible(false);

        if (status.equalsIgnoreCase("Completed")) {
            deliveryDate.setFocusable(false);
            deliveryDate.setFocusableInTouchMode(false);
            deliveryDate.setCursorVisible(false);

            deliveryTime.setFocusable(false);
            deliveryTime.setFocusableInTouchMode(false);
            deliveryTime.setCursorVisible(false);

            foodItems.setFocusable(false);
            foodItems.setFocusableInTouchMode(false);
            foodItems.setCursorVisible(false);

            donationQuantity.setFocusable(false);
            donationQuantity.setFocusableInTouchMode(false);
            donationQuantity.setCursorVisible(false);
        }
    }

    private void initializeComponents() {
        donatorNameTextView = findViewById(R.id.donationDetailDonatorByText);
        callDonator = findViewById(R.id.donationDetailCallDonatorButton);

        foodImagePhoto = findViewById(R.id.donationDetailFoodPhoto);
        imageLoadingProgressBar = findViewById(R.id.donationDetailFoodPhotoProgressBar);

        donationDate = findViewById(R.id.donationDetailDonationDate);
        deliveryDate = findViewById(R.id.donationDetailDeliveryDate);
        deliveryTime = findViewById(R.id.donationDetailDeliveryTime);
        pickUpAddress = findViewById(R.id.donationDetailPickUpAddress);
        foodItems = findViewById(R.id.donationDetailFoodItems);
        donationQuantity = findViewById(R.id.donationDetailTotalDonation);
        donationStatus = findViewById(R.id.donationDetailDonationStatus);

        deliveryDateLayout = findViewById(R.id.donationDetailDeliveryDateLayout);
        deliveryTimeLayout = findViewById(R.id.donationDetailDeliveryTimeLayout);
        foodItemsLayout = findViewById(R.id.donationDetailFoodItemsLayout);
        donationQuantityLayout = findViewById(R.id.donationDetailTotalDonationLayout);

        deleteDonation = findViewById(R.id.donationDetailDeleteDonationButton);
        deleteProgressBar = findViewById(R.id.donatorDetailButtonProgressBar);

        updateDonationStatus = findViewById(R.id.donationDetailUpdateStatusButton);
        updateDonationStatusProgressBar = findViewById(R.id.donatorDetailUpdateStatusButtonProgressBar);

        deliveryDate.setOnFocusChangeListener(this);
        deliveryTime.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.donationDetailDeliveryDate:
                    Util.hideKeyboard(DonationDetailActivity.this);
                    setupCalendar();
                    break;
                case R.id.donationDetailDeliveryTime:
                    Util.hideKeyboard(DonationDetailActivity.this);
                    setupClock();
                    break;
                case R.id.donationDetailFoodItems:
                    foodItemsLayout.setErrorEnabled(false);
                    break;
                case R.id.donationDetailTotalDonation:
                    donationQuantityLayout.setErrorEnabled(false);
                    break;
            }
        } else {
            switch (v.getId()) {
                case R.id.donationDetailDeliveryDate:
                    if (datePickerDialog != null) {
                        datePickerDialog.hide();
                    }
                    break;
                case R.id.donationDetailDeliveryTime:
                    if (timePickerDialog != null) {
                        timePickerDialog.hide();
                    }
                    break;
            }
        }
    }
}
