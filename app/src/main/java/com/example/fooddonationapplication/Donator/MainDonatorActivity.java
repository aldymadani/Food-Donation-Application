package com.example.fooddonationapplication.Donator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.fooddonationapplication.Donator.EventUserInterface.EventFragment;
import com.example.fooddonationapplication.Donator.HistoryUserInterface.DonationHistoryFragment;
import com.example.fooddonationapplication.Donator.EditProfileInterface.EditProfileFragment;
import com.example.fooddonationapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainDonatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_donator);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_donator, new EventFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.nav_event:
                            selectedFragment = new EventFragment();
                            break;
                        case R.id.nav_history:
                            selectedFragment = new DonationHistoryFragment();
                            break;
                        case R.id.nav_edit_profile:
                            selectedFragment = new EditProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_donator, selectedFragment).commit();
                    return true;
                }
            };
}
