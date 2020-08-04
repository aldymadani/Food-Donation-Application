package com.example.fooddonationapplication.ui.general;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.SocialCommunity;
import com.example.fooddonationapplication.ui.social_community.MainSocialCommunityActivity;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.util.constant.Constant;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.example.fooddonationapplication.viewmodel.SocialCommunityRegisterViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SocialCommunityRegisterActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private static final String TAG = "SocialRegisterActivity";
    private static final int GalleryPick = 1;
    private String socialCommunityImageURI, emailData, passwordData, confirmPasswordData, socialCommunityNameData, telephoneNumberData, socialCommunityDescriptionData;

    private EditText emailId, passwordId, confirmPasswordId, socialCommunityNameId, telephoneNumberId, socialCommunityDescription;
    private Button btnSignUp;
    private FirebaseAuth mFirebaseAuth;
    private TextInputLayout textInputEmail, textInputPassword, textInputConfirmPassword, textInputSocialCommunityName, textInputTelephoneNumber, textInputSocialCommunityDescription;
    private ProgressBar progressBar;
    private ImageView socialCommunityPhoto;
    private Bitmap bitmap;
    private boolean hasImage;

    private SocialCommunity socialCommunity = new SocialCommunity();

    SocialCommunityRegisterViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_social_community);
        initializeComponents();
        progressBar.setVisibility(View.INVISIBLE);

        mFirebaseAuth = FirebaseAuth.getInstance();

        emailId.setOnFocusChangeListener(this);
        passwordId.setOnFocusChangeListener(this);
        confirmPasswordId.setOnFocusChangeListener(this);
        socialCommunityNameId.setOnFocusChangeListener(this);
        telephoneNumberId.setOnFocusChangeListener(this);
        socialCommunityDescription.setOnFocusChangeListener(this);

        hasImage = false;
        mViewModel = new ViewModelProvider(this).get(SocialCommunityRegisterViewModel.class);
        if (mViewModel.getImageBitmap() != null) {
            bitmap = mViewModel.getImageBitmap();
            socialCommunityPhoto.setImageBitmap(bitmap);
            hasImage = true;
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailData = emailId.getText().toString();
                passwordData = passwordId.getText().toString();
                confirmPasswordData = confirmPasswordId.getText().toString();
                socialCommunityNameData = socialCommunityNameId.getText().toString();
                telephoneNumberData = telephoneNumberId.getText().toString();
                socialCommunityDescriptionData = socialCommunityDescription.getText().toString();
                emailId.clearFocus();
                passwordId.clearFocus();
                confirmPasswordId.clearFocus();
                socialCommunityNameId.clearFocus();
                telephoneNumberId.clearFocus();
                socialCommunityDescription.clearFocus();
                Util.hideKeyboard(SocialCommunityRegisterActivity.this);
                checkingAllFields();
            }
        });

        socialCommunityPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SocialCommunityRegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GalleryPick);
                } else {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GalleryPick);
                }
            }
        });
    }

    private void allActionStatus(boolean status) {
        emailId.setFocusable(status);
        emailId.setFocusableInTouchMode(status);
        emailId.setCursorVisible(status);
        passwordId.setFocusable(status);
        passwordId.setFocusableInTouchMode(status);
        passwordId.setCursorVisible(status);
        socialCommunityNameId.setFocusable(status);
        socialCommunityNameId.setFocusableInTouchMode(status);
        socialCommunityNameId.setCursorVisible(status);
        telephoneNumberId.setFocusable(status);
        telephoneNumberId.setFocusableInTouchMode(status);
        telephoneNumberId.setCursorVisible(status);
        socialCommunityDescription.setFocusable(status);
        socialCommunityDescription.setFocusableInTouchMode(status);
        socialCommunityDescription.setCursorVisible(status);
        socialCommunityPhoto.setEnabled(status);
        if (status) {
            btnSignUp.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            btnSignUp.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GalleryPick) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            } else {
                Toast.makeText(SocialCommunityRegisterActivity.this, "Media Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == Activity.RESULT_OK && data != null) {
            Uri ImageURI = data.getData();

            CropImage.activity(ImageURI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(SocialCommunityRegisterActivity.this.getContentResolver(), resultUri);
                    hasImage = true;
                    Log.d(TAG, String.valueOf(bitmap));
                    socialCommunityPhoto.setImageBitmap(bitmap);
                    mViewModel.setImageBitmap(bitmap);
                    hasImage = true;
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

    private void checkingAllFields() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Email checking
        boolean emailValidation = false;
        if (emailData.isEmpty()) {
            textInputEmail.setError("Please input your email");
        } else if (!emailData.matches(emailPattern)) {
            textInputEmail.setError("Please input a valid email");
        } else {
            emailValidation = true;
            textInputEmail.setErrorEnabled(false);
        }

        // Password checking
        boolean passwordValidation = false;
        if (passwordData.isEmpty() && confirmPasswordData.isEmpty()) {
            textInputPassword.setError("Please enter your password");
            textInputConfirmPassword.setError("Please enter your password");
        } else {
            if (passwordData.isEmpty()) {
                textInputPassword.setError("Please enter your password");
            } else if (passwordData.length() <= 5) {
                textInputPassword.setError("Password minimum is 6 digit");
            } else {
                textInputPassword.setErrorEnabled(false);
            }

            if (confirmPasswordData.isEmpty()) {
                textInputConfirmPassword.setError("Please enter your password");
            } else if (confirmPasswordData.length() <= 5) {
                textInputConfirmPassword.setError("Password minimum is 6 digit");
            } else {
                textInputConfirmPassword.setErrorEnabled(false);
            }

            if (!passwordData.equals(confirmPasswordData)) {
                textInputConfirmPassword.setError("The password is not matching");
            } else {
                textInputConfirmPassword.setErrorEnabled(false);
                passwordValidation = true;
            }
        }

        // Full name checking
        if (socialCommunityNameData.isEmpty()) {
            textInputSocialCommunityName.setError("Please enter your full name");
        } else {
            textInputSocialCommunityName.setErrorEnabled(false);
        }

        // Telephone number checking
        boolean telephoneNumberValidation = false;
        if (telephoneNumberData.isEmpty()) {
            textInputTelephoneNumber.setError("Please enter your telephone number");
        } else if (telephoneNumberData.length() < 7 || telephoneNumberData.length() > 13) {
            textInputTelephoneNumber.setError("Please Please input a valid telephone number");
        } else {
            telephoneNumberValidation = true;
            textInputTelephoneNumber.setErrorEnabled(false);
        }

        // Description checking
        if (socialCommunityDescriptionData.isEmpty()) {
            textInputSocialCommunityDescription.setError("Please input your description");
        } else {
            textInputSocialCommunityDescription.setErrorEnabled(false);
        }

        if (!hasImage) {
            // TODO nanti ganti errornya di image jangan TOAST!
            Toast.makeText(getApplicationContext(), "Please insert the picture of the social community", Toast.LENGTH_LONG).show();
        }

        // Register user
        if (!emailValidation && !passwordValidation && socialCommunityNameData.isEmpty() && !telephoneNumberValidation && socialCommunityDescriptionData.isEmpty()) {
            Toast.makeText(SocialCommunityRegisterActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
        } else if (emailValidation && passwordValidation && !socialCommunityNameData.isEmpty() && telephoneNumberValidation && !socialCommunityDescriptionData.isEmpty() && hasImage) {
            registerUser();
        } else {
            Toast.makeText(SocialCommunityRegisterActivity.this, "Error occurred, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser() {
        allActionStatus(false);
        mFirebaseAuth.createUserWithEmailAndPassword(emailData, passwordData).addOnCompleteListener(SocialCommunityRegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    allActionStatus(true);
                    if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                        textInputEmail.setError("Email is already used");
                        Toast.makeText(SocialCommunityRegisterActivity.this, "The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SocialCommunityRegisterActivity.this, "Sign Up is unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleUpload(bitmap);
                }
            }
        });
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("social-community").child(uuid + ".jpeg");

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
                        socialCommunityImageURI = uri.toString();
                        Log.d(TAG, socialCommunityImageURI);
                        registerToDatabase();
                    }
                });
    }

    private void updateUserName(String name) {
        final FirebaseUser user = mFirebaseAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse(socialCommunityImageURI))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "Error occurred, please try again");
                            allActionStatus(true);
                        } else {
                            Log.d(TAG, "User profile created");
                            Log.d(TAG, user.getDisplayName());
                            allActionStatus(true);
                            Intent intent = new Intent(SocialCommunityRegisterActivity.this, MainSocialCommunityActivity.class);
                            intent.putExtra(IntentNameExtra.SOCIAL_COMMUNITY_MODEL, socialCommunity);
                            Toast.makeText(SocialCommunityRegisterActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void registerToDatabase() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        socialCommunity.setName(socialCommunityNameData);
        socialCommunity.setPhone(telephoneNumberData);
        socialCommunity.setRole(Constant.SOCIAL_COMMUNITY_ROLE);
        socialCommunity.setDescription(socialCommunityDescriptionData);
        socialCommunity.setImageURI(socialCommunityImageURI);
        socialCommunity.setTotalEventCreated(0);
        socialCommunity.setNotificationAvailabilityInMillis(System.currentTimeMillis());
        socialCommunity.setUuid(uuid);
        db.collection("users").document(uuid)
                .set(socialCommunity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!\nThe Unique ID of user is : " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                        updateUserName(socialCommunityNameData);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error writing document", e);
                    }
                });
    }

    private void initializeComponents() {
        emailId = findViewById(R.id.socialCommunityRegisterEmail);
        passwordId = findViewById(R.id.socialCommunityRegisterPassword);
        confirmPasswordId = findViewById(R.id.socialCommunityRegisterConfirmPassword);
        socialCommunityNameId = findViewById(R.id.socialCommunityRegisterSocialCommunityName);
        telephoneNumberId = findViewById(R.id.socialCommunityRegisterTelephoneNumber);
        socialCommunityDescription = findViewById(R.id.socialCommunityRegisterDescription);

        textInputEmail = findViewById(R.id.socialCommunityRegisterEmailLayout);
        textInputPassword = findViewById(R.id.socialCommunityRegisterPasswordLayout);
        textInputConfirmPassword = findViewById(R.id.socialCommunityRegisterConfirmPasswordLayout);
        textInputSocialCommunityName = findViewById(R.id.socialCommunityRegisterSocialCommunityNameLayout);
        textInputTelephoneNumber = findViewById(R.id.socialCommunityRegisterTelephoneNumberLayout);
        textInputSocialCommunityDescription = findViewById(R.id.socialCommunityRegisterDescriptionLayout);

        socialCommunityPhoto = findViewById(R.id.socialCommunityRegisterImage);

        btnSignUp = findViewById(R.id.socialCommunityRegisterConfirm);
        progressBar = findViewById(R.id.socialCommunityRegisterProgressBar);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.socialCommunityRegisterEmail:
                textInputEmail.setErrorEnabled(false);
                break;
            case R.id.socialCommunityRegisterPassword:
                textInputPassword.setErrorEnabled(false);
                break;
            case R.id.socialCommunityRegisterConfirmPassword:
                textInputConfirmPassword.setErrorEnabled(false);
                break;
            case R.id.socialCommunityRegisterSocialCommunityName:
                textInputSocialCommunityName.setErrorEnabled(false);
                break;
            case R.id.socialCommunityRegisterTelephoneNumber:
                textInputTelephoneNumber.setErrorEnabled(false);
                break;
            case R.id.socialCommunityRegisterDescription:
                textInputSocialCommunityDescription.setErrorEnabled(false);
                break;
        }
    }
}
