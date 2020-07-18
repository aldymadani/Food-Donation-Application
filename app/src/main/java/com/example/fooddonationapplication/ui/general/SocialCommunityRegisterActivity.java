package com.example.fooddonationapplication.ui.general;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SocialCommunityRegisterActivity extends AppCompatActivity {

    private static final String TAG = "SocialRegisterActivity";
    private static final int TAKE_IMAGE_CODE = 1;
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
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SocialCommunityRegisterActivity.this, new String[] {Manifest.permission.CAMERA}, TAKE_IMAGE_CODE);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, TAKE_IMAGE_CODE);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TAKE_IMAGE_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // TODO mungkin batasi gunakan crop
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    socialCommunityPhoto.setImageBitmap(bitmap);
                    mViewModel.setImageBitmap(bitmap);
                    hasImage = true;
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
        btnSignUp.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mFirebaseAuth.createUserWithEmailAndPassword(emailData, passwordData).addOnCompleteListener(SocialCommunityRegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    btnSignUp.setVisibility(View.VISIBLE);
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
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "Error occurred, please try again");
                        } else {
                            Log.d(TAG, "User profile created");
                            Log.d(TAG, user.getDisplayName());
                            Intent intent = new Intent(SocialCommunityRegisterActivity.this, MainSocialCommunityActivity.class);
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
        User user = new User(socialCommunityNameData, telephoneNumberData, socialCommunityDescriptionData, uuid, "social community", socialCommunityImageURI);
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
