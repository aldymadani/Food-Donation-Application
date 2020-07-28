package com.example.fooddonationapplication.ui.social_community.profile;

import android.Manifest;
import android.app.Activity;
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
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fooddonationapplication.model.User;
import com.example.fooddonationapplication.ui.general.LoginActivity;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.viewmodel.SocialCommunityProfileViewModel;
import com.example.fooddonationapplication.viewmodel.UpdateEventViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SocialCommunityProfileFragment extends Fragment {

    private static final String TAG = "SocialProfileFragment";

    private EditText fullName, email, telephoneNumber, description, password, confirmPassword;
    private TextInputLayout emailLayout, telephoneNumberLayout, descriptionLayout, passwordLayout, confirmPasswordLayout;
    private Button updateProfileButton, logOutButton;
    private ProgressBar updateProgressBar, socialCommunityProfilePhotoProgressBar;
    private ImageView socialCommunityPhoto;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private User oldUserData = new User();
    private User newUserData = new User();

    // View Model
    private SocialCommunityProfileViewModel mViewModel;

    private boolean hasChanged;

    // For photo
    private static final int GalleryPick = 1;
    private Bitmap bitmap;
    private String eventImageURI;
    private boolean hasImageChanged;

    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_social_community_profile, container, false);

        fullName = rootView.findViewById(R.id.socialCommunityProfileFullName);
        email = rootView.findViewById(R.id.socialCommunityProfileEmail);
        telephoneNumber = rootView.findViewById(R.id.socialCommunityProfileTelephoneNumber);
        description = rootView.findViewById(R.id.socialCommunityProfileDescription);
        password = rootView.findViewById(R.id.socialCommunityProfilePassword);
        confirmPassword = rootView.findViewById(R.id.socialCommunityProfileConfirmPassword);

        emailLayout = rootView.findViewById(R.id.socialCommunityProfileEmailLayout);
        telephoneNumberLayout = rootView.findViewById(R.id.socialCommunityProfileTelephoneNumberLayout);
        descriptionLayout = rootView.findViewById(R.id.socialCommunityProfileDescriptionLayout);
        passwordLayout = rootView.findViewById(R.id.socialCommunityProfilePasswordLayout);
        confirmPasswordLayout = rootView.findViewById(R.id.socialCommunityProfileConfirmPasswordLayout);

        updateProfileButton = rootView.findViewById(R.id.socialCommunityProfileUpdateButton);
        logOutButton = rootView.findViewById(R.id.socialCommunityProfileLogOutButton);

        updateProgressBar = rootView.findViewById(R.id.socialCommunityProfileProgressBar);
        socialCommunityProfilePhotoProgressBar = rootView.findViewById(R.id.socialCommunityProfilePhotoProgressBar);

        socialCommunityPhoto = rootView.findViewById(R.id.socialCommunityProfilePhoto);

        updateProgressBar.setVisibility(View.INVISIBLE);

        hasChanged = false;

        fullName.setText(user.getDisplayName());
        email.setText(user.getEmail());

        mViewModel = new ViewModelProvider(this).get(SocialCommunityProfileViewModel.class);
        if (mViewModel.getImageBitmap() != null) {
            bitmap = mViewModel.getImageBitmap();
            socialCommunityPhoto.setImageBitmap(bitmap);
            socialCommunityProfilePhotoProgressBar.setVisibility(View.INVISIBLE); // TODO add image loading later on
            hasImageChanged = mViewModel.isHasImageChanged();
        } else {
            Picasso.get().load(user.getPhotoUrl()).error(R.drawable.ic_error_black_24dp).into(socialCommunityPhoto, new com.squareup.picasso.Callback() { // TODO pass the URL from login / register activity, got a little bug if user.getPhotoUrl() don't have value
                @Override
                public void onSuccess() {
                    socialCommunityProfilePhotoProgressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    socialCommunityProfilePhotoProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Error in loading the image", Toast.LENGTH_SHORT).show();
                }
            });
        }

        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            oldUserData.setDescription(documentSnapshot.getString("description"));
                            oldUserData.setPhone(documentSnapshot.getString("phone")); // TODO try to pass from activity login
                            oldUserData.setImageURI(documentSnapshot.getString("imageURI"));
                            telephoneNumber.setText(oldUserData.getPhone());
                            description.setText(oldUserData.getDescription());
                        }
                    }
                });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidated()) {
                    updateProgressBar.setVisibility(View.VISIBLE);
                    updateProfileButton.setVisibility(View.INVISIBLE);
                    allActionStatus(false);
                    checkingChanges();
                }
            }
        });

        socialCommunityPhoto.setOnClickListener(new View.OnClickListener() {
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
        return rootView;
    }

    private void allActionStatus(boolean status) {
        email.setFocusable(status);
        email.setFocusableInTouchMode(status);
        email.setCursorVisible(status);
        telephoneNumber.setFocusable(status);
        telephoneNumber.setFocusableInTouchMode(status);
        telephoneNumber.setCursorVisible(status);
        description.setFocusable(status);
        description.setFocusableInTouchMode(status);
        description.setCursorVisible(status);
        password.setFocusable(status);
        password.setFocusableInTouchMode(status);
        password.setCursorVisible(status);
        confirmPassword.setFocusable(status);
        confirmPassword.setFocusableInTouchMode(status);
        confirmPassword.setCursorVisible(status);
        socialCommunityPhoto.setEnabled(status);
    }

    private void checkingChanges() {
        newUserData.setPhone(telephoneNumber.getText().toString());
        newUserData.setDescription(description.getText().toString());

        hasChanged = !oldUserData.isSame(newUserData);

        if (!password.getText().toString().isEmpty()) {
            hasChanged = true;
        }

        if (hasImageChanged) {
            hasChanged = true;
            handleUpload(bitmap);
        } else {
            newUserData.setImageURI(oldUserData.getImageURI());
            updateUserData();
        }
    }

    private void updateUserData() {
        if (hasChanged) {
            if (!email.getText().toString().equals(user.getEmail())) {
                emailLayout.setErrorEnabled(false);
                user.updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "User email address updated.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            if (!confirmPassword.getText().toString().isEmpty()) {
                user.updatePassword(confirmPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "User password updated.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });
            }

            getData();
        } else {
            Toast.makeText(getContext(), "Nothing has changed", Toast.LENGTH_SHORT).show();
            updateProgressBar.setVisibility(View.INVISIBLE);
            updateProfileButton.setVisibility(View.VISIBLE);
        }
    }

    private void getData() {
        db.collection("events").whereEqualTo("socialCommunityID", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());
                            }
                            Log.d(TAG, list.toString());
                            updateData((ArrayList) list);
                        }
                    }
                });
    }

    private void updateData(ArrayList list) {
        WriteBatch batch = db.batch();
        for (int i = 0; i < list.size(); i++) {
            DocumentReference eventReference = db.collection("events").document((String) list.get(i));
            batch.update(eventReference, "socialCommunityTelephoneNumber", newUserData.getPhone());
//            if (!newPhoneNumber.equals(oldPhoneNumber)) {
//                DocumentReference ref = db.collection("donators").document((String) list.get(i));
//                batch.update(ref, "name", user.getDisplayName());
//                batch.update(ref, "phone", newPhoneNumber);
//            } else {
//                DocumentReference ref = db.collection("donators").document((String) list.get(i));
//                batch.update(ref, "name", user.getDisplayName());
//            }
        }

        DocumentReference userReference = db.collection("users").document(user.getUid());
        batch.update(userReference, "description", newUserData.getDescription());
        batch.update(userReference, "phone", newUserData.getPhone());
        batch.update(userReference, "imageURI", newUserData.getImageURI());

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Profile is successfully updated", Toast.LENGTH_SHORT).show();
                updateProgressBar.setVisibility(View.INVISIBLE);
                updateProfileButton.setVisibility(View.VISIBLE);
                allActionStatus(true);

                // Make password & confirm password empty after updation
                password.setText("");
                confirmPassword.setText("");

                // Checking for the new things again
                oldUserData.setDescription(newUserData.getDescription());
                oldUserData.setPhone(newUserData.getPhone());
                oldUserData.setImageURI(newUserData.getImageURI());
                hasImageChanged = false;
                hasChanged = false;
                mViewModel.setHasImageChanged(false);
                Toast.makeText(getContext(), "Profile successfully updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidated() {
        boolean isValidated = false;
        boolean emailValidation = false;
        if (email.getText().toString().isEmpty()) {
            emailLayout.setError("Please insert your email");
        } else {
            emailValidation = true;
            emailLayout.setErrorEnabled(false);
        }

        boolean telephoneNumberValidation = false;
        if (telephoneNumber.getText().toString().isEmpty()) {
            telephoneNumberLayout.setError("Please input your telephone number");
        } else if (telephoneNumber.getText().toString().length() <= 7 || telephoneNumber.getText().toString().length() > 13) {
            telephoneNumberLayout.setError("Please Please input a valid telephone number");
        } else {
            telephoneNumberValidation = true;
            telephoneNumberLayout.setErrorEnabled(false);
        }

        boolean descriptionValidation = false;
        if (description.getText().toString().isEmpty()) {
            descriptionLayout.setError("Please input your description");
        } else {
            descriptionValidation = true;
            descriptionLayout.setErrorEnabled(false);
        }

        boolean passwordValidation = false;
        if (password.getText().toString().isEmpty() && confirmPassword.getText().toString().isEmpty()) {
            passwordValidation = true;
        } else if (!password.getText().toString().isEmpty() || !confirmPassword.getText().toString().isEmpty()) {
            if (password.getText().toString().isEmpty()) {
                passwordLayout.setError("Please insert your new password");
            } else if (password.getText().toString().length() <= 5) {
                passwordLayout.setError("Password minimum is 6 digit");
            } else {
                passwordLayout.setErrorEnabled(false);
            }

            if (confirmPassword.getText().toString().isEmpty()) {
                confirmPasswordLayout.setError("Please insert your new password");
            } else if (confirmPassword.getText().toString().length() <= 5) {
                confirmPasswordLayout.setError("Password minimum is 6 digit");
            } else {
                confirmPasswordLayout.setErrorEnabled(false);
            }

            if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                confirmPasswordLayout.setError("The password is not matching");
            } else {
                passwordValidation = true;
            }
        }

        if (emailValidation && telephoneNumberValidation && descriptionValidation && passwordValidation) {
            isValidated = true;
        }
        return isValidated;
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
            // TODO try this later
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), ImageURI);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            CropImage.activity(ImageURI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(16,9) // TODO change to 1x1 later on
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                // TODO: https://www.google.com/search?hl=en&q=createSource%20api%2028%20problem
                ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), resultUri);
                try {
                    bitmap = ImageDecoder.decodeBitmap(source);
                    hasImageChanged = true;
                    mViewModel.setHasImageChanged(true);
                    socialCommunityPhoto.setImageBitmap(bitmap);
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
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("event-image").child(user.getUid() + ".jpeg");

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
                        newUserData.setImageURI(eventImageURI);
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder() // TODO add profile photo to authentication later on https://firebase.google.com/docs/auth/android/manage-users
                                .setPhotoUri(Uri.parse(eventImageURI))
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User profile updated.");
                                            updateUserData();
                                        }
                                    }
                                });
                    }
                });
    }
}
