package com.ipredictfantasy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipredictfantasy.R;
import com.ipredictfantasy.utility.GlobalActivity;

public class MycompetitionActivity extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView back;
    View v1, v;
    TextView tabOne, tabtwo;
    MyPageAdapter pageAdapter;
    Typeface typeface, typefacebold, typefacebull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycompetition);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");
        typefacebold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");
        typefacebull = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");

        TextView toolbartext = (TextView) findViewById(R.id.toolbartext_back);
        toolbartext.setText(R.string.mycompetitiontitle);

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.invalidate();
        viewPager.setAdapter(pageAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MycompetitionActivity.this, MatchsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        tabOne.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tabselected));
                        tabtwo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tabunselected));
                        MycompetitionFragment.fragmentposition = 0;
                        break;
                    case 1:
                        tabOne.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tabunselected));
                        tabtwo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tabselected));
                        MycompetitionFragment.fragmentposition = 1;
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                MycompetitionFragment.fragmentposition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        toolbartext.setTypeface(typefacebold);

        setupTabIcons();
        setUpTabStrip();
    }

    public void setUpTabStrip() {
        LinearLayout mTabsLinearLayout = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
            if (i == 0) {
                tabOne.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tabselected));
                tabtwo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tabunselected));
            }
        }
    }

    private void setupTabIcons() {
        v1 = LayoutInflater.from(MycompetitionActivity.this).inflate(R.layout.custom_tab, (ViewGroup) null);
        tabOne = (TextView) v1.findViewById(R.id.tab);
        tabOne.setText(R.string.cricket);
        try {
            tabLayout.getTabAt(0).setCustomView(v1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        v = LayoutInflater.from(MycompetitionActivity.this).inflate(R.layout.custom_tab, (ViewGroup) null);
        tabtwo = (TextView) v.findViewById(R.id.tab);
        tabtwo.setText(R.string.football);
        tabLayout.getTabAt(1).setCustomView(v);

        tabOne.setTypeface(typeface);
        tabtwo.setTypeface(typeface);
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            String gameid = GlobalActivity.GameidFinder(MycompetitionFragment.fragmentposition);
            fragment = new MycompetitionFragment().newInstance(gameid);
            return fragment;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MycompetitionActivity.this, MatchsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
