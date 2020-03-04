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

import com.example.fooddonationapplication.Donator.MainDonatorActivity;
import com.example.fooddonationapplication.SocialCommunity.MainSocialCommunityActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    EditText emailId, passwordId;
    Button btnSignIn, btnRegister;
    FirebaseAuth mFirebaseAuth;
    ProgressBar progressBar;
    Snackbar snackbar;
    View view;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextInputLayout textInputEmail, textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.login_email);
        passwordId = findViewById(R.id.login_password);
        btnSignIn = findViewById(R.id.login_sign_in_button);
        btnRegister = findViewById(R.id.login_register_text_view);
        textInputEmail = findViewById(R.id.login_email_layout);
        textInputPassword = findViewById(R.id.login_password_layout);
        progressBar = findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        view = findViewById(R.id.login_activity);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    checkRole(uuid);
                } else {
                    Toast.makeText(LoginActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnSignIn.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_register_text_view:
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.login_sign_in_button:
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString(); // TODO needs trim?
                if(inputValidation(email, password)) {
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
            snackbar = Snackbar.make(view, "Please fill in all the information", Snackbar.LENGTH_INDEFINITE);
            snackbar.setDuration(5000);
            snackbar.show();
            Toast.makeText(LoginActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
        } else if (!email.isEmpty() && !password.isEmpty()) {
            btnSignIn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            isValid = true;
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            snackbar = Snackbar.make(view, "Error occurred, please try again", Snackbar.LENGTH_INDEFINITE);
            snackbar.setDuration(5000);
            snackbar.show();
            Toast.makeText(LoginActivity.this, "Error occurred, please try again", Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    protected  void authenticateUser(String email, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    snackbar = Snackbar.make(view, "Login error, please try again", Snackbar.LENGTH_INDEFINITE); // TODO erase or not?
                    snackbar.setDuration(5000);
                    snackbar.show();
                    Toast.makeText(LoginActivity.this, "Login error, please try again", Toast.LENGTH_SHORT).show();
                    btnSignIn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    checkRole(uuid);
                }
            }
        });
    }

    protected void checkRole(String uuid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uuid); // TODO CHANGE TO UPPERCASE FIRST LETTER
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
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainSocialCommunityActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            btnSignIn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
