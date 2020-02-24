package com.example.fooddonationapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fooddonationapplication.model.User;
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

public class RegisterActivity extends AppCompatActivity {

    EditText emailId, passwordId, fullNameId, telephoneNumberId;
    Button btnSignUp;
    FirebaseAuth mFirebaseAuth;
    TextInputLayout textInputEmail, textInputPassword, textInputFullName, textInputTelephoneNumber;
    ProgressBar progressBar;
    private boolean isNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.register_email);
        passwordId = findViewById(R.id.register_password); // MINIMUM 6 Characters
        btnSignUp = findViewById(R.id.register_confirm);
        fullNameId = findViewById(R.id.register_full_name);
        telephoneNumberId = findViewById(R.id.register_telephone_number);
        textInputEmail = findViewById(R.id.register_email_layout);
        textInputPassword = findViewById(R.id.register_password_layout);
        textInputFullName = findViewById(R.id.register_full_name_layout);
        textInputTelephoneNumber = findViewById(R.id.register_telephone_number_layout);
        progressBar = findViewById(R.id.progressBar);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        progressBar.setVisibility(View.INVISIBLE);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();
                final String fullName = fullNameId.getText().toString();
                final String telephoneNumber = telephoneNumberId.getText().toString();

                // Email Checking
                if (email.isEmpty()) {
//                    emailId.setError("Please enter your email");
//                    emailId.requestFocus();
                    textInputEmail.setError("Please enter your email");
                } else if(!email.contains("@") || !email.contains(".")) {
                    textInputEmail.setError("Please input a valid email");
                } else if (!email.isEmpty()){ //TODO add email checking when registration
                    textInputEmail.setErrorEnabled(false);
                }

                // Password checking
                if (password.isEmpty()) {
//                    passwordId.setError("Please enter your password");
//                    passwordId.requestFocus();
                    textInputPassword.setError("Please enter your password");
                } else if (password.length() <= 5) {
//                    passwordId.setError("Password length minimum is 6");
//                    passwordId.requestFocus();
                    textInputPassword.setError("Password must be at least 6 characters");
                } else if (!password.isEmpty()) {
                    textInputPassword.setErrorEnabled(false);
                }

                // Full name checking
                if (fullName.isEmpty()) {
//                    fullNameId.setError("Please enter your full name");
                    textInputFullName.setError("Please enter your full name");
//                    fullNameId.requestFocus();
                } else if (!fullName.isEmpty()) {
                    textInputFullName.setErrorEnabled(false);
                }

                // Telephone number checking
                if (telephoneNumber.isEmpty()) {
//                    telephoneNumberId.setError("Please enter your telephone number");
//                    telephoneNumberId.requestFocus();
                    textInputTelephoneNumber.setError("Please enter your telephone number");
                } else if (telephoneNumber.length() <= 7 && telephoneNumber.length() >= 12) {
                    textInputTelephoneNumber.setError("Please Please input a valid telephone number");
                } else if (!telephoneNumber.isEmpty()) {
                    textInputTelephoneNumber.setErrorEnabled(false);
                }

                // Register user
                if (email.isEmpty() && password.isEmpty() && fullName.isEmpty() && telephoneNumber.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
                }
                else if (!email.isEmpty() && !password.isEmpty() && !fullName.isEmpty() && !telephoneNumber.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnSignUp.setVisibility(View.INVISIBLE);
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                btnSignUp.setVisibility(View.VISIBLE);
                                Toast.makeText(RegisterActivity.this, "Sign Up is unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                            } else {
                                String uuid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
                                User user = new User(fullName, telephoneNumber, uuid);
                                db.collection("users").document(uuid)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("RegisterActivity", "DocumentSnapshot successfully written!\nThe Unique ID of user is : " + mFirebaseAuth.getInstance().getCurrentUser().getUid());
                                                updateUserName(fullName);
                                                startActivity(new Intent(RegisterActivity.this, MainMenuActivity.class));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Main", "Error writing document", e);
                                            }
                                        });

                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    btnSignUp.setVisibility(View.VISIBLE);
                    Toast.makeText(RegisterActivity.this, "Error occurred, please try again", Toast.LENGTH_SHORT).show();
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
                            Log.d("DonateActivity", "Error occurred, please try again");
                        } else {
                            Log.d("DonateActivity", "User profile created");
                            Log.d("DonateActivity", user.getDisplayName());
                        }
                    }
                });
    }
}
