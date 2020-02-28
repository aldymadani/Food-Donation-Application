package com.example.fooddonationapplication.Donator.HistoryUserInterface;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.fooddonationapplication.R;

public class HistoryDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        Toast.makeText(this, "History Detail Activity", Toast.LENGTH_SHORT).show();
    }
}
