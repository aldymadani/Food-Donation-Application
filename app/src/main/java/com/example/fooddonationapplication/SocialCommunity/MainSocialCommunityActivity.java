package com.example.fooddonationapplication.SocialCommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.fooddonationapplication.Donator.EditProfileInterface.EditProfileFragment;
import com.example.fooddonationapplication.Donator.EventUserInterface.EventFragment;
import com.example.fooddonationapplication.Donator.HistoryUserInterface.HistoryFragment;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.SocialCommunity.CreateEventFragment;
import com.example.fooddonationapplication.SocialCommunity.EventHistoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainSocialCommunityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_social_community);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_social_community);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.getMenu().findItem(R.id.nav_event_history).setChecked(true);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_social_community, new EventHistoryFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.nav_create_event:
                            selectedFragment = new CreateEventFragment();
                            break;
                        case R.id.nav_event_history:
                            selectedFragment = new EventHistoryFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_social_community, selectedFragment).commit();
                    return true;
                }
            };
}
