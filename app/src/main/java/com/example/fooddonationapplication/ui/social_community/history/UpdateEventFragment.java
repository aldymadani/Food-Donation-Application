package com.example.fooddonationapplication.ui.social_community.history;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.ui.social_community.MainSocialCommunityActivity;
import com.example.fooddonationapplication.viewmodel.UpdateEventViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateEventFragment extends Fragment {

    private static final String TAG = "UpdateEventFragment";
    private TextView eventTitle;
    private EditText eventDescription, eventEndDate, eventTargetQuantity, eventTotalDonation;
    private TextInputLayout eventDescriptionLayout, eventEndDateLayout, eventTargetQuantityLayout;
    private ImageView eventPhoto;
    private Button updateEventConfirmation, deleteEventConfirmation, sendNotificationConfirmation;
    private ProgressBar updateEventProgressBar, deleteEventProgressBar, sendNotificationProgressBar, imageEventPhotoProgressBar;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Event newEvent = new Event();
    private Event event = new Event();
    private boolean hasChanged;

    View rootView;

    // For photo
    private static final int GalleryPick = 1;
    private Bitmap bitmap;
    private String eventImageURI, eventId;
    private boolean hasImageChanged;

    // For Date Picker
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private String chosenDate;
    private Long chosenDateInMillis, endDateInMillisData;

    // View Model
    private UpdateEventViewModel mViewModel;

    ListenerRegistration eventTotalDonationListerner;

    public UpdateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_update_event, container, false);

        eventTitle = rootView.findViewById(R.id.updateEventTitle);

        eventDescription = rootView.findViewById(R.id.updateEventDescription);
        eventEndDate = rootView.findViewById(R.id.updateEventEndDate);
        eventTargetQuantity = rootView.findViewById(R.id.updateEventTargetQuantity);
        eventTotalDonation = rootView.findViewById(R.id.updateEventTotalDonation);

        eventDescriptionLayout = rootView.findViewById(R.id.updateEventDescriptionLayout);
        eventEndDateLayout = rootView.findViewById(R.id.updateEventEndDateLayout);
        eventTargetQuantityLayout = rootView.findViewById(R.id.updateEventTargetQuantityLayout);

        eventPhoto = rootView.findViewById(R.id.updateEventImage);

        updateEventConfirmation = rootView.findViewById(R.id.updateEventConfirmButton);
        deleteEventConfirmation = rootView.findViewById(R.id.updateEventDeleteButton);
        sendNotificationConfirmation = rootView.findViewById(R.id.updateEventSendNotificationButton);

        updateEventProgressBar = rootView.findViewById(R.id.updateEventConfirmProgressBar);
        deleteEventProgressBar = rootView.findViewById(R.id.updateEventDeleteProgressBar);
        sendNotificationProgressBar = rootView.findViewById(R.id.updateEventSendNotificationButtonProgressBar);
        imageEventPhotoProgressBar = rootView.findViewById(R.id.updateEventImageProgressBar);

        updateEventProgressBar.setVisibility(View.INVISIBLE);
        deleteEventProgressBar.setVisibility(View.INVISIBLE);
        sendNotificationProgressBar.setVisibility(View.INVISIBLE);

        // Variable to check if there are changes
        hasChanged = false;

        // Coding started

        // Retrieving data from activity
        FragmentActivity fragmentActivity = requireActivity();
        event = fragmentActivity.getIntent().getParcelableExtra("eventData");
        if (event == null) {
            Toast.makeText(getContext(), "NULL DATA", Toast.LENGTH_SHORT).show();
            // TODO: Alert when data is null
            fragmentActivity.finish();
        }
        eventId = event.getEventID();

        // Initialize the text in text field
        eventTitle.setText((event.getTitle() + " Details").toUpperCase());
        eventDescription.setText(event.getDescription());
        eventEndDate.setText(event.getEndDate());
        eventTargetQuantity.setText(String.valueOf(event.getTargetQuantity()));
//        eventTotalDonation.setText(String.valueOf(event.getTotalDonation()));

        // Disable the totalDonation text field
        eventTotalDonation.setFocusableInTouchMode(false);
        eventTotalDonation.setFocusable(false);
        eventTotalDonation.setCursorVisible(false);

        // To hide the keyboard when user click in the end date text field
        eventEndDate.setInputType(InputType.TYPE_NULL);

        // ViewModel for Image
        mViewModel = new ViewModelProvider(this).get(UpdateEventViewModel.class);
        if (mViewModel.getImageBitmap() != null) {
            bitmap = mViewModel.getImageBitmap();
            eventPhoto.setImageBitmap(bitmap);
            imageEventPhotoProgressBar.setVisibility(View.INVISIBLE);
            hasImageChanged = mViewModel.isHasImageChanged();
        } else {
            Picasso.get().load(event.getImageURI()).error(R.drawable.ic_error_black_24dp).into(eventPhoto, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    imageEventPhotoProgressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    imageEventPhotoProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Error in loading the image", Toast.LENGTH_SHORT).show();
                }
            });
        }

        deleteEventConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getTotalDonation() > 0) {
                    Toast.makeText(getContext(), "Please ensure that the event have no donator", Toast.LENGTH_SHORT).show();
                } else {
                    deleteEventConfirmation.setVisibility(View.INVISIBLE);
                    deleteEventProgressBar.setVisibility(View.VISIBLE);
                    updateEventConfirmation.setEnabled(false);
                    sendNotificationConfirmation.setEnabled(false);
                    allActionStatus(false);
                    deleteEvent();
                }
            }
        });

        updateEventConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasEmptyField()) {
                    updateEventConfirmation.setVisibility(View.INVISIBLE);
                    updateEventProgressBar.setVisibility(View.VISIBLE);
                    deleteEventConfirmation.setEnabled(false);
                    sendNotificationConfirmation.setEnabled(false);
                    allActionStatus(false);
                    checkingChanges();
                }
            }
        });

        eventPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, GalleryPick);
                } else {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GalleryPick);
                }
            }
        });

        eventEndDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setUpCalendar();
                } else {
                    if (datePickerDialog != null) {
                        datePickerDialog.hide();
                    }
                }
            }
        });

        CollectionReference eventRef = db.collection("events");
        eventTotalDonationListerner = eventRef.document(eventId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    DecimalFormat df = new DecimalFormat("#.###");
                    String formattedTotalDonation = df.format(documentSnapshot.getDouble("totalDonation"));
                    eventTotalDonation.setText(formattedTotalDonation);
                }
            }
        });

        return rootView;
    }

    private void allActionStatus(boolean status) {
        eventDescription.setFocusable(status);
        eventDescription.setFocusableInTouchMode(status);
        eventDescription.setCursorVisible(status);
        eventEndDate.setFocusable(status);
        eventEndDate.setFocusableInTouchMode(status);
        eventEndDate.setCursorVisible(status);
        eventTargetQuantity.setFocusable(status);
        eventTargetQuantity.setFocusableInTouchMode(status);
        eventTargetQuantity.setCursorVisible(status);
        eventTotalDonation.setFocusable(status);
        eventTotalDonation.setFocusableInTouchMode(status);
        eventTotalDonation.setCursorVisible(status);
        eventPhoto.setEnabled(status);
    }

    private void setUpCalendar() {
        hideKeyboard(getActivity());
        // To hide the keyboard when transitioning from other text field to end date text field
        calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                chosenDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                eventEndDate.setText(chosenDate);
//                            chosenDate += " 23:59:59";
//                            try {
//                                chosenDateInMillis = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(chosenDate).getTime();
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
                Log.d(TAG, "Chosen Date" + chosenDate);
//                            Log.d(TAG, String.valueOf(chosenDateInMillis));
                Log.d(TAG, String.valueOf(System.currentTimeMillis()));
            }
        }, year, month, day);
        long now = System.currentTimeMillis() - 1000;
        datePickerDialog.getDatePicker().setMinDate(now + (1000 * 60 * 60 * 24 * 7));
        calendar.add(Calendar.YEAR, 0); // TODO CHECK LATER WHY NEEDED
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    eventEndDate.clearFocus();
                }
            }
        });
        datePickerDialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        eventTotalDonationListerner.remove();
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
    public void onPause() {
        super.onPause();
        if (eventEndDate.hasFocus()) {
            eventEndDate.clearFocus();
        }
    }

    private void updateEvent() {
        // TODO check has changed to check if the user has change anything
        if (hasChanged) {
            DocumentReference eventReference = db.collection("events").document(event.getEventID());
            eventReference.update(
                    "description", newEvent.getDescription(),
                    "endDate", newEvent.getEndDate(),
                    "endDateInMillis", newEvent.getEndDateInMillis(),
                    "imageURI", newEvent.getImageURI(), // TODO add image URI later
                    "targetQuantity", newEvent.getTargetQuantity()
            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    deleteEventConfirmation.setEnabled(true);
                    sendNotificationConfirmation.setEnabled(true);
                    updateEventProgressBar.setVisibility(View.INVISIBLE);
                    updateEventConfirmation.setVisibility(View.VISIBLE);
                    allActionStatus(true);

                    // Checking for the new things again
                    event.setDescription(newEvent.getDescription());
                    event.setEndDate(newEvent.getEndDate());
                    event.setEndDateInMillis(newEvent.getEndDateInMillis());
                    event.setImageURI(newEvent.getImageURI());
                    event.setTargetQuantity(newEvent.getTargetQuantity()); // TODO try to shadow copy
                    // event = newEvent
                    hasChanged = false;
                    hasImageChanged = false;
                    mViewModel.setHasImageChanged(false);
                    Toast.makeText(getContext(), "Event successfully updated", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Nothing has changed", Toast.LENGTH_SHORT).show();
            deleteEventConfirmation.setEnabled(true);
            sendNotificationConfirmation.setEnabled(true);
            updateEventProgressBar.setVisibility(View.INVISIBLE);
            updateEventConfirmation.setVisibility(View.VISIBLE);
            allActionStatus(true);
        }
    }

    private boolean hasEmptyField() {
        boolean hasEmpty = true;
        if (eventDescription.getText().toString().isEmpty()) {
            eventDescriptionLayout.setError("Please input the event description");
        } else {
            eventDescriptionLayout.setErrorEnabled(false);
        }

        if (eventEndDate.getText().toString().isEmpty()) {
            eventEndDateLayout.setError("Please input event end date");
        } else {
            eventEndDateLayout.setErrorEnabled(false);
        }

        if (eventTargetQuantity.getText().toString().isEmpty()) {
            eventTargetQuantityLayout.setError("Please input your target quantity of the event");
        } else {
            eventTargetQuantityLayout.setErrorEnabled(false);
        }

        if (!eventDescription.getText().toString().isEmpty() && !eventEndDate.getText().toString().isEmpty()
                && !eventTargetQuantity.getText().toString().isEmpty()) {
            hasEmpty = false;
        }

        return hasEmpty;
    }

    private void checkingChanges() {
        // TODO: add checking if there is empty field

//        newEvent = event;
        newEvent.setDescription(eventDescription.getText().toString());
        newEvent.setEndDate(eventEndDate.getText().toString());
        // Convert the date to Millis
        try {
            chosenDateInMillis = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(eventEndDate.getText().toString() + " 23:59:59").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newEvent.setEndDateInMillis(chosenDateInMillis);
        newEvent.setTargetQuantity(Double.parseDouble(eventTargetQuantity.getText().toString()));

        hasChanged = !event.isSame(newEvent);
        Log.d(TAG, "NEW: " + newEvent.getDescription());
        Log.d(TAG, "OLD: " + event.getDescription());
        Toast.makeText(getContext(), String.valueOf(!event.isSame(newEvent)), Toast.LENGTH_SHORT).show();

        if (hasImageChanged) {
            hasChanged = true;
            handleUpload(bitmap);
        } else {
            newEvent.setImageURI(event.getImageURI());
            updateEvent();
        }
    }

    private void deleteEvent() {
        // TODO disable update and send notification and the rest of the text and image click
        // Perform delete donation on the database
        WriteBatch batch = db.batch();
        DocumentReference donatorReference = db.collection("events").document(event.getEventID());
        batch.delete(donatorReference); // TODO don't use batch, use single deletion

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Perform deletion of the image on the firebase storage
//                String storageUrl = "event-image/" + eventID + ".jpeg";
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(event.getImageURI());
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d(TAG, "onSuccess: deleted file");
                        Intent intent = new Intent(getContext(), MainSocialCommunityActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.d(TAG, "onFailure: did not delete file");
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, String.valueOf(grantResults));
        if (requestCode == GalleryPick) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            } else {
                Toast.makeText(getActivity(), "Media Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == Activity.RESULT_OK && data != null) {
            Uri ImageURI = data.getData();
            // TODO try this later
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ImageURI);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            CropImage.activity(ImageURI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(16,9)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                // TODO: https://www.google.com/search?hl=en&q=createSource%20api%2028%20problem
                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), resultUri);
                try {
                    bitmap = ImageDecoder.decodeBitmap(source);
                    hasImageChanged = true;
                    mViewModel.setHasImageChanged(true);
                    eventPhoto.setImageBitmap(bitmap);
                    mViewModel.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Log.e("UpdateEventFragment", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG, String.valueOf(error));
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
//        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.v(TAG, date);
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("event-image").child(event.getEventID() + ".jpeg");

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
                        newEvent.setImageURI(eventImageURI);
                        updateEvent();
                    }
                });
    }
}
