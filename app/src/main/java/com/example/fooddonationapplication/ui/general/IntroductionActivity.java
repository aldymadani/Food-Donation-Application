package com.example.fooddonationapplication.ui.general;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.adapter.SliderAdapter;

public class IntroductionActivity extends AppCompatActivity {

    private LinearLayout dotsLayout;
    private ViewPager slideViewPager;
    private SliderAdapter sliderAdapter;
    private Button backButton, nextButton;
    private int currentPage;

    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        slideViewPager = findViewById(R.id.introductionViewPager);
        backButton = findViewById(R.id.slideBackButton);
        nextButton = findViewById(R.id.slideNextButton);
        dotsLayout = findViewById(R.id.slideDots);
        final String registerAs = getIntent().getStringExtra("registerAs");

        sliderAdapter = new SliderAdapter(this);
        slideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        slideViewPager.addOnPageChangeListener(viewListener);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideViewPager.getCurrentItem() == 2) {
                    if (registerAs.equalsIgnoreCase("donator")) {
                        Intent i = new Intent(IntroductionActivity.this, DonatorRegisterActivity.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(IntroductionActivity.this, SocialCommunityRegisterActivity.class);
                        startActivity(i);
                    }
                    Toast.makeText(IntroductionActivity.this, "GO TO REGISTER " + registerAs + " ACTIVITY", Toast.LENGTH_SHORT).show();
                }
                slideViewPager.setCurrentItem(currentPage + 1);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideViewPager.setCurrentItem(currentPage - 1);
            }
        });
    }

    public void addDotsIndicator(int position) {
        dots = new TextView[3];

        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getColor(R.color.colorTransparentWhite));

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getColor(R.color.gradient_start_color));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage = position;
            if (position == 0) {
                nextButton.setEnabled(true);
                backButton.setEnabled(false);
                backButton.setVisibility(View.INVISIBLE);

                nextButton.setText("NEXT");
                backButton.setText("");
            } else if (position == 2) {
                nextButton.setEnabled(true);
                backButton.setEnabled(true);
                backButton.setVisibility(View.VISIBLE);

                nextButton.setText("FINISH");
                backButton.setText("BACK");
            } else {
                nextButton.setEnabled(true);
                backButton.setEnabled(true);
                backButton.setVisibility(View.VISIBLE);

                nextButton.setText("NEXT");
                backButton.setText("BACK");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
