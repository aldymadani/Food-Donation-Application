package com.example.fooddonationapplication.ui.donator.event;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.fooddonationapplication.model.Donation;
import com.example.fooddonationapplication.ui.donator.MainDonatorActivity;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.util.constant.RequestCodeConstant;
import com.example.fooddonationapplication.viewmodel.DonatorViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateDonationActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private static final String TAG = "DonateActivity";

    private Button btnConfirm;
    private TextInputLayout textInputFoodItems, textInputDate, textInputTime, textInputQuantity;
    private EditText etAddress, etFoodItems, etDate, etTime, etQuantity;
    private ProgressBar progressBar;
    private ImageView foodImage;
    private Bitmap bitmap;
    private String pickUpAddressData, foodItemsData, timeData, totalDonationData, chosenDate, foodImageURL, userId, eventId;
    private boolean hasImage;

    // Firestore Database Access
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String donationDocumetId = db.collection("donations").document().getId();

    // View Model
    DonatorViewModel mViewModel;

    // DatePicker and TimePicker
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_create);
        initializeComponents();
        hasImage = false;

        progressBar.setVisibility(View.INVISIBLE);

        // To hide the keyboard when user click in the end date text field
        etDate.setInputType(InputType.TYPE_NULL);
        etTime.setInputType(InputType.TYPE_NULL);

        mViewModel = new ViewModelProvider(this).get(DonatorViewModel.class);
        if (mViewModel.getImageBitmap() != null) {
            bitmap = mViewModel.getImageBitmap();
            foodImage.setImageBitmap(bitmap);
            hasImage = true;
        }

        etFoodItems.setOnFocusChangeListener(this);
        etDate.setOnFocusChangeListener(this);
        etTime.setOnFocusChangeListener(this);
        etQuantity.setOnFocusChangeListener(this);

        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(CreateDonationActivity.this);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateDonationActivity.this);
                View takeImageOptionDialog = getLayoutInflater().inflate(R.layout.dialog_option, null);
                mBuilder.setView(takeImageOptionDialog);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                TextView dialogTitle = takeImageOptionDialog.findViewById(R.id.registerDialogTitle);
                Button cameraButton = takeImageOptionDialog.findViewById(R.id.registerDialogDonatorButton);
                Button galleryButton = takeImageOptionDialog.findViewById(R.id.registerDialogSocialCommunityButton);
                dialogTitle.setText("Take Photo Options:");
                cameraButton.setText("Camera");
                galleryButton.setText("Gallery");

                cameraButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CreateDonationActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCodeConstant.TAKE_IMAGE_CODE);
                        } else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(intent, RequestCodeConstant.TAKE_IMAGE_CODE);
                            }
                        }
                    }
                });

                galleryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCodeConstant.GALLERY_PICK);
                        } else {
                            Intent galleryIntent = new Intent();
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                            galleryIntent.setType("image/*");
                            startActivityForResult(galleryIntent, RequestCodeConstant.GALLERY_PICK);
                        }
                    }
                });
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAddress.clearFocus();
                etFoodItems.clearFocus();
                etTime.clearFocus();
                etQuantity.clearFocus();
                Util.hideKeyboard(CreateDonationActivity.this);
                pickUpAddressData = etAddress.getText().toString();
                foodItemsData = etFoodItems.getText().toString();
                timeData = etTime.getText().toString();
                totalDonationData = etQuantity.getText().toString();
                checkingAllFields();
            }
        });
    }

    private void checkingAllFields() {
        if (pickUpAddressData.isEmpty()) {
            pickUpAddressData = "Shipping by the donator";
        }

        if (foodItemsData.isEmpty()) {
            textInputFoodItems.setError("Please fill in the food items field");
        } else {
            textInputFoodItems.setErrorEnabled(false);
        }

        boolean pickupDateValidation = false;
        if (etDate.getText().toString().isEmpty()) {
            textInputDate.setError("Please chose the pick up / delivery date");
        } else {
            pickupDateValidation = true;
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
            textInputQuantity.setError("Minimum donation is 1 kg");
        } else {
            validQuantity = true;
            textInputQuantity.setErrorEnabled(false);
        }

        if (!hasImage) {
            Toast.makeText(getApplicationContext(), "Please insert the food image", Toast.LENGTH_SHORT).show();
        }

        if (!foodItemsData.isEmpty() && pickupDateValidation && !timeData.isEmpty() && !totalDonationData.isEmpty() && hasImage && validQuantity) {
            progressBar.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.INVISIBLE);
            handleUpload(bitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCodeConstant.TAKE_IMAGE_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, RequestCodeConstant.TAKE_IMAGE_CODE);
                }
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RequestCodeConstant.GALLERY_PICK) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RequestCodeConstant.GALLERY_PICK);
            } else {
                Toast.makeText(this, "Media Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeConstant.TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    foodImage.setImageBitmap(bitmap);
                    mViewModel.setImageBitmap(bitmap);
                    hasImage = true;
            }
        }

        if (requestCode == RequestCodeConstant.GALLERY_PICK) {
            switch (resultCode) {
                case RESULT_OK:
                    Uri ImageURI = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(CreateDonationActivity.this.getContentResolver(), ImageURI);
                        foodImage.setImageBitmap(bitmap);
                        mViewModel.setImageBitmap(bitmap);
                        hasImage = true;
                    } catch (IOException e) {
                        Log.e("UpdateEventFragment", e.getLocalizedMessage());
                        e.printStackTrace();
                    }
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("donated-food").child(donationDocumetId + ".jpeg");

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
                        foodImageURL = uri.toString();
                        Log.d(TAG, foodImageURL);
                        CreateDonation();
                    }
                });
    }

    private void CreateDonation() {
        eventId = getIntent().getStringExtra("eventId");
        String eventTitle = getIntent().getStringExtra("eventName");
        String socialCommunityId = getIntent().getStringExtra("socialCommunityId");
        String socialCommunityName = getIntent().getStringExtra("socialCommunityName");
        String socialCommunityPhone = getIntent().getStringExtra("socialCommunityPhoneNumber");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String donatorName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

        final Donation donation = new Donation();
        donation.setDonatorName(donatorName);
        donation.setDonatorId(userId);
        donation.setPickUpAddress(pickUpAddressData);
        donation.setFoodItems(foodItemsData);
        donation.setPickUpDate(chosenDate);
        donation.setPickUpTime(timeData);
        donation.setDonationDate(currentDate);
        donation.setTotalDonation(Double.parseDouble(totalDonationData));
        donation.setDonationId(donationDocumetId);
        donation.setEventName(eventTitle);
        donation.setEventId(eventId);
        donation.setSocialCommunityName(socialCommunityName);
        donation.setSocialCommunityPhoneNumber(socialCommunityPhone);
        donation.setSocialCommunityId(socialCommunityId);
        donation.setStatus("On-Progress");
        donation.setImageURL(foodImageURL);

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            donation.setDonatorPhone(documentSnapshot.getString("phone"));
                            db.collection("donations").document(donationDocumetId)
                                    .set(donation)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CreateDonationActivity.this, "Donation is successfully created", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Donation successfully written!");
                                            progressBar.setVisibility(View.INVISIBLE);
                                            btnConfirm.setVisibility(View.VISIBLE);

                                            // Go To Main Menu Again
                                            Intent intent = new Intent(getApplicationContext(), MainDonatorActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Donation creation is failed, try again", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });
                        }
                    }
                });
    }

    private void setupClock() {
        timePickerDialog = new TimePickerDialog(CreateDonationActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                String amPm;
                if (hourOfDay >= 12) {
                    amPm = " PM";
                } else {
                    amPm = " AM";
                }
                etTime.clearFocus();
                etTime.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
            }
        }, 0, 0, false);
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                etTime.clearFocus();
            }
        });
        timePickerDialog.show();
    }

    private void setupCalendar() {
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(CreateDonationActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                chosenDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                etDate.setText(Util.convertToFullDate(chosenDate));
                etDate.clearFocus();
            }
        }, year, month, day);
        long now = System.currentTimeMillis() - 1000;
        datePickerDialog.getDatePicker().setMinDate(now);
        calendar.add(Calendar.YEAR, 0);
        long eventEndDate = getIntent().getExtras().getLong("endDateInMillis");
        Log.d(TAG, String.valueOf(eventEndDate));
        datePickerDialog.getDatePicker().setMaxDate(eventEndDate);
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                etDate.clearFocus();
            }
        });
        datePickerDialog.show();
    }

    private void initializeComponents() {
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
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.donate_food_item:
                    textInputFoodItems.setErrorEnabled(false);
                    break;
                case R.id.donate_date:
                    textInputDate.setErrorEnabled(false);
                    setupCalendar();
                    break;
                case R.id.donate_time:
                    textInputTime.setErrorEnabled(false);
                    setupClock();
                    break;
                case R.id.donate_quantity:
                    textInputQuantity.setErrorEnabled(false);
                    break;
            }
        } else {
            switch (v.getId()) {
                case R.id.donate_date:
                    if (datePickerDialog != null) {
                        datePickerDialog.hide();
                    }
                    break;
                case R.id.donate_time:
                    if (timePickerDialog != null) {
                        timePickerDialog.hide();
                    }
                    break;
            }
        }
    }
}
