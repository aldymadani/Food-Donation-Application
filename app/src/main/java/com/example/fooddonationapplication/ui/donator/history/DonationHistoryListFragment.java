package com.example.fooddonationapplication.ui.donator.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.adapter.ViewPagerAdapter;
import com.example.fooddonationapplication.ui.donator.history.child.ChildDonationHistoryListFragment;
import com.example.fooddonationapplication.util.constant.Constant;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.google.android.material.tabs.TabLayout;

public class DonationHistoryListFragment extends Fragment {

    private ViewPagerAdapter eventAdapter;
    private FragmentActivity fragmentActivity;
    private ViewPager mViewPager;
    private TabLayout mTabs;
    private ChildDonationHistoryListFragment onProgressDonation;
    private ChildDonationHistoryListFragment completedDonation;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_list_social_community, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentActivity = requireActivity();
        mViewPager = rootView.findViewById(R.id.listEventViewPager);
        mTabs = rootView.findViewById(R.id.listEventTabLayout);
        eventAdapter = new ViewPagerAdapter(getChildFragmentManager());

        setDefaultFragment();
    }

    private void setDefaultFragment() {
        if (this.isAdded()) {
            onProgressDonation = new ChildDonationHistoryListFragment();
            Bundle onProgressBundle = new Bundle();
            onProgressBundle.putString(IntentNameExtra.DONATION_LIST_ARGUMENT, Constant.ON_PROGRESS_DONATIONS);
            onProgressDonation.setArguments(onProgressBundle);

            completedDonation = new ChildDonationHistoryListFragment();
            Bundle completedBundle = new Bundle();
            completedBundle.putString(IntentNameExtra.DONATION_LIST_ARGUMENT, Constant.COMPLETED_DONATIONS);
            completedDonation.setArguments(completedBundle);

            eventAdapter.addFragment(onProgressDonation, "On-Progress");
            eventAdapter.addFragment(completedDonation, "Completed");

            mViewPager.setAdapter(eventAdapter);
            mViewPager.setCurrentItem(0);

            mTabs.setupWithViewPager(mViewPager);
        }
    }
}

