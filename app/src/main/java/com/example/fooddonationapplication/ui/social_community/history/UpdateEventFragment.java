package com.example.fooddonationapplication.ui.social_community.history;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.services.MySingleton;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.model.Event;
import com.example.fooddonationapplication.ui.social_community.MainSocialCommunityActivity;
import com.example.fooddonationapplication.viewmodel.UpdateEventViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateEventFragment extends Fragment {

    private static final String TAG = "UpdateEventFragment";
    private TextView eventTitle;
    private EditText eventDescription, eventEndDate, eventTargetQuantity, eventTotalDonation;
    private TextInputLayout eventDescriptionLayout, eventEndDateLayout, eventTargetQuantityLayout;
    private ImageView eventPhoto;
    private Button updateEventButton, deleteEventButton, sendNotificationButton;
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

    // Send Notification
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA_kQBhCI:APA91bFc0c2ZXDUJdQRzTIESv_qs2SLJocXUPqPII0WSjaeTEZgshflKKBOk74lJAWkoFBCcz7THIBlXEmDoCaHzfaMOwv4ZEh-5ZiQP1GBAREorM8mypdYwvvbxx87aY3ZuVLzib_tJ";
    final private String contentType = "application/json";

    public UpdateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_event_update, container, false);

        eventTitle = rootView.findViewById(R.id.updateEventTitle);

        eventDescription = rootView.findViewById(R.id.updateEventDescription);
        eventEndDate = rootView.findViewById(R.id.updateEventEndDate);
        eventTargetQuantity = rootView.findViewById(R.id.updateEventTargetQuantity);
        eventTotalDonation = rootView.findViewById(R.id.updateEventTotalDonation);

        eventDescriptionLayout = rootView.findViewById(R.id.updateEventDescriptionLayout);
        eventEndDateLayout = rootView.findViewById(R.id.updateEventEndDateLayout);
        eventTargetQuantityLayout = rootView.findViewById(R.id.updateEventTargetQuantityLayout);

        eventPhoto = rootView.findViewById(R.id.updateEventImage);

        updateEventButton = rootView.findViewById(R.id.updateEventConfirmButton);
        deleteEventButton = rootView.findViewById(R.id.updateEventDeleteButton);
        sendNotificationButton = rootView.findViewById(R.id.updateEventSendNotificationButton);

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

        // Initialize data for end date in Millis
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

        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getTotalDonation() > 0) {
                    Toast.makeText(getContext(), "Please ensure that the event have no donator", Toast.LENGTH_SHORT).show();
                } else {
                    setUpDeleteEventDialog();
                }
            }
        });

        updateEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasEmptyField()) {
                    allActionStatus(false, "UPDATE_START");
                    checkingChanges();
                }
            }
        });

        eventPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                    setupCalendar();
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

        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allActionStatus(false, "NOTIFICATION_START");
                if (event.getEndDateInMillis() < System.currentTimeMillis()) {
                    allActionStatus(true, "NOTIFICATION_FINISH");
                    Toast.makeText(requireActivity(), "Sorry, your event is expired already", Toast.LENGTH_SHORT).show();
                } else {
                    sendNotificationButton.setVisibility(View.INVISIBLE);
                    String userUuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    db.collection("users").document(userUuid)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        long notificationDateInMillis = documentSnapshot.getLong("notificationAvailabilityInMillis");
                                        if (System.currentTimeMillis() > notificationDateInMillis) {
                                            setUpNotificationData();
                                        } else {
                                            allActionStatus(true, "NOTIFICATION_FINISH");
                                            Toast.makeText(requireActivity(), "Sorry, you can only sent notification once every two weeks", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });
        return rootView;
    }

    private void setUpDeleteEventDialog() {
        final MaterialDialog mDialog = new MaterialDialog.Builder(requireActivity())
                .setAnimation(R.raw.delete_animation)
                .setTitle("Delete Event")
                .setMessage("Are you sure want to delete this Event?")
                .setCancelable(false)
                .setPositiveButton("Delete", R.drawable.ic_delete_forever_black_24dp, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        // Hide the dialog again
                        dialogInterface.dismiss();
                        deleteEvent();
                    }
                })
                .setNegativeButton("Cancel", R.drawable.ic_cancel_black_24dp, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();
        mDialog.show();
    }

    private void setUpNotificationData() {
        String TOPIC = "/topics/FoodDonation"; //topic must match with what the receiver subscribed to
        String NOTIFICATION_TITLE = event.getTitle();
        String NOTIFICATION_MESSAGE = event.getDescription();

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
//                Toast.makeText(getContext(), "JSON " + notification, Toast.LENGTH_SHORT).show();
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(getContext(), "onResponse" + response.toString(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getContext(), "Request error", Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        updateUserDatabase();
    }

    private void updateUserDatabase() {
        String userUuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userReference = db.collection("users").document(userUuid);
        userReference.update("notificationAvailabilityInMillis", System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7 * 2)) // 2 Weeks
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        allActionStatus(true, "NOTIFICATION_FINISH");
                        Toast.makeText(requireActivity(), "Sending notification is completed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void allActionStatus(boolean status, String action) {
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
        if (action.equalsIgnoreCase("UPDATE_START")) {
            updateEventButton.setVisibility(View.INVISIBLE);
            updateEventProgressBar.setVisibility(View.VISIBLE);
            sendNotificationButton.setEnabled(false);
            deleteEventButton.setEnabled(false);
        } else if (action.equalsIgnoreCase("UPDATE_FINISH")) {
            updateEventProgressBar.setVisibility(View.INVISIBLE);
            updateEventButton.setVisibility(View.VISIBLE);
            sendNotificationButton.setEnabled(true);
            deleteEventButton.setEnabled(true);
        } else if (action.equalsIgnoreCase("DELETE_START")) {
            deleteEventButton.setVisibility(View.INVISIBLE);
            deleteEventProgressBar.setVisibility(View.VISIBLE);
            updateEventButton.setEnabled(false);
            sendNotificationButton.setEnabled(false);
        } else if (action.equalsIgnoreCase("DELETE_FINISH")) {
            deleteEventProgressBar.setVisibility(View.INVISIBLE);
            deleteEventButton.setVisibility(View.VISIBLE);
            updateEventButton.setEnabled(true);
            sendNotificationButton.setEnabled(true);
        } else if (action.equalsIgnoreCase("NOTIFICATION_START")) {
            sendNotificationButton.setVisibility(View.INVISIBLE);
            sendNotificationProgressBar.setVisibility(View.VISIBLE);
            updateEventButton.setEnabled(false);
            deleteEventButton.setEnabled(false);
        } else if (action.equalsIgnoreCase("NOTIFICATION_FINISH")) {
            sendNotificationProgressBar.setVisibility(View.INVISIBLE);
            sendNotificationButton.setVisibility(View.VISIBLE);
            updateEventButton.setEnabled(true);
            deleteEventButton.setEnabled(true);
        }
    }

    private void setupCalendar() {
        Util.hideKeyboard(requireActivity());
        // To hide the keyboard when transitioning from other text field to end date text field
        calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
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
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                eventEndDate.clearFocus();
            }
        });
        datePickerDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventTotalDonationListerner.remove();
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
                    allActionStatus(true, "UPDATE_FINISH");

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
            allActionStatus(true, "UPDATE_FINISH");
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
        allActionStatus(false, "DELETE_START");
        WriteBatch batch = db.batch();
        DocumentReference eventReference = db.collection("events").document(event.getEventID());
        batch.delete(eventReference); // TODO don't use batch, use single deletion

        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference socialCommunityReference = db.collection("users").document(uuid);
        batch.update(socialCommunityReference, "totalEventCreated", FieldValue.increment(-1));

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
                Toast.makeText(requireActivity(), "Media Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == Activity.RESULT_OK && data != null) {
            Uri ImageURI = data.getData();

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
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), resultUri);
                    hasImageChanged = true;
                    mViewModel.setHasImageChanged(true);
                    eventPhoto.setImageBitmap(bitmap);
                    mViewModel.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), resultUri);
//                try {
//                    bitmap = ImageDecoder.decodeBitmap(source);
//                    hasImageChanged = true;
//                    mViewModel.setHasImageChanged(true);
//                    eventPhoto.setImageBitmap(bitmap);
//                    mViewModel.setImageBitmap(bitmap);
//                } catch (IOException e) {
//                    Log.e("UpdateEventFragment", e.getLocalizedMessage());
//                    e.printStackTrace();
//                }
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
