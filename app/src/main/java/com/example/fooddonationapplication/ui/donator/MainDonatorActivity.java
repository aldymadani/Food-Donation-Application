package com.example.fooddonationapplication.ui.donator;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.model.Donator;
import com.example.fooddonationapplication.ui.donator.profile.DonatorProfileFragment;
import com.example.fooddonationapplication.ui.donator.event.EventListFragment;
import com.example.fooddonationapplication.ui.donator.history.DonationHistoryListFragment;
import com.example.fooddonationapplication.ui.social_community.MainSocialCommunityActivity;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.example.fooddonationapplication.viewmodel.MainDonatorViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Util.backToLogin(MainDonatorActivity.this);
        }

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
        FirebaseMessaging.getInstance().subscribeToTopic("FoodDonation")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "SubscribeToTopic To Topic FoodDonation";
                        if (!task.isSuccessful()) {
                            msg = "Failed: SubscribeToTopic To Topic FoodDonation";
                        }
                        Log.d(TAG, msg);
//                        Toast.makeText(MainSocialCommunityActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        final String userUuid = user.getUid();
        FirebaseMessaging.getInstance().subscribeToTopic(userUuid)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "SubscribeToTopic To " + userUuid;
                        if (!task.isSuccessful()) {
                            msg = "Failed: SubscribeToTopic To Topic FoodDonation";
                        }
                        Log.d(TAG, msg);
//                        Toast.makeText(MainSocialCommunityActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        System.out.println(formatter.format(calendar.getTime()));
//        Log.d("CEK", formatter.format(calendar.getTime()));
//        Toast.makeText(this, formatter.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
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
