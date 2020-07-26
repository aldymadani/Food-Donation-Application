package com.example.fooddonationapplication.ui.social_community.history;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.adapter.ViewPagerAdapter;
import com.example.fooddonationapplication.model.Event;
import com.google.android.material.tabs.TabLayout;

public class UpdateEventActivity extends AppCompatActivity {

    private static final String TAG = "UpdateEventActivity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    String eventID = "";
    String totalDonation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

//        Intent intent = getIntent();
//        Event event = intent.getParcelableExtra("Event");

        Log.d(TAG,eventID + "Total Donation : " +totalDonation);

        tabLayout = findViewById(R.id.updateEventTabLayout);
        viewPager = findViewById(R.id.updateEventViewPager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Adding fragment here
        adapter.addFragment(new UpdateEventFragment(), "Event Details");
        adapter.addFragment(new DonationListFragment(), "Donator");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // Adding icon
        tabLayout.getTabAt(0).setIcon(R.drawable.calendar);
        tabLayout.getTabAt(1).setIcon(R.drawable.donator_list);

        // Remove shadow from the action bar
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setElevation(0);

//        eventID = getIntent().getStringExtra("eventID");
//        totalDonation = getIntent().getStringExtra("totalDonation");
//        if (Double.parseDouble(totalDonation) <= 0) {
//            totalDonation = "0";
//        }

//        Bundle bundle = new Bundle();
//        bundle.putString("eventID", eventID);
//        bundle.putString("totalDonation", totalDonation);
//        // set donationListFragment Arguments
//        DonationListFragment donationListFragment = new DonationListFragment();
//        donationListFragment.setArguments(bundle);
    }

    public String getEventID() {
        eventID = getIntent().getStringExtra("eventID");
        return eventID;
    }

    public String getTotalDonation() {
        totalDonation = getIntent().getStringExtra("totalDonation");
        if (Double.parseDouble(totalDonation) <= 0) {
            totalDonation = "0";
        }
        return totalDonation;
    }
}
