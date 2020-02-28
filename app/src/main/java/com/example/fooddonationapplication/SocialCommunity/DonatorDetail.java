package com.example.fooddonationapplication.SocialCommunity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.fooddonationapplication.R;

public class DonatorDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donator_detail);
        Toast.makeText(this, "This is donator detail interface", Toast.LENGTH_SHORT).show();
    }
}
