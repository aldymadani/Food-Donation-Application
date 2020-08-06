package com.example.fooddonationapplication.ui.social_community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.ui.social_community.create.CreateEventFragment;
import com.example.fooddonationapplication.ui.social_community.event.list.SocialEventListFragment;
import com.example.fooddonationapplication.ui.social_community.profile.SocialCommunityProfileFragment;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.viewmodel.MainSocialCommunityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainSocialCommunityActivity extends AppCompatActivity {

    private static final String TAG = "SocialCommunityActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment CreateEventFragment = new CreateEventFragment();
    Fragment SocialEventListFragment = new SocialEventListFragment();
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Util.backToLogin(MainSocialCommunityActivity.this);
        }

        fragmentManager.beginTransaction().add(R.id.fragment_container_social_community, SocialCommunityProfileFragment, "3").commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container_social_community, SocialEventListFragment, "2").commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container_social_community, CreateEventFragment, "1").commit();
        mViewModel = new ViewModelProvider(this).get(MainSocialCommunityViewModel.class);
        Log.d(TAG, String.valueOf(mViewModel.getLastSeen()));

        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (mViewModel.getLastSeen() == 1) {
            bottomNav.setSelectedItemId(R.id.nav_create_event);
        } else if (mViewModel.getLastSeen() == 2) {
            bottomNav.setSelectedItemId(R.id.nav_event_list);
        } else if (mViewModel.getLastSeen() == 3){
            bottomNav.setSelectedItemId(R.id.nav_social_community_profile);
        } else {
            bottomNav.setSelectedItemId(R.id.nav_event_list);
        }

        FirebaseMessaging.getInstance().unsubscribeFromTopic("FoodDonation")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "UnsubscribeFromTopic To Topic FoodDonation";
                        if (!task.isSuccessful()) {
                            msg = "Failed: UnsubscribeFromTopic To Topic FoodDonation";
                        }
                        Log.d(TAG, msg);
                    }
                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_create_event:
                            activeFragment = CreateEventFragment;
                            firstInactiveFragment = SocialEventListFragment;
                            secondInactiveFragment = SocialCommunityProfileFragment;
                            mViewModel.setLastSeen(1);
                            break;
                        case R.id.nav_event_list:
                            activeFragment = SocialEventListFragment;
                            firstInactiveFragment = CreateEventFragment;
                            secondInactiveFragment = SocialCommunityProfileFragment;
                            mViewModel.setLastSeen(2);
                            break;
                        case R.id.nav_social_community_profile:
                            activeFragment = SocialCommunityProfileFragment;
                            firstInactiveFragment = CreateEventFragment;
                            secondInactiveFragment = SocialEventListFragment;
                            mViewModel.setLastSeen(3);
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
