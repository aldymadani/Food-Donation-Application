package com.example.fooddonationapplication.ui.general;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donator;
import com.example.fooddonationapplication.model.SocialCommunity;
import com.example.fooddonationapplication.ui.donator.MainDonatorActivity;
import com.example.fooddonationapplication.ui.social_community.MainSocialCommunityActivity;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private EditText emailField, passwordField;
    private Button btnSubmit, btnRegister, donatorButton, socialCommunityButton;
    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;
    private TextView loginTitle;
    private TextInputLayout textInputEmail, textInputPassword;

    private boolean isReauth = false;
    private String userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isReauth = getIntent().getBooleanExtra(IntentNameExtra.IS_REAUTH, false);
        userEmail = getIntent().getStringExtra(IntentNameExtra.USER_EMAIL);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.login_email);
        passwordField = findViewById(R.id.login_password);
        loginTitle = findViewById(R.id.loginTitle);
        btnSubmit = findViewById(R.id.loginSubmitButton);
        btnRegister = findViewById(R.id.loginRegisterButton);
        textInputEmail = findViewById(R.id.login_email_layout);
        textInputPassword = findViewById(R.id.login_password_layout);
        progressBar = findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        if (isReauth) {
            loginTitle.setText("Re-authentication");
            btnSubmit.setText("Re-authenticate");
            Util.hide(btnRegister);
            emailField.setText(userEmail);
            emailField.setFocusable(false);
            passwordField.requestFocus();
            Util.showKeyboard(this);
        }

        textInputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputEmail.setErrorEnabled(false);
            }
        });

        textInputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputPassword.setErrorEnabled(false);
            }
        });

        btnSubmit.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void reauthResult(boolean isSuccess) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(IntentNameExtra.REAUTH_RESULT, isSuccess);
        if (isSuccess) {
            setResult(RESULT_OK, returnIntent);
        } else {
            setResult(RESULT_CANCELED, returnIntent);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginRegisterButton:
                Util.hideKeyboard(LoginActivity.this);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                View registerDialog = getLayoutInflater().inflate(R.layout.dialog_option, null);
                mBuilder.setView(registerDialog);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                donatorButton = registerDialog.findViewById(R.id.registerDialogDonatorButton);
                socialCommunityButton = registerDialog.findViewById(R.id.registerDialogSocialCommunityButton);
                donatorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LoginActivity.this, DonatorRegisterActivity.class);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });
                socialCommunityButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LoginActivity.this, SocialCommunityRegisterActivity.class);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.loginSubmitButton:
                btnRegister.setEnabled(false);
                Util.hideKeyboard(LoginActivity.this);
                emailField.clearFocus();
                passwordField.clearFocus();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                if (inputValidation(email, password)) {
                    authenticateUser(email, password);
                }
                break;
        }
    }

    protected boolean inputValidation(String email, String password) {
        boolean isValid = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Email checking
        if (email.isEmpty()) {
            textInputEmail.setError("Please enter your email");
        } else if (!email.matches(emailPattern)) {
            textInputEmail.setError("Please input a valid email");
        } else {
            textInputEmail.setErrorEnabled(false);
        }

        // Password checking
        if (password.isEmpty()) {
            textInputPassword.setError("Please enter your password");
        } else {
            textInputPassword.setErrorEnabled(false);
        }

        // User authentication
        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
        } else if (!email.isEmpty() && !password.isEmpty()) {
            btnSubmit.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
            isValid = true;
        } else {
            btnSubmit.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, "Error occurred, please try again", Toast.LENGTH_SHORT).show();
        }

        // TODO To change focus to title so error text wouldn't be gone (NEED RECHECK IF IT'S NECESSARY)
        // loginTitle.requestFocus();
        return isValid;
    }

    protected void authenticateUser(String email, String password) {
        if (!isReauth) {
            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            textInputEmail.setError("The account is not registered yet");
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            textInputPassword.setError("The password doesn't match the email address");
                        } else {
                            Toast.makeText(LoginActivity.this, "Login error, please try again", Toast.LENGTH_SHORT).show();
                        }
                        btnSubmit.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        btnRegister.setEnabled(true);
                    } else {
                        textInputPassword.setErrorEnabled(false);
                        String uuid = mFirebaseAuth.getCurrentUser().getUid();
                        checkRole(uuid);
                    }
                }
            });
        } else {
            // https://firebase.google.com/docs/auth/android/manage-users#re-authenticate_a_user
            FirebaseUser user = mFirebaseAuth.getCurrentUser();

            // Get auth credentials from the user for re-authentication. The example below shows
            // email and password credentials but there are multiple possible providers,
            // such as GoogleAuthProvider or FacebookAuthProvider.
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        reauthResult(true);
                    } else {
                        btnSubmit.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            textInputPassword.setError("The password doesn't match the email address");
                        } else {
                            reauthResult(false);
                        }
                    }
                }
            });
        }
    }

    protected void checkRole(String uuid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uuid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if (document.getData().containsValue("donator")) {
                            Intent intent = new Intent(LoginActivity.this, MainDonatorActivity.class);
                            intent.putExtra(IntentNameExtra.DONATOR_MODEL, document.toObject(Donator.class));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            btnSubmit.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            btnRegister.setEnabled(true);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainSocialCommunityActivity.class);
                            intent.putExtra(IntentNameExtra.SOCIAL_COMMUNITY_MODEL, document.toObject(SocialCommunity.class));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            btnSubmit.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            btnRegister.setEnabled(true);
                        }
                        Toast.makeText(LoginActivity.this, "You are logged in!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
