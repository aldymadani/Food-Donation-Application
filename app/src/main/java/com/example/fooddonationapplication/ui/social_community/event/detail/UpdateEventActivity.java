package com.example.fooddonationapplication.ui.social_community.event.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.adapter.ViewPagerAdapter;
import com.example.fooddonationapplication.ui.social_community.event.detail.child.DonationListFragment;
import com.example.fooddonationapplication.ui.social_community.event.detail.child.UpdateEventFragment;
import com.example.fooddonationapplication.util.Util;
import com.google.android.material.tabs.TabLayout;

public class UpdateEventActivity extends AppCompatActivity {

    private static final String TAG = "UpdateEventActivity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_update);

        tabLayout = findViewById(R.id.updateEventTabLayout);
        viewPager = findViewById(R.id.updateEventViewPager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Adding fragment here
        adapter.addFragment(new UpdateEventFragment(), "Event Details");
        adapter.addFragment(new DonationListFragment(), "Donation List");

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Util.hideKeyboard(UpdateEventActivity.this);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

        // Adding icon
        tabLayout.getTabAt(0).setIcon(R.drawable.calendar);
        tabLayout.getTabAt(1).setIcon(R.drawable.donator_list);

        // Remove shadow from the action bar
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setElevation(0);
    }
}
