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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.ui.donator.MainDonatorActivity;
import com.example.fooddonationapplication.ui.social_community.MainSocialCommunityActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private EditText emailId, passwordId;
    private Button btnSignIn, btnRegister, donatorButton, socialCommunityButton;
    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;
    View view;
    private TextInputLayout textInputEmail, textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO implement double click back to exit application (currently only single click)

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.login_email);
        passwordId = findViewById(R.id.login_password);
        btnSignIn = findViewById(R.id.login_sign_in_button);
        btnRegister = findViewById(R.id.loginRegisterButton);
        textInputEmail = findViewById(R.id.login_email_layout);
        textInputPassword = findViewById(R.id.login_password_layout);
        progressBar = findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        view = findViewById(R.id.login_activity);

        btnSignIn.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginRegisterButton:
                btnSignIn.setEnabled(false);
                hideKeyboard(LoginActivity.this);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                View registerDialog = getLayoutInflater().inflate(R.layout.dialog_register, null);
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
                        dialog.hide();
                        btnSignIn.setEnabled(true);
                    }
                });
                socialCommunityButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LoginActivity.this, SocialCommunityRegisterActivity.class);
                        startActivity(i);
                        dialog.hide();
                        btnSignIn.setEnabled(true);
                    }
                });
                break;
            case R.id.login_sign_in_button:
                btnRegister.setEnabled(false);
                hideKeyboard(LoginActivity.this);
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString(); // TODO needs trim?
                if (inputValidation(email, password)) {
                    passwordId.clearFocus();
                    emailId.clearFocus();
                    authenticateUser(email, password);
                }
                break;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            btnSignIn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
            isValid = true;
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, "Error occurred, please try again", Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    protected  void authenticateUser(String email, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        textInputEmail.setError("The account is not registered yet");
                        Toast.makeText(LoginActivity.this, "The account is not registered yet", Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        textInputPassword.setError("The password doesn't match the email address");
                        Toast.makeText(LoginActivity.this, "The password doesn't match the email address", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login error, please try again", Toast.LENGTH_SHORT).show();
                    }
                    btnSignIn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    btnRegister.setEnabled(true);
                } else {
                    textInputEmail.setErrorEnabled(false);
                    textInputPassword.setErrorEnabled(false);
                    String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    checkRole(uuid);
                }
            }
        });
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
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            btnSignIn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            btnRegister.setEnabled(true);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainSocialCommunityActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            btnSignIn.setVisibility(View.VISIBLE);
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
