package com.example.fooddonationapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam laoreet, odio non blandit pulvinar, magna nisl posuere metus, sed pretium ex purus vel ligula. Maecenas consequat commodo ligula eget malesuada. Mauris porttitor urna in sem finibus auctor. Sed pretium et tortor ullamcorper imperdiet. Sed nibh ligula, posuere tincidunt lorem vitae, maximus iaculis justo. Nam volutpat mollis enim, sodales elementum nibh scelerisque a. Pellentesque id erat varius, sollicitudin justo eget, varius erat. Sed convallis ultricies accumsan. Etiam condimentum venenatis neque, eget interdum nibh malesuada ut.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam laoreet, odio non blandit pulvinar, magna nisl posuere metus, sed pretium ex purus vel ligula. Maecenas consequat commodo ligula eget malesuada. Mauris porttitor urna in sem finibus auctor. Sed pretium et tortor ullamcorper imperdiet. Sed nibh ligula, posuere tincidunt lorem vitae, maximus iaculis justo. Nam volutpat mollis enim, sodales elementum nibh scelerisque a. Pellentesque id erat varius, sollicitudin justo eget, varius erat. Sed convallis ultricies accumsan. Etiam condimentum venenatis neque, eget interdum nibh malesuada ut.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam laoreet, odio non blandit pulvinar, magna nisl posuere metus, sed pretium ex purus vel ligula. Maecenas consequat commodo ligula eget malesuada. Mauris porttitor urna in sem finibus auctor. Sed pretium et tortor ullamcorper imperdiet. Sed nibh ligula, posuere tincidunt lorem vitae, maximus iaculis justo. Nam volutpat mollis enim, sodales elementum nibh scelerisque a. Pellentesque id erat varius, sollicitudin justo eget, varius erat. Sed convallis ultricies accumsan. Etiam condimentum venenatis neque, eget interdum nibh malesuada ut."
    };

    @Override
    public int getCount() {
        return slideHeadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
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
        container.removeView((ConstraintLayout) object);
    }
}
