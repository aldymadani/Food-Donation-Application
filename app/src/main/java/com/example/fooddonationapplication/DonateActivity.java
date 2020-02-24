package com.example.fooddonationapplication;

import android.app.DatePickerDialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DonateActivity extends AppCompatActivity {

    private static final String TAG = "DonateActivity";

    Button btnConfirm;
    TextInputLayout textInputAddress, textInputFoodItems, textInputDate, textInputQuantity;
    ProgressBar progressBar;
    ImageView foodImage;
    FirebaseAuth mFirebaseAuth;
    EditText etDate;

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
        textInputQuantity = findViewById(R.id.donate_date_layout);
        btnConfirm = findViewById(R.id.donate_confirm);
        progressBar = findViewById(R.id.donate_progressBar);
        foodImage = findViewById(R.id.donate_food_photo);
        etDate = findViewById(R.id.donate_date);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String getCurrentDateTime = sdf.format(c.getTime());
        String getMyTime = "23/2/2020 23:59:59";
        Log.d(TAG, getCurrentDateTime);

        if (getCurrentDateTime.compareTo(getMyTime) < 0) {

            Log.d(TAG, "getCurrentDateTime older than getMyTime ");
        } else {
            Log.d(TAG, "getMyTime older than getCurrentDateTime ");
        }

        try {
            chosenDateInMillis = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(getMyTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, String.valueOf(chosenDateInMillis));
        Log.d(TAG, String.valueOf(System.currentTimeMillis()));

        if (System.currentTimeMillis() > chosenDateInMillis) {
            Log.d(TAG, "Expired");
        } else {
            Log.d(TAG, "Active");
        }

        int daysOld = (int) (chosenDateInMillis - System.currentTimeMillis());

        if (daysOld < 0) {
            Log.d(TAG, "Expired2");
        } else {
            Log.d(TAG, "Active2");
        }

        etDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { // TODO Change to Perform Click
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

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
                    datePickerDialog.getDatePicker().setMinDate(now);// TODO: used to hide previous date,month and year
                    calendar.add(Calendar.YEAR, 0);
                    datePickerDialog.getDatePicker().setMaxDate(now + (1000 * 60 * 60 * 24 * 7));// TODO: used to hide future date,month and year
                    datePickerDialog.show();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    foodImage.setImageBitmap(bitmap);
                    handleUpload(bitmap);
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

//        reference.putBytes(baos.toByteArray())
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        getDownloadUrl(reference);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "onFailure: " + e.getCause());
//                    }
//                });
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
