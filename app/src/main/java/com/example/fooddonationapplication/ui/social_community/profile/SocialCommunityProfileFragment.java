package com.example.fooddonationapplication.ui.social_community.profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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

import com.example.fooddonationapplication.model.Donator;
import com.example.fooddonationapplication.model.SocialCommunity;
import com.example.fooddonationapplication.ui.general.LoginActivity;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.example.fooddonationapplication.util.constant.RequestCodeConstant;
import com.example.fooddonationapplication.viewmodel.SocialCommunityProfileViewModel;
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

    private FragmentActivity activity;

    private TextInputLayout telephoneNumberLayout, descriptionLayout;
    private EditText fullName, telephoneNumber, description, totalEventCreated;
    private Button updateCredentialButton, updateProfileButton, logOutButton;
    private ProgressBar updateProgressBar, socialCommunityProfilePhotoProgressBar, updateCredentialProgressBar;
    private ImageView socialCommunityPhoto;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // For storing and comparing the user data for update
    private SocialCommunity oldUserData;
    private SocialCommunity newUserData = new SocialCommunity();

    private boolean hasChanged;
    private boolean hasTelephoneNumberChanged;

    // For photo
    private static final int GalleryPick = 1;
    private Bitmap bitmap;
    private String eventImageURI;
    private boolean hasImageChanged;

    // View Model
    private SocialCommunityProfileViewModel mViewModel;

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile_social_community, container, false);
        activity = requireActivity();
        initializeComponents();

        updateProgressBar.setVisibility(View.INVISIBLE);
        updateCredentialProgressBar.setVisibility(View.INVISIBLE);

        // Disable Total Event Created Edit Text
        totalEventCreated.setFocusable(false);
        totalEventCreated.setFocusableInTouchMode(false);
        totalEventCreated.setCursorVisible(false);

        hasChanged = false;
        hasTelephoneNumberChanged = false;

        mViewModel = new ViewModelProvider(this).get(SocialCommunityProfileViewModel.class);
        if (mViewModel.getImageBitmap() != null) {
            bitmap = mViewModel.getImageBitmap();
            socialCommunityPhoto.setImageBitmap(bitmap);
            socialCommunityProfilePhotoProgressBar.setVisibility(View.INVISIBLE);
            hasImageChanged = mViewModel.isHasImageChanged();
        } else {
            Picasso.get().load(user.getPhotoUrl()).error(R.drawable.ic_error_black_24dp).into(socialCommunityPhoto, new com.squareup.picasso.Callback() {
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

        // Retrieving data from activity
        FragmentActivity fragmentActivity = requireActivity();
        oldUserData = fragmentActivity.getIntent().getParcelableExtra(IntentNameExtra.SOCIAL_COMMUNITY_MODEL);
        if (oldUserData == null) {
            Toast.makeText(fragmentActivity, "Data isn't loaded", Toast.LENGTH_SHORT).show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            oldUserData = document.toObject(SocialCommunity.class);
                            initializeTextData();
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } else {
            initializeTextData();
        }

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        updateCredentialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(activity, LoginActivity.class);
                loginIntent.putExtra(IntentNameExtra.IS_REAUTH, true);
                loginIntent.putExtra(IntentNameExtra.USER_EMAIL, user.getEmail());

                startActivityForResult(loginIntent, RequestCodeConstant.REAUTH_REQUEST_CODE);
            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidated()) {
                    updateProgressBar.setVisibility(View.VISIBLE);
                    updateProfileButton.setVisibility(View.INVISIBLE);
                    updateCredentialButton.setEnabled(false);
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

    private void initializeTextData() {
        if (user != null) {
            fullName.setText(user.getDisplayName());
        }
        telephoneNumber.setText(oldUserData.getPhone());
        description.setText(oldUserData.getDescription());
        totalEventCreated.setText(String.valueOf(oldUserData.getTotalEventCreated()));
    }

    private void allActionStatus(boolean status) {
        telephoneNumber.setFocusable(status);
        telephoneNumber.setFocusableInTouchMode(status);
        telephoneNumber.setCursorVisible(status);
        description.setFocusable(status);
        description.setFocusableInTouchMode(status);
        description.setCursorVisible(status);
        socialCommunityPhoto.setEnabled(status);
    }

    private void checkingChanges() {
        newUserData.setName(fullName.getText().toString());
        newUserData.setPhone(telephoneNumber.getText().toString());
        newUserData.setDescription(description.getText().toString());

        hasChanged = !oldUserData.isSame(newUserData);
        Log.d(TAG, "hasChanged = " + hasChanged);
        Log.d(TAG, "oldUserData" + oldUserData.getDescription());
        Log.d(TAG, "newUserData" + newUserData.getDescription());

        if (!newUserData.getPhone().equalsIgnoreCase(oldUserData.getPhone())) {
            hasTelephoneNumberChanged = true;
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
            if (hasTelephoneNumberChanged) {
                getDonatorDocumentData();
            } else {
                updateUserDatabase();
            }
        } else {
            Toast.makeText(getContext(), "Nothing has changed", Toast.LENGTH_SHORT).show();
            updateCredentialButton.setEnabled(true);
            updateProgressBar.setVisibility(View.INVISIBLE);
            updateProfileButton.setVisibility(View.VISIBLE);
            allActionStatus(true);
        }
    }

    private void getDonatorDocumentData() {
        db.collection("donators").whereEqualTo("socialCommunityId", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());
                            }
                            Log.d(TAG, list.toString());
                            updateDonatorDatabase((ArrayList) list);
                        }
                    }
                });
    }

    private void updateDonatorDatabase(ArrayList list) {
        WriteBatch batch = db.batch();
        for (int i = 0; i < list.size(); i++) {
            DocumentReference eventReference = db.collection("donators").document((String) list.get(i));
            batch.update(eventReference, "socialCommunityPhoneNumber", newUserData.getPhone());
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUserDatabase();
            }
        });
    }

    private void updateUserDatabase() {
        WriteBatch batch = db.batch();
        DocumentReference userReference = db.collection("users").document(user.getUid());
        batch.update(userReference, "description", newUserData.getDescription());
        batch.update(userReference, "phone", newUserData.getPhone());
        batch.update(userReference, "imageURI", newUserData.getImageURI());

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateCredentialButton.setEnabled(true);
                updateProgressBar.setVisibility(View.INVISIBLE);
                updateProfileButton.setVisibility(View.VISIBLE);
                allActionStatus(true);

                // Checking for the new things again
                oldUserData.setDescription(newUserData.getDescription());
                oldUserData.setPhone(newUserData.getPhone());
                oldUserData.setImageURI(newUserData.getImageURI());
                hasImageChanged = false;
                hasChanged = false;
                hasTelephoneNumberChanged = false;
                mViewModel.setHasImageChanged(false);
                Toast.makeText(getContext(), "Profile successfully updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidated() {
        boolean isValidated = false;
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

        if (telephoneNumberValidation && descriptionValidation) {
            isValidated = true;
        }
        return isValidated;
    }

    private void openChangeCredential() {
        Util.hideKeyboard(requireActivity());
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View updateCredentialDialog = getLayoutInflater().inflate(R.layout.dialog_option, null);
        mBuilder.setView(updateCredentialDialog);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        TextView dialogTitle = updateCredentialDialog.findViewById(R.id.registerDialogTitle);
        Button emailButton = updateCredentialDialog.findViewById(R.id.registerDialogDonatorButton);
        Button passwordButton = updateCredentialDialog.findViewById(R.id.registerDialogSocialCommunityButton);
        dialogTitle.setText("Choose Credential:");
        emailButton.setText("E-mail");
        passwordButton.setText("Password");

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                View updateCredentialDialog = getLayoutInflater().inflate(R.layout.dialog_credential_update_email, null);
                mBuilder.setView(updateCredentialDialog);
                final AlertDialog emailDialog = mBuilder.create();
                emailDialog.show();
                final TextInputLayout emailInputLayout = updateCredentialDialog.findViewById(R.id.updateEmailCredentialDialogEmailLayout);
                final EditText emailInput = updateCredentialDialog.findViewById(R.id.updateEmailCredentialDialogEmail);
                Button updateEmailButton = updateCredentialDialog.findViewById(R.id.updateEmailCredentialDialogButton);
                updateEmailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                        if (emailInput.getText().toString().isEmpty()) {
                            emailInputLayout.setError("Please input your new email");
                        } else if (!emailInput.getText().toString().matches(emailPattern)) {
                            emailInputLayout.setError("Please input a valid email");
                        } else if (emailInput.getText().toString().equalsIgnoreCase(user.getEmail())) {
                            emailInputLayout.setError("The new email is the same with the current email");
                        } else {
                            String newEmail = emailInput.getText().toString();
                            emailDialog.dismiss();
                            updateProfileButton.setEnabled(false);
                            logOutButton.setEnabled(false);
                            updateCredentialButton.setVisibility(View.INVISIBLE);
                            updateCredentialProgressBar.setVisibility(View.VISIBLE);
                            user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        updateCredentialButton.setVisibility(View.VISIBLE);
                                        updateCredentialProgressBar.setVisibility(View.INVISIBLE);
                                        updateProfileButton.setEnabled(true);
                                        logOutButton.setEnabled(true);
                                        Toast.makeText(getContext(), "User email address updated.", Toast.LENGTH_SHORT).show();
                                    } else if (!task.isSuccessful()) {
                                        Log.d(TAG, String.valueOf(task.getException()));
                                        Toast.makeText(getContext(), String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
                                        updateCredentialButton.setVisibility(View.VISIBLE);
                                        updateCredentialProgressBar.setVisibility(View.INVISIBLE);
                                        updateProfileButton.setEnabled(true);
                                        logOutButton.setEnabled(true);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                View updateCredentialDialog = getLayoutInflater().inflate(R.layout.dialog_credential_update_password, null);
                mBuilder.setView(updateCredentialDialog);
                final AlertDialog passwordDialog = mBuilder.create();
                passwordDialog.show();

                final TextInputLayout passwordInputLayout = updateCredentialDialog.findViewById(R.id.updatePasswordCredentialDialogPasswordLayout);
                final EditText passwordInput = updateCredentialDialog.findViewById(R.id.updatePasswordCredentialDialogPassword);
                final TextInputLayout confirmPasswordInputLayout = updateCredentialDialog.findViewById(R.id.updatePasswordCredentialDialogConfirmPasswordLayout);
                final EditText confirmPasswordInput = updateCredentialDialog.findViewById(R.id.updatePasswordCredentialDialogConfirmPassword);
                Button updatePasswordButton = updateCredentialDialog.findViewById(R.id.updatePasswordCredentialPassDialogUpdateButton);
                updatePasswordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (passwordInput.getText().toString().isEmpty() && confirmPasswordInput.getText().toString().isEmpty()) {
                            passwordInputLayout.setError("Please insert your new password");
                            confirmPasswordInputLayout.setError("Please input your new password");
                        } else if (!passwordInput.getText().toString().isEmpty() || !confirmPasswordInput.getText().toString().isEmpty()) {
                            if (passwordInput.getText().toString().isEmpty()) {
                                passwordInputLayout.setError("Please insert your new password");
                            } else if (passwordInput.getText().toString().length() <= 5) {
                                passwordInputLayout.setError("Password minimum is 6 digit");
                            } else {
                                passwordInputLayout.setErrorEnabled(false);
                            }

                            if (confirmPasswordInput.getText().toString().isEmpty()) {
                                confirmPasswordInputLayout.setError("Please insert your new password");
                            } else if (confirmPasswordInput.getText().toString().length() <= 5) {
                                confirmPasswordInputLayout.setError("Password minimum is 6 digit");
                            } else {
                                confirmPasswordInputLayout.setErrorEnabled(false);
                            }

                            if (!passwordInput.getText().toString().equals(confirmPasswordInput.getText().toString())) {
                                confirmPasswordInputLayout.setError("The password is not matching");
                            } else {
                                confirmPasswordInputLayout.setErrorEnabled(false);
                                passwordDialog.dismiss();
                                Util.hideKeyboard(requireActivity());
                                updateProfileButton.setEnabled(false);
                                logOutButton.setEnabled(false);
                                updateCredentialButton.setVisibility(View.INVISIBLE);
                                updateCredentialProgressBar.setVisibility(View.VISIBLE);
                                user.updatePassword(passwordInput.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            updateCredentialButton.setVisibility(View.VISIBLE);
                                            updateCredentialProgressBar.setVisibility(View.INVISIBLE);
                                            updateProfileButton.setEnabled(true);
                                            logOutButton.setEnabled(true);
                                            Toast.makeText(getContext(), "User password updated.", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "User password updated.");
                                        }
                                    }
                                });
                            }
                        }
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
                    .setAspectRatio(1,1)
                    .start(getContext(), this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), resultUri);
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
        if (requestCode == RequestCodeConstant.REAUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    boolean isReauthSuccess = data.getBooleanExtra(IntentNameExtra.REAUTH_RESULT, false);
                    if (isReauthSuccess) {
                        Toast.makeText(activity, "Reauthentication successful.", Toast.LENGTH_SHORT).show();
                        openChangeCredential();
                    } else {
                        Toast.makeText(activity, "Reauthentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(activity, "Reauthentication failed.", Toast.LENGTH_SHORT).show();
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
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
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

    private void initializeComponents() {
        fullName = rootView.findViewById(R.id.socialCommunityProfileFullName);
        telephoneNumber = rootView.findViewById(R.id.socialCommunityProfileTelephoneNumber);
        description = rootView.findViewById(R.id.socialCommunityProfileDescription);
        totalEventCreated = rootView.findViewById(R.id.socialCommunityProfileTotalEventCreated);

        telephoneNumberLayout = rootView.findViewById(R.id.socialCommunityProfileTelephoneNumberLayout);
        descriptionLayout = rootView.findViewById(R.id.socialCommunityProfileDescriptionLayout);

        updateCredentialButton = rootView.findViewById(R.id.socialCommunityProfileUpdateCredentialButton);
        updateProfileButton = rootView.findViewById(R.id.socialCommunityProfileUpdateButton);
        logOutButton = rootView.findViewById(R.id.socialCommunityProfileLogOutButton);

        updateCredentialProgressBar = rootView.findViewById(R.id.socialCommunityProfileUpdateCredentialProgressBar);
        updateProgressBar = rootView.findViewById(R.id.socialCommunityProfileProgressBar);
        socialCommunityProfilePhotoProgressBar = rootView.findViewById(R.id.socialCommunityProfilePhotoProgressBar);

        socialCommunityPhoto = rootView.findViewById(R.id.socialCommunityProfilePhoto);
    }
}
