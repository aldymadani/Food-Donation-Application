package com.example.fooddonationapplication.ui.social_community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.ui.social_community.create.CreateEventFragment;
import com.example.fooddonationapplication.ui.social_community.history.EventHistoryFragment;
import com.example.fooddonationapplication.ui.social_community.profile.SocialCommunityProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainSocialCommunityActivity extends AppCompatActivity {

    private static final String TAG = "MainSocialCommunityActivity";

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
                        case R.id.nav_social_community_profile:
                            selectedFragment = new SocialCommunityProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_social_community, selectedFragment).commit();
                    return true;
                }
            };
}