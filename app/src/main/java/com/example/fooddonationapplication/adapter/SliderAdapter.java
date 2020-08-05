package com.example.fooddonationapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.example.fooddonationapplication.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    // Arrays
    public int[] slideImages = {
            R.drawable.introduction_homeless,
            R.drawable.introduction_nature,
            R.drawable.introduction_donate
    };

    public String[] slideHeadings = {
            "Help Poverty Group",
            "Healthier Environment",
            "Reduce Food Waste"
    };

    public String[] slideDescriptions = {
            "The amount of food wasted is incredibly huge, this is very disheartening while there are around 1 billion people go to bed hungry every night. By donating foods, you can help the poverty group to receive foods from donation.",
            "Most of the people that wasting food, do not know or aware about the negative impacts of wasting foods.  The methane gas produced by the food waste is 20-25 times stronger than the carbon dioxide. You can reduce the food waste by donating the food to the social community.",
            "Be aware of the social issue around you. Decrease your food waste by donating them to the social community through this application. This application is easy to use for donation and has many features that can be useful for donation process. Register now!"
    };

    @Override
    public int getCount() {
        return slideHeadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ScrollView) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImage = view.findViewById(R.id.slideImageView);
        TextView slideHeadingText = view.findViewById(R.id.slideHeading);
        TextView slideDescriptionText = view.findViewById(R.id.slideSubtitle);

        slideImage.setImageResource(slideImages[position]);
        slideHeadingText.setText(slideHeadings[position]);
        slideDescriptionText.setText(slideDescriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ScrollView) object);
    }
}
