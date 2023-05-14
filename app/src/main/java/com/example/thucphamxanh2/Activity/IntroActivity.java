package com.example.thucphamxanh2.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thucphamxanh2.Adapter.SliderAdapter;
import com.example.thucphamxanh2.R;

public class IntroActivity extends AppCompatActivity {

    ViewPager viewPagerIntro;
    LinearLayout dotsLayoutIntro;
    SliderAdapter sliderAdapterIntro;
    TextView[] dots;

    Button btn_started_intro;
    int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        viewPagerIntro = findViewById(R.id.slider_intro);
        dotsLayoutIntro = findViewById(R.id.dots_intro);
        btn_started_intro = findViewById(R.id.btn_started_intro);

        sliderAdapterIntro = new SliderAdapter(this);
        viewPagerIntro.setAdapter(sliderAdapterIntro);
        addDots(0);
        viewPagerIntro.addOnPageChangeListener(changeListener);

        btn_started_intro.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });

        getSupportActionBar().hide(); // Ẩn actionbar
    }

    public void skip(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void next(View view){
        viewPagerIntro.setCurrentItem(currentPos + 1);
    }

    private void addDots(int position){
        dots = new TextView[3];
        dotsLayoutIntro.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);

            dotsLayoutIntro.addView(dots[i]);
        }

        if (dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.black));
        }
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;

            if (position == 0){
                btn_started_intro.setVisibility(View.INVISIBLE);
            }else if (position == 1){
                btn_started_intro.setVisibility(View.INVISIBLE);
            }else{
                btn_started_intro.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}