package com.example.fooddonationapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.fooddonationapplication.Donator.EventUserInterface.DonateActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainMenuActivity extends AppCompatActivity {

    Button btnLogOut, btnAddData, btnToDonateUI;
    FirebaseAuth mFirebaseAuth;
    String docRef = "";
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
//        getIncomingIntent();

        btnLogOut = findViewById(R.id.main_menu_log_out_button);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = mFirebaseAuth.getInstance().getCurrentUser();
//                Log.d("DonateActivity", user.getDisplayName()); //TODO don't forget add user name when registration
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btnAddData = findViewById(R.id.button);
        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> user = new HashMap<>();
                user.put("first", "Ada");
                user.put("last", "Lovelace");
                user.put("born", 1815);
                user.put("uid", mFirebaseAuth.getInstance().getCurrentUser().getUid());

                // Add a new document with a generated ID
                db.collection("events")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("DonateActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
                                docRef = documentReference.getId();
                                Log.d("Main Activity", mFirebaseAuth.getInstance().getCurrentUser().getUid());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("DonateActivity", "Error adding document", e);
                            }
                        });

                DocumentReference newCityRef = db.collection("smtg").document();
                String smtg = db.collection("smtg").document().getId();
                user.put("docId", newCityRef);

                Log.d("Main", docRef);
                Log.d("Main", "Get ID: " + smtg);

                user.put("anotherDocId", smtg);
//                newCityRef.set(user);
                db.collection("cities").document(smtg)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Main", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Main", "Error writing document", e);
                            }
                        });
            }
        });

        btnToDonateUI = findViewById(R.id.button3);
        btnToDonateUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, DonateActivity.class);
                startActivity(i);
            }
        });
    }

    private void getIncomingIntent() {
//        Log.d("MainMenuActivity", getIntent().getStringExtra("eventID"));
    }
}
