package com.example.fooddonationapplication.Donator.EventUserInterface;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddonationapplication.Donator.MainDonatorActivity;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.SocialCommunity.DonatorDetail;
import com.example.fooddonationapplication.SocialCommunity.MainSocialCommunityActivity;
import com.example.fooddonationapplication.model.Donator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DonateActivity extends AppCompatActivity {

    private static final String TAG = "DonateActivity";
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnConfirm;
    private TextInputLayout textInputAddress, textInputFoodItems, textInputDate, textInputTime, textInputQuantity;
    private ProgressBar progressBar;
    private ImageView foodImage;
    private EditText etAddress, etFoodItems, etDate, etTime, etQuantity;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private String PROFILE_IMAGE_URL = null;
    private int TAKE_IMAGE_CODE = 10001;
    private Bitmap bitmap;
    private String pickUpAddressData;
    private String foodItemsData;
    private String pickUpData;
    private String timeData;
    private String totalDonationData;
    private String chosenDate;
    private Long chosenDateInMillis;
    private boolean hasImage;
    private String foodImageURI;
    private String userID;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        hasImage = false;
        textInputAddress = findViewById(R.id.donate_address_layout);
        textInputFoodItems = findViewById(R.id.donate_food_item_layout);
        textInputDate = findViewById(R.id.donate_date_layout);
        textInputQuantity = findViewById(R.id.donate_quantity_layout);
        textInputTime = findViewById(R.id.donate_time_layout);
        btnConfirm = findViewById(R.id.donate_confirm);
        progressBar = findViewById(R.id.donate_progressBar);
        foodImage = findViewById(R.id.donate_food_photo);

        etAddress = findViewById(R.id.donate_address);
        etFoodItems = findViewById(R.id.donate_food_item);
        etDate = findViewById(R.id.donate_date);
        etTime = findViewById(R.id.donate_time);
        etQuantity = findViewById(R.id.donate_quantity);

        progressBar.setVisibility(View.INVISIBLE);

//        if(savedInstanceState != null) {
//            Bitmap bitmapNew = savedInstanceState.getParcelable("image");
//            foodImage.setImageBitmap(bitmapNew);
//        }

        etDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    calendar = Calendar.getInstance();

                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    datePickerDialog = new DatePickerDialog(DonateActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            chosenDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                            etDate.setText(chosenDate);
                            chosenDate += " 23:59:59";
                            try {
                                chosenDateInMillis = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(chosenDate).getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "Chosen Date" + chosenDate);
                            Log.d(TAG, String.valueOf(chosenDateInMillis));
                            Log.d(TAG, String.valueOf(System.currentTimeMillis()));
                        }
                    }, year, month, day);
                    long now = System.currentTimeMillis() - 1000;
                    datePickerDialog.getDatePicker().setMinDate(now);
                    calendar.add(Calendar.YEAR, 0);
                    datePickerDialog.getDatePicker().setMaxDate(now + (1000 * 60 * 60 * 24 * 7));
                    datePickerDialog.show();
                    return true;
                }
                return false;
            }
        });

        etTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(DonateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                            String amPm;
                            if (hourOfDay >= 12) {
                                amPm = " PM";
                            } else {
                                amPm = " AM";
                            }
                            etTime.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
                        }
                    }, 0, 0, false);
                    timePickerDialog.show();
                    return true;
                }
                return false;
            }
        });

        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUpAddressData = etAddress.getText().toString();
                foodItemsData = etFoodItems.getText().toString();
                pickUpData = etDate.getText().toString();
                timeData = etTime.getText().toString();
                totalDonationData = etQuantity.getText().toString();
                checkingAllFields();
            }
        });
    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        Bitmap bitmapSaved = ((BitmapDrawable)foodImage.getDrawable()).getBitmap();
//        outState.putParcelable("image", bitmapSaved);
//        super.onSaveInstanceState(outState);
//    }

    private void checkingAllFields() {
        if (pickUpAddressData.isEmpty()) {
            pickUpAddressData = "Shipping by the donator";
        }

        if (foodItemsData.isEmpty()) {
            textInputFoodItems.setError("Please fill in the food items field");
        } else {
            textInputFoodItems.setErrorEnabled(false);
        }

        if (pickUpData.isEmpty()) {
            textInputDate.setError("Please chose the pick up / delivery date");
        } else {
            textInputDate.setErrorEnabled(false);
        }

        if (timeData.isEmpty()) {
            textInputTime.setError("Please chose the time");
        } else {
            textInputTime.setErrorEnabled(false);
        }

        boolean validQuantity = false;
        if (totalDonationData.isEmpty()) {
            textInputQuantity.setError("Please fill in the quantity");
        } else if (Double.parseDouble(totalDonationData) < 1) {
            textInputQuantity.setError("Minimum donation is 1 Kg");
        } else {
            validQuantity = true;
            textInputQuantity.setErrorEnabled(false);
        }

        if (!hasImage) {
            Toast.makeText(getApplicationContext(), "Please insert the food image", Toast.LENGTH_SHORT).show();
        }

        if (foodItemsData.isEmpty() && pickUpData.isEmpty() && timeData.isEmpty() && totalDonationData.isEmpty() && !hasImage && !validQuantity) {
            Toast.makeText(getApplicationContext(), "Please complete in all the information", Toast.LENGTH_SHORT).show();
        } else if (!foodItemsData.isEmpty() && !pickUpData.isEmpty() && !timeData.isEmpty() && !totalDonationData.isEmpty() && hasImage && validQuantity) {
            progressBar.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.INVISIBLE);
            handleUpload(bitmap);
        } else {
            Toast.makeText(getApplicationContext(), "Error occurred, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    foodImage.setImageBitmap(bitmap);
                    hasImage = true;
            }
        }
    }


    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String currentDateDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.v(TAG, currentDateDetail);
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("donated-food").child(uuid + currentDateDetail + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getCause());
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "OnSuccess: " + uri);
                        foodImageURI = uri.toString();
                        Log.d(TAG, foodImageURI);
                        CreateDonation();
                    }
                });
    }

    private void CreateDonation() {
        eventID = getIntent().getStringExtra("eventID");
        String eventTitle = getIntent().getStringExtra("eventName");
        String socialCommunityId = getIntent().getStringExtra("socialCommunityId");
        String socialCommunityName = getIntent().getStringExtra("socialCommunityName");

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String donatorName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        final String donatorDocumentID = db.collection("donators").document().getId();

        final Donator donator = new Donator();
        donator.setEventId(eventID);
        donator.setEventName(eventTitle);
        donator.setUuid(userID);
        donator.setName(donatorName);
        donator.setPickUpAddress(pickUpAddressData);
        donator.setFoodItems(foodItemsData);
        donator.setPickUpDate(pickUpData);
        donator.setPickUpTime(timeData);
        donator.setDonationDate(currentDate);
        donator.setImageURI(foodImageURI);
        donator.setTotalDonation(Double.parseDouble(totalDonationData));
        donator.setDonatorId(donatorDocumentID);
        donator.setSocialCommunityId(socialCommunityId);
        donator.setSocialCommunityName(socialCommunityName);

        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            donator.setPhone(documentSnapshot.getString("phone"));
                            db.collection("donators").document(donatorDocumentID)
                                    .set(donator)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Donation is successfully created", Toast.LENGTH_SHORT).show();
                                            UpdateUserAndEventDonation();
                                            Log.d(TAG, "Donation is successfully created");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Donation creation is failed, try again", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Error writing document", e);
                                            Log.d(TAG, "Donation is Failure");
                                        }
                                    });
                        }
                    }
                });
    }

    private void UpdateUserAndEventDonation() {
        DocumentReference userDocumentReference = db.collection("users").document(userID);
        DocumentReference eventDocumentReference = db.collection("events").document(eventID);

        WriteBatch batch = db.batch();

        batch.update(userDocumentReference, "totalDonation", FieldValue.increment(Double.parseDouble(totalDonationData)));
        batch.update(eventDocumentReference, "totalDonation", FieldValue.increment(Double.parseDouble(totalDonationData)));

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(DonateActivity.this, "Donation is successfully created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainDonatorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Log.d(TAG, "Event successfully written!");
                progressBar.setVisibility(View.INVISIBLE);
                btnConfirm.setVisibility(View.VISIBLE);
            }
        });

//        userDocumentReference.update("totalDonation", FieldValue.increment(Double.parseDouble(totalDonationData)))
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, String.valueOf((totalDonationData)));
//                        Log.d(TAG, "User total donation is successfully updated");
//                        UpdateEventDonation();
//                    }
//                });
    }

//    private void UpdateEventDonation() {
//        DocumentReference eventDocumentReference = db.collection("events").document(eventID);
//        eventDocumentReference.update("totalDonation", FieldValue.increment(Double.parseDouble(totalDonationData)))
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, String.valueOf(Double.parseDouble(totalDonationData)));
//                        Log.d(TAG, "Event total donation is successfully updated");
//                        Intent intent = new Intent(getApplicationContext(), MainDonatorActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        Log.d(TAG, "Event successfully written!");
//                        progressBar.setVisibility(View.INVISIBLE);
//                        btnConfirm.setVisibility(View.VISIBLE);
//                    }
//                });
//    }
}
