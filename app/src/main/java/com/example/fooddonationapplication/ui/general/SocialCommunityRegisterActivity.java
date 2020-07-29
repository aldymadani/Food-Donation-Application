package com.example.fooddonationapplication.ui.general;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.example.fooddonationapplication.model.User;
import com.example.fooddonationapplication.ui.donator.MainDonatorActivity;
import com.example.fooddonationapplication.ui.social_community.MainSocialCommunityActivity;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SocialCommunityRegisterActivity extends AppCompatActivity {

    private static final String TAG = "SocialRegisterActivity";
    private static final int GalleryPick = 1;
    private String socialCommunityImageURI, emailData, passwordData, socialCommunityNameData, telephoneNumberData, socialCommunityDescriptionData;

    private EditText emailId, passwordId, socialCommunityNameId, telephoneNumberId, socialCommunityDescription;
    private Button btnSignUp;
    private FirebaseAuth mFirebaseAuth;
    private TextInputLayout textInputEmail, textInputPassword, textInputSocialCommunityName, textInputTelephoneNumber, textInputSocialCommunityDescription;
    private ProgressBar progressBar;
    private ImageView socialCommunityPhoto;
    private Bitmap bitmap;
    private boolean hasImage;

    SocialCommunityRegisterViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_community_register);

        hasImage = false;

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.socialCommunityRegisterEmail);
        passwordId = findViewById(R.id.socialCommunityRegisterPassword);
        socialCommunityNameId = findViewById(R.id.socialCommunityRegisterSocialCommunityName);
        telephoneNumberId = findViewById(R.id.socialCommunityRegisterTelephoneNumber);
        socialCommunityDescription = findViewById(R.id.socialCommunityRegisterDescription);

        textInputEmail = findViewById(R.id.socialCommunityRegisterEmailLayout);
        textInputPassword = findViewById(R.id.socialCommunityRegisterPasswordLayout);
        textInputSocialCommunityName = findViewById(R.id.socialCommunityRegisterSocialCommunityNameLayout);
        textInputTelephoneNumber = findViewById(R.id.socialCommunityRegisterTelephoneNumberLayout);
        textInputSocialCommunityDescription = findViewById(R.id.socialCommunityRegisterDescriptionLayout);

        socialCommunityPhoto = findViewById(R.id.socialCommunityRegisterImage);

        btnSignUp = findViewById(R.id.socialCommunityRegisterConfirm);
        progressBar = findViewById(R.id.socialCommunityRegisterProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        mViewModel = new ViewModelProvider(this).get(SocialCommunityRegisterViewModel.class);
        if(mViewModel.getImageBitmap() != null) {
            bitmap = mViewModel.getImageBitmap();
            socialCommunityPhoto.setImageBitmap(bitmap);
            hasImage = true;
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailData = emailId.getText().toString();
                passwordData = passwordId.getText().toString();
                socialCommunityNameData = socialCommunityNameId.getText().toString();
                telephoneNumberData = telephoneNumberId.getText().toString();
                socialCommunityDescriptionData = socialCommunityDescription.getText().toString();
                checkingAllFields();
            }
        });

        socialCommunityPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SocialCommunityRegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, GalleryPick);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // TODO mungkin batasi gunakan crop
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == Activity.RESULT_OK && data != null) {
            Uri ImageURI = data.getData();

            CropImage.activity(ImageURI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                // TODO: https://www.google.com/search?hl=en&q=createSource%20api%2028%20problem
                ImageDecoder.Source source = ImageDecoder.createSource(SocialCommunityRegisterActivity.this.getContentResolver(), resultUri);
                try {
                    bitmap = ImageDecoder.decodeBitmap(source);
                    hasImage = true;
                    Log.d(TAG, String.valueOf(bitmap));
                    socialCommunityPhoto.setImageBitmap(bitmap);
                    mViewModel.setImageBitmap(bitmap);
                    hasImage = true;
                } catch (IOException e) {
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
        if (emailData.isEmpty()) {
            textInputEmail.setError("Please input your email");
        } else if (!emailData.matches(emailPattern)) {
            textInputEmail.setError("Please input a valid email");
        } else {
            textInputEmail.setErrorEnabled(false);
        }

        // Password checking
        boolean passwordValidation = false;
        if (passwordData.isEmpty()) {
            textInputPassword.setError("Please enter your password");
        } else if (passwordData.length() <= 5) {
            textInputPassword.setError("Password must be at least 6 characters");
        } else {
            passwordValidation = true;
            textInputPassword.setErrorEnabled(false);
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
        if (emailData.isEmpty() && passwordData.isEmpty() && socialCommunityNameData.isEmpty() && !telephoneNumberValidation && socialCommunityDescriptionData.isEmpty()) { // TODO tambahin description sama gambar
            Toast.makeText(SocialCommunityRegisterActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
        } else if (!emailData.isEmpty() && passwordValidation && !socialCommunityNameData.isEmpty() && telephoneNumberValidation && !socialCommunityDescriptionData.isEmpty() && hasImage) {
//            handleUpload(bitmap);
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
                    if(task.getException().getMessage().equals("The email address is already in use by another account.")) {
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
//        handleUpload(bitmap);
//        String currentDateDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Log.v(TAG, currentDateDetail);
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
                        Log.d(TAG,socialCommunityImageURI);
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
                            Toast.makeText(SocialCommunityRegisterActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // TODO try to implement finish
                            startActivity(intent);
                        }
                    }
                });
    }

    private void registerToDatabase() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        User user = new User(socialCommunityNameData, telephoneNumberData, socialCommunityDescriptionData, uuid, "social community", socialCommunityImageURI);
//        User user = new User();
        Map<String, Object> user = new HashMap<>();
        user.put("name", socialCommunityNameData);
        user.put("phone", telephoneNumberData);
        user.put("description", socialCommunityDescriptionData);
        user.put("uuid", uuid);
        user.put("role", "social community");
        user.put("imageURI", socialCommunityImageURI);
        user.put("totalEventCreated", 0);
//        user.setName(socialCommunityNameData);
//        user.setPhone(telephoneNumberData);
//        user.setDescription(socialCommunityDescriptionData);
//        user.setUuid(uuid);
//        user.setRole("social community");
//        user.setImageURI(socialCommunityImageURI);
        db.collection("users").document(uuid)
                .set(user)
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
}
