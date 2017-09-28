package com.ipredictfantasy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ipredictfantasy.R;

import me.relex.circleindicator.CircleIndicator;


/**
 * Created by umm on 17-Feb-17.
 */

public class IntroScreen extends Activity implements View.OnClickListener {
    SharedPreferences preferences;
    private TextView mIntroTextView;
    private Button mGetStarted;
    static Integer[] intro = {R.drawable.intro1, R.drawable.intro2, R.drawable.intro3};
    static String[] mhelptext = {"Pick out the best players or teams around you and battle it out to see who's the best",
            "Create your own sports profile to gain popularity and know where you stand among other players.", "Wanna check out the latest highlights and updates in the world of Ontro? Just Stream through this page."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int height = metrics.heightPixels;

        Typeface typeface_regular = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");
        Typeface typefacebold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");
        preferences = getSharedPreferences("ipredict", MODE_PRIVATE);
        IntroScreenDataBaseHelper databaseHandler = new IntroScreenDataBaseHelper(this);
        databaseHandler.addStatus("success");

        mIntroTextView = (TextView) findViewById(R.id.tutorial_text);
        mGetStarted = (Button) findViewById(R.id.get_started);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        Button getstarted = (Button) findViewById(R.id.get_started);
        mGetStarted.setTypeface(typeface_regular);
       /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            getstarted.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
        } else {
            getstarted.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_normal));
        }*/

        IntroAdapter adapter = new IntroAdapter(this);

        ViewPager viewPager2 = (ViewPager) findViewById(R.id.viewPager2);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params.height = (int) (height * 0.5);
        params.topMargin = (int) (height * 0.035);
//        viewPager2.setLayoutParams(params);
        viewPager2.setAdapter(adapter);
        mIntroTextView.setText(mhelptext[0]);

        viewPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        CircleIndicator mPageIndicator = (CircleIndicator) findViewById(R.id.indicator);
        mPageIndicator.setViewPager(viewPager2);

        mPageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                int pos = arg0;
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
                // TODO Auto-generated method stub
                mIntroTextView.setText(mhelptext[position]);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });

        btn_login.setOnClickListener(this);
        getstarted.setOnClickListener(this);
        btn_login.setTypeface(typeface_regular);
        getstarted.setTypeface(typeface_regular);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_started:
                Intent intent = new Intent(getApplicationContext(), WelcomeScreen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
                finish();
                break;
        }
    }

    private class IntroAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;

        IntroAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return intro.length;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.fragment_screen_slide_page, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.slideimage);
            imageView.setImageResource(intro[position]);

            container.addView(itemView);
            return itemView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}

