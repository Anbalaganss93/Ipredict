package com.ipredictfantasy.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipredictfantasy.R;

public class BonusPointsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus_points);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");
        Typeface typefacebold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.toobarwithback, null);
        TextView toolbartext = (TextView) findViewById(R.id.toolbartext_back);
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView sureto_buycontent = (TextView) findViewById(R.id.sure_buycontent);
        Button buy = (Button) findViewById(R.id.buy);

        Toolbar toolbar = new Toolbar(this);
        toolbartext.setText(R.string.bonuspointtitle);
        toolbartext.setTypeface(typefacebold);
        toolbar.addView(v);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BonusPointsActivity.this, MatchsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
                finish();
            }
        });

        sureto_buycontent.setTypeface(typeface);
        buy.setTypeface(typefacebold);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BonusPointsActivity.this, MatchsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
        finish();
    }
}
