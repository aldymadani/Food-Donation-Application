package com.example.fooddonationapplication;

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

import com.example.fooddonationapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    EditText emailId, passwordId, fullNameId, telephoneNumberId;
    Button btnSignUp;
    FirebaseAuth mFirebaseAuth;
    TextInputLayout textInputEmail, textInputPassword, textInputFullName, textInputTelephoneNumber;
    ProgressBar progressBar;
    private boolean isNewUser;
    private boolean emailUsed;

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
        progressBar.setVisibility(View.INVISIBLE);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();
                String fullName = fullNameId.getText().toString();
                String telephoneNumber = telephoneNumberId.getText().toString();
                allFieldValidation(email, password, fullName, telephoneNumber);
            }
        });
    }

    protected void allFieldValidation(String email, String password, String fullName, String telephoneNumber) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Email checking
        if (email.isEmpty()) {
            textInputEmail.setError("Please input your email");
        } else if (!email.matches(emailPattern)) {
            textInputEmail.setError("Please input a valid email");
        } else if (!email.isEmpty()) {
            textInputEmail.setErrorEnabled(false);
        }

        // Password checking
        if (password.isEmpty()) {
            textInputPassword.setError("Please enter your password");
        } else if (password.length() <= 5) {
            textInputPassword.setError("Password must be at least 6 characters");
        } else if (!password.isEmpty()) {
            textInputPassword.setErrorEnabled(false);
        }

        // Full name checking
        if (fullName.isEmpty()) {
            textInputFullName.setError("Please enter your full name");
        } else if (!fullName.isEmpty()) {
            textInputFullName.setErrorEnabled(false);
        }

        // Telephone number checking
        if (telephoneNumber.isEmpty()) {
            textInputTelephoneNumber.setError("Please enter your telephone number");
        } else if (telephoneNumber.length() <= 7 && telephoneNumber.length() >= 12) {
            textInputTelephoneNumber.setError("Please Please input a valid telephone number");
        } else if (!telephoneNumber.isEmpty()) {
            textInputTelephoneNumber.setErrorEnabled(false);
        }

        // Register user
        if (email.isEmpty() && password.isEmpty() && fullName.isEmpty() && telephoneNumber.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
        } else if (!email.isEmpty() && !password.isEmpty() && !fullName.isEmpty() && !telephoneNumber.isEmpty()) {
            registerUser(email, password, fullName, telephoneNumber);
        } else {
            Toast.makeText(RegisterActivity.this, "Error occurred, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    protected void registerUser(String email, String password, final String fullName, final String telephoneNumber) {
        btnSignUp.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    btnSignUp.setVisibility(View.VISIBLE);
                    if(task.getException().getMessage().equals("The email address is already in use by another account.")) {
                        textInputEmail.setError("Email is already used");
                        Toast.makeText(RegisterActivity.this, "The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Sign Up is unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    User user = new User(fullName, telephoneNumber, uuid);
                    db.collection("Users").document(uuid)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("RegisterActivity", "DocumentSnapshot successfully written!\nThe Unique ID of user is : " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    updateUserName(fullName);
                                    Toast.makeText(RegisterActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
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
