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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddonationapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DonateActivity extends AppCompatActivity {

    private static final String TAG = "DonateActivity";

    Button btnConfirm;
    TextInputLayout textInputAddress, textInputFoodItems, textInputDate, textInputTime, textInputQuantity;
    ProgressBar progressBar;
    ImageView foodImage;
    FirebaseAuth mFirebaseAuth;
    EditText etAddress, etFoodItems, etDate, etTime, etQuantity;

    Calendar calendar;
    DatePickerDialog datePickerDialog;

    String PROFILE_IMAGE_URL = null;
    int TAKE_IMAGE_CODE = 10001;
    Bitmap bitmap;

    String chosenDate;
    Long chosenDateInMillis;

    String docRef = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

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

        etDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { // TODO Change to Perform Click
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
                            Log.d(TAG, "Chosen Date" + chosenDate); // TODO Erase Later
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
                } return false;
            }
        });

        etTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { // TODO Change to Perform Click
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
                } return false;
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
                progressBar.setVisibility(View.VISIBLE);
                checkingAllFields();
                // TODO Checking all the fields
                // TODO Upload the data into database
                // TODO Show donation is completed and redirect to main menu
            }
        });
    }

    private void checkingAllFields() { // TODO change to boolean checking
        // Address is optional
        if(etFoodItems.getText().toString().isEmpty()) {
            textInputFoodItems.setError("Please fill in the food items field");
        } else {
            textInputFoodItems.setErrorEnabled(false);
        }

        if(etDate.getText().toString().isEmpty()) {
            textInputDate.setError("Please chose the pick up / delivery date");
        } else {
            textInputDate.setErrorEnabled(false);
        }

        if(etTime.getText().toString().isEmpty()) {
            textInputTime.setError("Please chose the time");
        } else {
            textInputTime.setErrorEnabled(false);
        }

        if(etQuantity.getText().toString().isEmpty()) {
            textInputQuantity.setError("Please fill in the quantity");
        } else {
            textInputQuantity.setErrorEnabled(false);
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
//                    handleUpload(bitmap);
            }
        }
    }


    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.v(TAG, date);
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("donated-food").child(uuid + date + ".jpeg");

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
                        addToDatabase(uri);
                    }
                });
    }

    private void addToDatabase(Uri reference) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);
        user.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        DocumentReference donatorsReference = db.collection("donators").document();
        String donatorsReferenceID = db.collection("donators").document().getId();

        db.collection("donators").document(donatorsReferenceID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Main", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Main", "Error writing document", e);
                    }
                });

        // Add a new document with a generated ID
        db.collection("events")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("DonateActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
                        docRef = documentReference.getId();
                        Log.d("Main Activity", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DonateActivity", "Error adding document", e);
                    }
                });

        DocumentReference newCityRef = db.collection("smtg").document();
        String smtg = db.collection("smtg").document().getId();
        user.put("docId", newCityRef);

        Log.d("Main", docRef);
        Log.d("Main", "Get ID: " + smtg);

        user.put("anotherDocId", smtg);
//                newCityRef.set(user);
        db.collection("cities").document(smtg)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Main", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Main", "Error writing document", e);
                    }
                });
    }
}
