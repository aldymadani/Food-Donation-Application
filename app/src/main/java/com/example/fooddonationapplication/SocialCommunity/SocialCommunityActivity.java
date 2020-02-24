package com.example.fooddonationapplication.SocialCommunity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.fooddonationapplication.LoginActivity;
import com.example.fooddonationapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class SocialCommunityActivity extends AppCompatActivity {

    Button btnLogOut;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_community);

        btnLogOut = findViewById(R.id.button2);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.getInstance().signOut();
                Intent i = new Intent(SocialCommunityActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
