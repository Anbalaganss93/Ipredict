package com.ipredictfantasy.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipredictfantasy.asynctasks.EmailLoginTask;
import com.ipredictfantasy.R;
import com.ipredictfantasy.utility.GlobalActivity;
import com.ipredictfantasy.utility.Util;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ForgotpasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText forgetmailaddress;
    private TextView title, content;
    private Button submit;
    private Typeface typeface, typefacebold;
    private String email_data, mForgetmailaddress;
    private InternetChecking internetChecking;
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        internetChecking = new InternetChecking(ForgotpasswordActivity.this);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");
        typefacebold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");

        forgetmailaddress = (EditText) findViewById(R.id.forgetmailaddress);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        title = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
        submit = (Button) findViewById(R.id.submit);
        ImageView forgetback = (ImageView) findViewById(R.id.forgetback);

        forgetback.setOnClickListener(this);
        submit.setOnClickListener(this);

        setfontstyle();
    }

    public void setfontstyle() {
        title.setTypeface(typefacebold);
        forgetmailaddress.setTypeface(typeface);
        content.setTypeface(typeface);
        submit.setTypeface(typefacebold);
    }

    void startAnim() {
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim() {
        avi.hide();
        // or avi.smoothToHide();
    }

    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgetback:
                finish();
                break;
            case R.id.submit:
                mForgetmailaddress = forgetmailaddress.getText().toString().trim();
                if (validation()) {
                    if (internetChecking.isConnectingToInternet()) {
                        startAnim();
                        forgetpasswordrecoverycall(mForgetmailaddress);
                    }
                }
                break;
        }
    }

    private void forgetpasswordrecoverycall(String emailaddress) {
        try {
            JSONObject regJsonObject = new JSONObject();
            regJsonObject.put("email", emailaddress);
            email_data = regJsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!Util.isNetworkAvailable(this)) {
            Toast.makeText(getApplicationContext(), "Connect to internet and try again", Toast.LENGTH_SHORT).show();
        } else {
            EmailLoginTask mforget = new EmailLoginTask(DomainUrl.forgetpassword, email_data);
            if (!email_data.isEmpty()) {
                try {
                    String response = mforget.execute().get();
                    Log.d("Forgetpass", response);
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        Toast.makeText(getApplicationContext(), "Password send to your email address please check", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(getApplicationContext(), MatchsActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else if (result.equalsIgnoreCase("error")) {
                        String msg = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopAnim();
                }
            }, GlobalActivity.delaytime);
        }
    }

    public Boolean validation() {
        if (mForgetmailaddress.length() == 0) {
            forgetmailaddress.setError("Enter email address");
            return false;
        }

        if (!mForgetmailaddress.matches(Util.EmailPattern)) {
            forgetmailaddress.setError("Please check your email address");
            return false;
        }

        return true;
    }
}
