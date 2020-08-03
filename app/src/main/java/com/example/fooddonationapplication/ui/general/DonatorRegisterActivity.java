package com.example.fooddonationapplication.ui.general;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donator;
import com.example.fooddonationapplication.ui.donator.MainDonatorActivity;
import com.example.fooddonationapplication.util.constant.Constant;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DonatorRegisterActivity extends AppCompatActivity {
    private static final String TAG = "DonatorRegisterActivity";

    private TextInputLayout textInputEmail, textInputPassword, textInputConfirmPassword, textInputFullName, textInputTelephoneNumber;
    private EditText emailId, passwordId, confirmPasswordId, fullNameId, telephoneNumberId;
    private Button btnSignUp;
    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;
    private Donator donator = new Donator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_donator);
        initializeComponents();

        mFirebaseAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.INVISIBLE);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();
                String confirmPassword = confirmPasswordId.getText().toString();
                String fullName = fullNameId.getText().toString();
                String telephoneNumber = telephoneNumberId.getText().toString();
                allFieldValidation(email, password, confirmPassword, fullName, telephoneNumber);
            }
        });
    }

    protected void allFieldValidation(String email, String password, String confirmPassword, String fullName, String telephoneNumber) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Email checking
        boolean emailValidation = false;
        if (email.isEmpty()) {
            textInputEmail.setError("Please input your email");
        } else if (!email.matches(emailPattern)) {
            textInputEmail.setError("Please input a valid email");
        } else {
            emailValidation = true;
            textInputEmail.setErrorEnabled(false);
        }

        // Password checking
        boolean passwordValidation = false;
        if (password.isEmpty() && confirmPassword.isEmpty()) {
            textInputPassword.setError("Please enter your password");
            textInputConfirmPassword.setError("Please enter your password");
        } else {
            if (password.isEmpty()) {
                textInputPassword.setError("Please enter your password");
            } else if (password.length() <= 5) {
                textInputPassword.setError("Password minimum is 6 digit");
            } else {
                textInputPassword.setErrorEnabled(false);
            }

            if (confirmPassword.isEmpty()) {
                textInputConfirmPassword.setError("Please enter your password");
            } else if (confirmPassword.length() <= 5) {
                textInputConfirmPassword.setError("Password minimum is 6 digit");
            } else {
                textInputConfirmPassword.setErrorEnabled(false);
            }

            if (!password.equals(confirmPassword)) {
                textInputConfirmPassword.setError("The password is not matching");
            } else {
                textInputConfirmPassword.setErrorEnabled(false);
                passwordValidation = true;
            }
        }

        // Full name checking
        if (fullName.isEmpty()) {
            textInputFullName.setError("Please enter your full name");
        } else {
            textInputFullName.setErrorEnabled(false);
        }

        // Telephone number checking
        boolean telephoneNumberValidation = false;
        if (telephoneNumber.isEmpty()) {
            textInputTelephoneNumber.setError("Please enter your telephone number");
        } else if (telephoneNumber.length() < 7 || telephoneNumber.length() > 13) {
            textInputTelephoneNumber.setError("Please Please input a valid telephone number");
        } else {
            telephoneNumberValidation = true;
            textInputTelephoneNumber.setErrorEnabled(false);
        }

        // Register user
        if (!emailValidation && !passwordValidation && fullName.isEmpty() && telephoneNumberValidation) {
            Toast.makeText(DonatorRegisterActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
        } else if (!emailValidation && passwordValidation && !fullName.isEmpty() && telephoneNumberValidation) {
            registerUser(email, password, fullName, telephoneNumber);
        } else {
            Toast.makeText(DonatorRegisterActivity.this, "Error occurred, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    protected void registerUser(String email, String password, final String fullName, final String telephoneNumber) {
        allActionStatus(false);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(DonatorRegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    allActionStatus(true);
                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        textInputEmail.setError("Email is already used");
                        Toast.makeText(DonatorRegisterActivity.this, "The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DonatorRegisterActivity.this, "Sign Up is unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    donator.setName(fullName);
                    donator.setPhone(telephoneNumber);
                    donator.setUuid(uuid);
                    donator.setRole(Constant.DONATOR_ROLE);
                    donator.setTotalDonation(0);
                    db.collection("users").document(uuid)
                            .set(donator)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!\nThe Unique ID of user is : " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    updateUserName(fullName);
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
        });
    }

    protected void updateUserName(String name) {
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
                            allActionStatus(true);
                        } else {
                            Log.d(TAG, "User profile created");
                            Log.d(TAG, user.getDisplayName());
                            allActionStatus(true);
                            Intent intent = new Intent(DonatorRegisterActivity.this, MainDonatorActivity.class);
                            intent.putExtra(IntentNameExtra.DONATOR_MODEL, donator);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Toast.makeText(DonatorRegisterActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
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
        fullNameId.setFocusable(status);
        fullNameId.setFocusableInTouchMode(status);
        fullNameId.setCursorVisible(status);
        telephoneNumberId.setFocusable(status);
        telephoneNumberId.setFocusableInTouchMode(status);
        telephoneNumberId.setCursorVisible(status);
        if (status) {
            btnSignUp.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            btnSignUp.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void initializeComponents() {
        emailId = findViewById(R.id.register_email);
        passwordId = findViewById(R.id.register_password);
        confirmPasswordId = findViewById(R.id.register_confirm_password);
        btnSignUp = findViewById(R.id.register_confirm);
        fullNameId = findViewById(R.id.register_full_name);
        telephoneNumberId = findViewById(R.id.register_telephone_number);
        textInputEmail = findViewById(R.id.register_email_layout);
        textInputPassword = findViewById(R.id.register_password_layout);
        textInputConfirmPassword = findViewById(R.id.register_confirm_password_layout);
        textInputFullName = findViewById(R.id.register_full_name_layout);
        textInputTelephoneNumber = findViewById(R.id.register_telephone_number_layout);
        progressBar = findViewById(R.id.progressBar);
    }
}
