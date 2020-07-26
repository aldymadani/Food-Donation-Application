package com.example.fooddonationapplication.ui.social_community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.ui.donator.MainDonatorActivity;
import com.example.fooddonationapplication.ui.donator.event.EventListFragment;
import com.example.fooddonationapplication.ui.donator.history.DonationHistoryListFragment;
import com.example.fooddonationapplication.ui.donator.profile.EditProfileFragment;
import com.example.fooddonationapplication.ui.social_community.create.CreateEventFragment;
import com.example.fooddonationapplication.ui.social_community.history.EventHistoryFragment;
import com.example.fooddonationapplication.ui.social_community.profile.SocialCommunityProfileFragment;
import com.example.fooddonationapplication.viewmodel.MainDonatorViewModel;
import com.example.fooddonationapplication.viewmodel.MainSocialCommunityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainSocialCommunityActivity extends AppCompatActivity {

    private static final String TAG = "SocialCommunityActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment CreateEventFragment = new CreateEventFragment();
    Fragment EventHistoryFragment = new EventHistoryFragment();
    Fragment SocialCommunityProfileFragment = new SocialCommunityProfileFragment();
    Fragment activeFragment;
    Fragment firstInactiveFragment;
    Fragment secondInactiveFragment;

    MainSocialCommunityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_social_community);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_social_community);
        fragmentManager.beginTransaction().add(R.id.fragment_container_social_community, SocialCommunityProfileFragment, "3").commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container_social_community, EventHistoryFragment, "2").commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container_social_community, CreateEventFragment, "1").commit();
        mViewModel = new ViewModelProvider(this).get(MainSocialCommunityViewModel.class);
        Log.d(TAG, String.valueOf(mViewModel.getLastSeen()));

        if (mViewModel.getLastSeen() <= 1) {
            activeFragment = CreateEventFragment;
            firstInactiveFragment = EventHistoryFragment;
            secondInactiveFragment = SocialCommunityProfileFragment;
            Toast.makeText(this, "Last Seen 1", Toast.LENGTH_SHORT).show();
        } else if (mViewModel.getLastSeen() == 2) {
            activeFragment = EventHistoryFragment;
            firstInactiveFragment = CreateEventFragment;
            secondInactiveFragment = SocialCommunityProfileFragment;
            Toast.makeText(this, "Last Seen 2", Toast.LENGTH_SHORT).show();
        } else {
            activeFragment = SocialCommunityProfileFragment;
            firstInactiveFragment = CreateEventFragment;
            secondInactiveFragment = EventHistoryFragment;
            Toast.makeText(this, "Last Seen 3", Toast.LENGTH_SHORT).show();
        }
        fragmentManager.beginTransaction().hide(firstInactiveFragment).hide(secondInactiveFragment).show(activeFragment).commit();

        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_create_event:
                            activeFragment = CreateEventFragment;
                            firstInactiveFragment = EventHistoryFragment;
                            secondInactiveFragment = SocialCommunityProfileFragment;
                            mViewModel.setLastSeen(1);
                            Toast.makeText(MainSocialCommunityActivity.this, "Create Event", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.nav_event_history:
                            activeFragment = EventHistoryFragment;
                            firstInactiveFragment = CreateEventFragment;
                            secondInactiveFragment = SocialCommunityProfileFragment;
                            mViewModel.setLastSeen(2);
                            Toast.makeText(MainSocialCommunityActivity.this, "Event History", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.nav_social_community_profile:
                            activeFragment = SocialCommunityProfileFragment;
                            firstInactiveFragment = CreateEventFragment;
                            secondInactiveFragment = EventHistoryFragment;
                            mViewModel.setLastSeen(3);
                            Toast.makeText(MainSocialCommunityActivity.this, "Edit Profile", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    fragmentManager.beginTransaction().hide(firstInactiveFragment).hide(secondInactiveFragment).show(activeFragment).commit();
                    return true;
                }
            };

    @Override
    protected void onPause() {
        super.onPause();
        fragmentManager.beginTransaction().hide(activeFragment).hide(firstInactiveFragment).hide(secondInactiveFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragmentManager.beginTransaction().show(activeFragment).hide(firstInactiveFragment).hide(secondInactiveFragment).commit();
    }
}
