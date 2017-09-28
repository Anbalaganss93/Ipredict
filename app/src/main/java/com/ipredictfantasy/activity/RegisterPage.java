package com.ipredictfantasy.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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


public class RegisterPage extends AppCompatActivity implements View.OnClickListener {
    private String registerdata;
    private EditText name, phonenumber, email, password, c_password;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private AVLoadingIndicatorView avi;
    private InternetChecking internetChecking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        internetChecking = new InternetChecking(RegisterPage.this);

        preferences = getSharedPreferences("ipredict", MODE_PRIVATE);
        editor = preferences.edit();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");
        Typeface typefacebold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");

        Toolbar toolbar = new Toolbar(this);
        View v = inflater.inflate(R.layout.toobarwithback, (ViewGroup) null);
        TextView text = (TextView) findViewById(R.id.toolbartext_back);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        stopAnim();

        text.setTypeface(typefacebold);

        RelativeLayout progresscontainer = (RelativeLayout) findViewById(R.id.progresscontainer);
        progresscontainer.setVisibility(View.GONE);
        ImageView back = (ImageView) findViewById(R.id.back);
        Button register = (Button) findViewById(R.id.r_register);
        name = (EditText) findViewById(R.id.fullname);
        phonenumber = (EditText) findViewById(R.id.phonenumber);
        email = (EditText) findViewById(R.id.emailaddress);
        password = (EditText) findViewById(R.id.r_password);
        c_password = (EditText) findViewById(R.id.r_confirmpassword);

        name.setTypeface(typeface);
        phonenumber.setTypeface(typeface);
        email.setTypeface(typeface);
        password.setTypeface(typeface);
        c_password.setTypeface(typeface);

        text.setText(R.string.register);
        toolbar.addView(v);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Button login_ur = (Button) findViewById(R.id.login_ur);
        login_ur.setOnClickListener(this);
        back.setOnClickListener(this);
        register.setOnClickListener(this);
        register.setTypeface(typefacebold);
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
        Intent intent = new Intent(getApplicationContext(), WelcomeScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_ur:
                Intent intent = new Intent(getApplicationContext(), WelcomeScreen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
                finish();
                break;
            case R.id.r_register:
                if (validation()) {
                    if (internetChecking.isConnectingToInternet()) {
                        startAnim();
                        String username = name.getText().toString().trim();
                        String emailaddress = email.getText().toString().trim();
                        String phoneno = phonenumber.getText().toString().trim();
                        String userpassword = password.getText().toString().trim();
                        String usertype = "1";
                        RegisterServercall(username, emailaddress, phoneno, userpassword, usertype);
                    }
                }
                break;
            case R.id.back:
                Intent mlogin = new Intent(getApplicationContext(), WelcomeScreen.class);
                startActivity(mlogin);
                overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
                finish();
                break;
        }

    }

    private Boolean validation() {
        if (name.getText().length() == 0) {
            name.setError("Enter name");
            Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_LONG).show();
            return false;
        } else if (email.getText().length() == 0) {
            email.setError("Enter email address");
            Toast.makeText(getApplicationContext(), "Please enter email address", Toast.LENGTH_LONG).show();
            return false;
        } else if (phonenumber.getText().length() == 0) {
            phonenumber.setError("Enter phonenumber");
            Toast.makeText(getApplicationContext(), "Please enter phonenumber", Toast.LENGTH_LONG).show();
            return false;
        } else if (phonenumber.getText().length() != 10 && phonenumber.getText().length() > 0) {
            phonenumber.setError("Check your phonenumber");
            Toast.makeText(getApplicationContext(), "Please check your phonenumber", Toast.LENGTH_LONG).show();
            return false;
        } else if (password.getText().length() == 0) {
            password.setError("Enter password");
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG).show();
            return false;
        } else if (!password.getText().toString().equals(c_password.getText().toString())) {
            c_password.setError("Password not match");
            Toast.makeText(getApplicationContext(), "Password not match", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void RegisterServercall(String username, String emailaddress, String phoneno, String userpassword, String usertype) {
        try {
            JSONObject regJsonObject = new JSONObject();
            regJsonObject.put("first_name", username);
            regJsonObject.put("email", emailaddress);
            regJsonObject.put("phone", phoneno);
            regJsonObject.put("password", userpassword);
            regJsonObject.put("login_type", usertype);
            registerdata = regJsonObject.toString();
            Log.d("UserSet_DATA", registerdata);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!Util.isNetworkAvailable(this)) {
            Toast.makeText(RegisterPage.this, "Connect to internet and try again", Toast.LENGTH_SHORT).show();
        } else {
            EmailLoginTask registerlogin = new EmailLoginTask(DomainUrl.REGISTER_URL, registerdata);
            if (!registerdata.isEmpty()) {
                String response = null;
                try {
                    response = registerlogin.execute().get();
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("result");

                    if (result.equals("success")) {
                        String registeduserid = json.getString("id");
                        String image = json.getString("image");
                        String phone = json.getString("phone");
                        String email = json.getString("email");
                        String name = json.getString("name");

                        editor = preferences.edit();
                        editor.putString("ipredict_userid", registeduserid);
                        editor.putString("ipredict_userimage", image);
                        editor.putString("phone", phone);
                        editor.putString("email", email);
                        editor.putString("ipredict_name", name);
                        editor.apply();

                        Intent intent1 = new Intent(getApplicationContext(), MatchsActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
                        finish();
                    } else {
                        String message = json.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
}







