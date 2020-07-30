package com.example.fooddonationapplication.ui.donator;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.ui.donator.profile.DonatorProfileFragment;
import com.example.fooddonationapplication.ui.donator.event.EventListFragment;
import com.example.fooddonationapplication.ui.donator.history.DonationHistoryListFragment;
import com.example.fooddonationapplication.viewmodel.MainDonatorViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainDonatorActivity extends AppCompatActivity {

    private static final String TAG = "MainDonatorActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment eventFragment = new EventListFragment();
    Fragment donationHistoryFragment = new DonationHistoryListFragment();
    Fragment editProfileFragment = new DonatorProfileFragment();
    Fragment activeFragment;
    Fragment firstInactiveFragment;
    Fragment secondInactiveFragment;
    MainDonatorViewModel mViewModel;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_donator);

        bottomNav = findViewById(R.id.bottom_navigation_donator);

        fragmentManager.beginTransaction().add(R.id.fragment_container_donator, editProfileFragment, "3").commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container_donator, donationHistoryFragment, "2").commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container_donator, eventFragment, "1").commit();
        mViewModel = new ViewModelProvider(this).get(MainDonatorViewModel.class);
        Log.d(TAG, String.valueOf(mViewModel.getLastSeen()));

        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (mViewModel.getLastSeen() == 1) {
            bottomNav.setSelectedItemId(R.id.nav_event);
        } else if (mViewModel.getLastSeen() == 2) {
            bottomNav.setSelectedItemId(R.id.nav_history);
        } else if (mViewModel.getLastSeen() == 3) {
            bottomNav.setSelectedItemId(R.id.nav_edit_profile);
        } else {
            bottomNav.setSelectedItemId(R.id.nav_event);
        }
        // fragmentManager.beginTransaction().hide(firstInactiveFragment).hide(secondInactiveFragment).show(activeFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_event:
                        activeFragment = eventFragment;
                        firstInactiveFragment = donationHistoryFragment;
                        secondInactiveFragment = editProfileFragment;
                        mViewModel.setLastSeen(1);
                        break;
                    case R.id.nav_history:
                        activeFragment = donationHistoryFragment;
                        firstInactiveFragment = eventFragment;
                        secondInactiveFragment = editProfileFragment;
                        mViewModel.setLastSeen(2);
                        break;
                    case R.id.nav_edit_profile:
                        activeFragment = editProfileFragment;
                        firstInactiveFragment = eventFragment;
                        secondInactiveFragment = donationHistoryFragment;
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
