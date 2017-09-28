package com.ipredictfantasy.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipredictfantasy.asynctasks.EmailLoginTask;
import com.ipredictfantasy.R;
import com.ipredictfantasy.ui.BlurTransformation;
import com.ipredictfantasy.ui.ImageCirclewithborder;
import com.ipredictfantasy.utility.Util;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    public static ImageView backprofile;
    public static ImageCirclewithborder profileimage;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String profileedited_data, e_email, e_name, e_phone, userid;
    private EditText profile_phone, profile_email, profile_name;
    private Button editbutton;
    private Boolean buttonclick = false;
    private KeyListener keyLis, keyLis2, keyLis3;
    private int MY_PERMISSIONS_REQUEST = 1;
    public static String encodedImage = "";
    public static Bitmap currentimage = null, photo;
    public AlertDialog.Builder alert;
    public AlertDialog c;
    public Dialog dialog;
    private InternetChecking internetChecking;
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");
        Typeface typefacebold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");

        internetChecking = new InternetChecking(Profile.this);
        preferences = getSharedPreferences("ipredict", MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("base64image", "");
        editor.apply();

        userid = preferences.getString("ipredict_userid", null);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        Toolbar toolbar = new Toolbar(this);

        View v = inflater.inflate(R.layout.toobarwithback, (ViewGroup) null);
        TextView toolbartext = (TextView) findViewById(R.id.toolbartext_back);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        toolbartext.setText(R.string.myprofiletitle);
        toolbartext.setTypeface(typefacebold);
        toolbar.addView(v);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RelativeLayout progresscontainer = (RelativeLayout) findViewById(R.id.progresscontainer);
        backprofile = (ImageView) findViewById(R.id.backprofile);
        profileimage = (ImageCirclewithborder) findViewById(R.id.profileimage);
        progresscontainer.setVisibility(View.GONE);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (0.32 * metrics.widthPixels);
        params.height = (int) (0.32 * metrics.widthPixels);
        profileimage.setLayoutParams(params);

        ImageView back = (ImageView) findViewById(R.id.back);
        editbutton = (Button) findViewById(R.id.editbutton);
        profile_email = (EditText) findViewById(R.id.profile_email);
        profile_name = (EditText) findViewById(R.id.Profile_name);
        profile_phone = (EditText) findViewById(R.id.profile_phone);

        keyLis = profile_email.getKeyListener();
        keyLis2 = profile_name.getKeyListener();
        keyLis3 = profile_phone.getKeyListener();

        if (internetChecking.isConnectingToInternet()) {
            startAnim();
            new Myprofile().execute(DomainUrl.MYPROFILE + "/" + userid);
        }

        profile_email.setTypeface(typeface);
        profile_name.setTypeface(typeface);
        profile_phone.setTypeface(typeface);
        editbutton.setTypeface(typefacebold);

        profile_email.setClickable(false);
        profile_name.setClickable(false);
        profile_phone.setClickable(false);
        profileimage.setClickable(false);
        profile_email.setKeyListener(null);
        profile_name.setKeyListener(null);
        profileimage.setOnKeyListener(null);
        profile_phone.setKeyListener(null);
        profile_email.setFocusable(false);
        profile_name.setFocusable(false);
        profile_phone.setFocusable(false);
        profile_email.setFocusableInTouchMode(false);
        profile_phone.setFocusableInTouchMode(false);
        profile_name.setFocusableInTouchMode(false);
        profileimage.setFocusableInTouchMode(false);

        editbutton.setText(R.string.editbutton);

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonclick) {
                    buttonclick = true;
                    profile_email.setKeyListener(keyLis);
                    profile_name.setKeyListener(keyLis2);
                    profile_phone.setKeyListener(keyLis3);
                    profile_phone.setKeyListener(keyLis3);
                    profile_email.setFocusable(true);
                    profile_name.setFocusable(true);
                    profile_phone.setFocusable(true);
                    profileimage.setClickable(true);
                    profile_email.setFocusableInTouchMode(true);
                    profile_phone.setFocusableInTouchMode(true);
                    profile_name.setFocusableInTouchMode(true);
                    profileimage.setFocusableInTouchMode(true);
                    editbutton.setText(R.string.savebutton);
                } else {
                    startAnim();
                    buttonclick = false;
                    profile_email.setKeyListener(null);
                    profile_name.setKeyListener(null);
                    profile_phone.setKeyListener(null);
                    profileimage.setOnKeyListener(null);
                    profile_email.setFocusable(false);
                    profile_name.setFocusable(false);
                    profile_phone.setFocusable(false);
                    profileimage.setClickable(false);
                    profile_email.setFocusableInTouchMode(false);
                    profile_phone.setFocusableInTouchMode(false);
                    profile_name.setFocusableInTouchMode(false);
                    profileimage.setFocusableInTouchMode(false);
                    editbutton.setText(R.string.editbutton);

                    e_email = profile_email.getText().toString().trim();
                    e_name = profile_name.getText().toString().trim();
                    e_phone = profile_phone.getText().toString().trim();

                    if (validation()) {
                        if (internetChecking.isConnectingToInternet()) {
                            encodedImage = preferences.getString("base64image", null);
                            Editprofile(e_email, e_phone, e_name);
                        }
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profileimage.setOnClickListener(this);
    }

    public Boolean validation() {
        if (e_name.length() == 0) {
            profile_name.setError("Please enter name");
            return false;
        } else if (e_email.length() == 0) {
            profile_email.setError("Please enter email address");
            return false;
        } else if (!e_email.matches(Util.EmailPattern)) {
            profile_email.setError("Check your email address");
            return false;
        } else if (e_phone.length() == 0) {
            profile_phone.setError("Please enter phone number");
            return false;
        } else if (e_phone.length() < 10) {
            profile_phone.setError("Check your phone number");
            return false;
        }
        return true;
    }

    private class Myprofile extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            Request request = new Request.Builder().url(params[0]).get().build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code" + response.toString());
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Log.d("REsult", s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String _image = jsonObject.getString("image");
                    String _phone = jsonObject.getString("phone");
                    String _email = jsonObject.getString("email");
                    String _name = jsonObject.getString("name");

                    profile_email.setText(_email);
                    profile_name.setText(_name);
                    profile_phone.setText(_phone);

                    Picasso.with(Profile.this).load(_image)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.profiledefaultimg).into(profileimage);
                    Picasso.with(Profile.this).load(_image)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE).transform(new BlurTransformation(18)).placeholder(R.drawable.profiledefaultimg).into(backprofile);

                    editor = preferences.edit();
                    editor.putString("ipredict_userimage", _image);
                    editor.putString("phone", _phone);
                    editor.putString("email", _email);
                    editor.putString("ipredict_name", _name);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            stopAnim();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    void startAnim() {
        avi.setVisibility(View.VISIBLE);
        avi.smoothToShow();
    }

    void stopAnim() {
        avi.setVisibility(View.GONE);
        avi.smoothToHide();
    }

    private void Editprofile(String e_emailaddress, String e_phone, String e_name) {
        try {
            JSONObject regJsonObject = new JSONObject();
            regJsonObject.put("name", e_name);
            regJsonObject.put("email", e_emailaddress);
            regJsonObject.put("phone", e_phone);
            regJsonObject.put("image", encodedImage);
            profileedited_data = regJsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!Util.isNetworkAvailable(this)) {
            Toast.makeText(getApplicationContext(), "Connect to internet and try again", Toast.LENGTH_SHORT).show();
        } else {
            EmailLoginTask medit = new EmailLoginTask(DomainUrl.editprofile + userid, profileedited_data);
            if (!profileedited_data.isEmpty()) {
                try {
                    String response = medit.execute().get();
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if (result.equals("success")) {
                        String _userid = jsonObject.getString("id");
                        String _image = jsonObject.getString("image");
                        String _phone = jsonObject.getString("phone");
                        String _email = jsonObject.getString("email");
                        String _name = jsonObject.getString("name");
                        editor = preferences.edit();
                        editor.putString("ipredict_userid", _userid);
                        editor.putString("ipredict_userimage", _image);
                        editor.putString("phone", _phone);
                        editor.putString("email", _email);
                        editor.putString("ipredict_name", _name);
                        editor.apply();

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
            stopAnim();
        }

    }

    String Arr[] = {"Gallery", "Camera"};

    public void uploadPhoto(View v) {
        alert = new AlertDialog.Builder(Profile.this);
        alert.setTitle("  photo");
        alert.setSingleChoiceItems(Arr, 2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                c.dismiss();
                if (which == 0) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 0);
                } else if (which == 1) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, 1);
                }
            }
        });
        c = alert.create();
        c.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 0) {
                try {
                    Uri uri = data.getData();
                    currentimage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    currentimage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Intent intent = new Intent(getApplicationContext(), CroperActivity.class);
                    startActivity(intent);

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Fail to allocate memory.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                if (data != null) {
                    photo = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    currentimage = photo;
                    Intent intent = new Intent(getApplicationContext(), CroperActivity.class);
                    startActivity(intent);
                } else {
                    onStop();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    // Permission granted
                } else {

                }
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(getApplicationContext(), "Permission was denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileimage:
                if (buttonclick) {
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion >= 23) {
                        if (ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(Profile.this, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
                        } else {
                            uploadPhoto(v);
                        }
                    } else {
                        uploadPhoto(v);
                    }
                }
                break;
        }
    }
}
