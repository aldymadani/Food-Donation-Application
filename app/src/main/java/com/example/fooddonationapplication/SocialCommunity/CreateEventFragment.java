package com.example.fooddonationapplication.SocialCommunity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEventFragment extends Fragment {

    private static final String TAG = "CreateEventFragment";
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

    // For photo
    int TAKE_IMAGE_CODE = 10001;
    Bitmap bitmap;
    String eventImageURI;
    private View rootView;
    private boolean hasImage = false;

    private static final int GalleryPick = 1;

    String eventNameData;
    String eventDescriptionData;
    String endDateData;
    String targetQuantityData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

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
                }
                return false;
            }
        });

        createEventConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventNameData = eventName.getText().toString();
                eventDescriptionData = eventDescription.getText().toString();
                endDateData = eventEndDate.getText().toString();
                targetQuantityData = targetQuantity.getText().toString();
                InputValidation();
            }
        });

        eventPhoto.setOnClickListener(new View.OnClickListener() { // TODO CHECK PERMISSION
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);

                // Old code
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                    startActivityForResult(intent, TAKE_IMAGE_CODE);
//                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == Activity.RESULT_OK && data != null) {
            Uri ImageURI = data.getData();

//            ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), ImageURI);
//            try {
//                bitmap = ImageDecoder.decodeBitmap(source);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//            try {
//                Bitmap  mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Log.d(TAG, String.valueOf(bitmap));
//            eventPhoto.setImageBitmap(bitmap);

            CropImage.activity(ImageURI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(16,9)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), resultUri);
                try {
                    bitmap = ImageDecoder.decodeBitmap(source);
                    hasImage = true;
                    Log.d(TAG, String.valueOf(bitmap));
                    eventPhoto.setImageBitmap(bitmap);
//                    handleUpload(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG, String.valueOf(error));
            }
        }

        // Old code
//        if (requestCode == TAKE_IMAGE_CODE) {
//            switch (resultCode) {
//                case Activity.RESULT_OK:
//                    bitmap = (Bitmap) data.getExtras().get("data");
//                    eventPhoto.setImageBitmap(bitmap);
//                    handleUpload(bitmap);
//            }
//        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.v(TAG, date);
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("event-image").child(uuid + " " + date + ".jpeg");

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
                        eventImageURI = uri.toString();
                        Log.d(TAG,eventImageURI);
                        InitializeEvent();
                    }
                });
    }

    private void InputValidation() {
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

        if (endDateData.isEmpty()) {
            eventEndDateLayout.setError("Please fill in the event end date");
        } else {
            eventEndDateLayout.setErrorEnabled(false);
        }

        if (targetQuantityData.isEmpty()) {
            targetQuantityLayout.setError("Please fill in the target quantity");
        } else {
            targetQuantityLayout.setErrorEnabled(false);
        }

        if (!hasImage) {
            Toast.makeText(getContext(), "Please insert the event image", Toast.LENGTH_SHORT).show();
        }

        if (eventNameData.isEmpty() && eventDescriptionData.isEmpty() && endDateData.isEmpty() && targetQuantityData.isEmpty() && !hasImage) {
            Toast.makeText(getContext(), "Please complete in all the information", Toast.LENGTH_SHORT).show();
        } else if (!eventNameData.isEmpty() && !eventDescriptionData.isEmpty() && !endDateData.isEmpty() && !targetQuantityData.isEmpty() && hasImage) {
            progressBar.setVisibility(View.VISIBLE);
            createEventConfirmation.setVisibility(View.INVISIBLE);
            handleUpload(bitmap);
        } else {
            Toast.makeText(getContext(), "Error occurred, please try again", Toast.LENGTH_SHORT).show();
        } // TODO don't forget image
    }

    private void InitializeEvent() {
        String socialCommunityID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String socialCommunityName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName(); // TODO SET DISPLAY NAME LATER FIRST

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String eventId = db.collection("events").document().getId(); // EaYQPHbG7NR1K4zdUMxx
        Log.d(TAG, eventId);

        final Event event = new Event(); // TODO ADD THE CONSTRUCTOR ADD IMAGE URI AT THE FIRST
        // TODO WATCH THIS TO COMPLETE https://www.youtube.com/watch?v=xnFnwbiDFuE
        event.setImageURI(eventImageURI);
        event.setEventID(eventId);
        event.setTitle(eventNameData);
        event.setDescription(eventDescriptionData);
        event.setSocialCommunityID(socialCommunityID);
        event.setSocialCommunityName(socialCommunityName);
        event.setEndDate(endDateData);
        event.setEndDateInMillis(chosenDateInMillis);
        event.setTargetQuantity(Double.parseDouble(targetQuantityData));
        event.setTotalDonation(0);

        db.collection("users").document(socialCommunityID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            event.setSocialCommunityTelephoneNumber(documentSnapshot.getString("phone"));
                            db.collection("events").document(eventId)
                                    .set(event)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Event is successfully created", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getContext(), MainSocialCommunityActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            Log.d(TAG, "Event successfully written!");
                                            progressBar.setVisibility(View.INVISIBLE);
                                            createEventConfirmation.setVisibility(View.VISIBLE);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error writing document", e);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            createEventConfirmation.setVisibility(View.VISIBLE);
                                        }
                                    });
                        }
                    }
                });
    }
}
