package com.ipredictfantasy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipredictfantasy.R;
import com.ipredictfantasy.utility.GlobalActivity;
import com.wang.avi.AVLoadingIndicatorView;


/**
 * Created by intel on 5/12/2016.
 */
public class HowtoplayActivity extends Activity {
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howtoplaylayout);

        Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");

        InternetChecking internetChecking = new InternetChecking(HowtoplayActivity.this);

        Intent intent = getIntent();
        String toolbarname = intent.getStringExtra("toolbarname");
        String link = intent.getStringExtra("link");

        WebView webView = (WebView) findViewById(R.id.webview);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        TextView faqtooltext = (TextView) findViewById(R.id.faqtooltext);
        ImageView backicon = (ImageView) findViewById(R.id.backicon);

        webView.setWebViewClient(new Mybrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        if (internetChecking.isConnectingToInternet()) {
            webView.loadUrl(link);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                startAnim();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO hide your progress image
                super.onPageFinished(view, url);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopAnim();
                    }
                }, GlobalActivity.delaytime);
            }
        });


        backicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        faqtooltext.setText(toolbarname);

        faqtooltext.setTypeface(bold);
    }

    void startAnim() {
        avi.show();
    }

    void stopAnim() {
        avi.hide();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class Mybrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
