package com.example.thucphamxanh2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.example.thucphamxanh2.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    int image[] = {
        R.drawable.intro1,
        R.drawable.intro2,
        R.drawable.intro4,

    };

    int headings[]={
        R.string.slide1_heading,
        R.string.slide2_heading,
        R.string.slide3_heading,
    };

    int infomation[] ={
            R.string.slide1_infor,
            R.string.slide2_infor,
            R.string.slide3_infor,
    };

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slides_layout,container,false);

        ImageView imageView = view.findViewById(R.id.slide_image);
        TextView heading = view.findViewById(R.id.slide_heading);
        TextView info = view.findViewById(R.id.slide_info);

        imageView.setImageResource(image[position]);
        heading.setText(headings[position]);
        info.setText(infomation[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);

    }
}
