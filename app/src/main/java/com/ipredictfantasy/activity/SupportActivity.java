package com.ipredictfantasy.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipredictfantasy.R;

public class SupportActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");
        Typeface typefacebold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");

        ImageView back = (ImageView) findViewById(R.id.back);
        TextView aboutus = (TextView) findViewById(R.id.aboutus);
        TextView privacy = (TextView) findViewById(R.id.privacy);
        TextView toolbartext_back = (TextView) findViewById(R.id.toolbartext_back);
        TextView howtoplay = (TextView) findViewById(R.id.howtoplay);
        TextView faq = (TextView) findViewById(R.id.faq);

        aboutus.setTypeface(typeface);
        privacy.setTypeface(typeface);
        howtoplay.setTypeface(typeface);
        faq.setTypeface(typeface);
        toolbartext_back.setTypeface(typefacebold);

        aboutus.setOnClickListener(this);
        privacy.setOnClickListener(this);
        faq.setOnClickListener(this);
        howtoplay.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.aboutus:
                Intent intent = new Intent(getApplicationContext(), HowtoplayActivity.class);
                intent.putExtra("toolbarname", "About us");
                intent.putExtra("link", "http://ipredictfantasy.com/about-us");
                startActivity(intent);
                break;
            case R.id.faq:
                Intent faqintent = new Intent(getApplicationContext(), HowtoplayActivity.class);
                faqintent.putExtra("toolbarname", "About us");
                faqintent.putExtra("link", "http://ipredictfantasy.com/faq");
                startActivity(faqintent);
                break;
            case R.id.privacy:
                Intent privacy = new Intent(getApplicationContext(), HowtoplayActivity.class);
                privacy.putExtra("toolbarname", "Privacy policy");
                privacy.putExtra("link", "http://ipredictfantasy.com/terms-conditions");
                startActivity(privacy);
                break;
            case R.id.howtoplay:
                Intent howtoplayintent = new Intent(getApplicationContext(), HowtoplayActivity.class);
                howtoplayintent.putExtra("toolbarname", "How to play");
                howtoplayintent.putExtra("link", "http://ipredictfantasy.com/how-to-play");
                startActivity(howtoplayintent);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
