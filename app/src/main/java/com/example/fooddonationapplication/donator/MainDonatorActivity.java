package com.example.fooddonationapplication.donator;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.donator.EditProfileInterface.EditProfileFragment;
import com.example.fooddonationapplication.donator.EventUserInterface.EventFragment;
import com.example.fooddonationapplication.donator.HistoryUserInterface.DonationHistoryFragment;
import com.example.fooddonationapplication.viewmodel.EventViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainDonatorActivity extends AppCompatActivity {

    private static final String TAG = "MainDonatorActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment eventFragment = new EventFragment();
    Fragment donationHistoryFragment = new DonationHistoryFragment();
    Fragment editProfileFragment = new EditProfileFragment();
    Fragment activeFragment;
    Fragment firstInactiveFragment;
    Fragment secondInactiveFragment;
    EventViewModel mViewModel;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation_donator);

        fragmentManager.beginTransaction().add(R.id.fragment_container_donator, editProfileFragment, "3").commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container_donator, donationHistoryFragment, "2").commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container_donator, eventFragment, "1").commit();
        mViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        Log.d(TAG, String.valueOf(mViewModel.getLastSeen()));

        if (mViewModel.getLastSeen() <= 1) {
            activeFragment = eventFragment;
            firstInactiveFragment = donationHistoryFragment;
            secondInactiveFragment = editProfileFragment;
            Toast.makeText(this, "Last Seen 1", Toast.LENGTH_SHORT).show();
        } else if (mViewModel.getLastSeen() == 2) {
            activeFragment = donationHistoryFragment;
            firstInactiveFragment = eventFragment;
            secondInactiveFragment = editProfileFragment;
            Toast.makeText(this, "Last Seen 2", Toast.LENGTH_SHORT).show();
        } else {
            activeFragment = editProfileFragment;
            firstInactiveFragment = donationHistoryFragment;
            secondInactiveFragment = eventFragment;
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
                        case R.id.nav_event:
                            activeFragment = eventFragment;
                            firstInactiveFragment = donationHistoryFragment;
                            secondInactiveFragment = editProfileFragment;
                            mViewModel.setLastSeen(1);
                            Toast.makeText(MainDonatorActivity.this, "Event", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.nav_history:
                            activeFragment = donationHistoryFragment;
                            firstInactiveFragment = eventFragment;
                            secondInactiveFragment = editProfileFragment;
                            mViewModel.setLastSeen(2);
                            Toast.makeText(MainDonatorActivity.this, "History", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.nav_edit_profile:
                            activeFragment = editProfileFragment;
                            firstInactiveFragment = eventFragment;
                            secondInactiveFragment = donationHistoryFragment;
                            mViewModel.setLastSeen(3);
                            Toast.makeText(MainDonatorActivity.this, "Edit", Toast.LENGTH_SHORT).show();
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
