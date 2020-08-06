package com.example.fooddonationapplication.ui.social_community.event.list;

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
import com.example.fooddonationapplication.ui.social_community.event.list.child.ChildEventListFragment;
import com.example.fooddonationapplication.util.constant.Constant;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.google.android.material.tabs.TabLayout;

public class SocialEventListFragment extends Fragment {

    private ViewPagerAdapter eventAdapter;
    private FragmentActivity fragmentActivity;
    private ViewPager mViewPager;
    private TabLayout mTabs;
    private ChildEventListFragment currentEvent;
    private ChildEventListFragment pastEvent;
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
            currentEvent = new ChildEventListFragment();
            Bundle currentBundle = new Bundle();
            currentBundle.putString(IntentNameExtra.EVENT_LIST_ARGUMENT, Constant.CURRENT_EVENT);
            currentEvent.setArguments(currentBundle);

            pastEvent = new ChildEventListFragment();
            Bundle pastBundle = new Bundle();
            pastBundle.putString(IntentNameExtra.EVENT_LIST_ARGUMENT, Constant.PAST_EVENT);
            pastEvent.setArguments(pastBundle);

            eventAdapter.addFragment(currentEvent, "Current");
            eventAdapter.addFragment(pastEvent, "Past");

            mViewPager.setAdapter(eventAdapter);
            mViewPager.setCurrentItem(0);

            mTabs.setupWithViewPager(mViewPager);
        }
    }
}

