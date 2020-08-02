package com.example.fooddonationapplication.ui.general;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donator;
import com.example.fooddonationapplication.model.SocialCommunity;
import com.example.fooddonationapplication.ui.donator.MainDonatorActivity;
import com.example.fooddonationapplication.ui.social_community.MainSocialCommunityActivity;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
//        if (user != null) {
//            // User is signed in, send to mainmenu
//            String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            checkRole(uuid);
//            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//        } else {
//            // User is signed out, send to register/login
//            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
//        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (restorePrefData()) {
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in, send to mainmenu
                        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        checkRole(uuid);
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out, send to register/login
                        Intent mainIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, IntroductionActivity.class);
                    startActivity(intent);
                    finish(); // Destroy activity A and not exist in Back stack
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPref", MODE_PRIVATE);
        return pref.getBoolean("isIntroOpened", false);
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
                            Intent mainIntent = new Intent(SplashScreenActivity.this, MainDonatorActivity.class);
                            mainIntent.putExtra(IntentNameExtra.DONATOR_MODEL, document.toObject(Donator.class));
                            startActivity(mainIntent);
                            finish();
                        } else {
                            Intent mainIntent = new Intent(SplashScreenActivity.this, MainSocialCommunityActivity.class);
                            mainIntent.putExtra(IntentNameExtra.SOCIAL_COMMUNITY_MODEL, document.toObject(SocialCommunity.class));
                            startActivity(mainIntent);
                            finish();
                        }
                        Toast.makeText(SplashScreenActivity.this, "You are logged in!", Toast.LENGTH_SHORT).show();
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
