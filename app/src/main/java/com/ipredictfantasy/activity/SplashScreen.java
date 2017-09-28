package com.ipredictfantasy.activity;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.plus.model.people.Person;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ipredictfantasy.R;
import com.ipredictfantasy.dto.MypointsModel;
import com.ipredictfantasy.utility.GlobalActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static String mycolor = "#FF0000";
    private InternetChecking internetChecking;
    private ImageView image;
    private Bitmap myBitmap;
    private String currentVersion, latestVersion;
    private Typeface regular;
    private String mdata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        FirebaseApp.getInstance();

        preferences = getSharedPreferences("ipredict", MODE_PRIVATE);
        image = (ImageView) findViewById(R.id.image);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        editor = preferences.edit();
        editor.putInt("devicewidth", width);
        editor.putInt("deviceheight", height);
        editor.apply();

        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        new generatePictureStyleNotification(this, "messagetitle", "fdg", "http://66.media.tumblr.com/d1dd973d8899c45a5e4cd5d5aca815aa/tumblr_nnshumSpOL1s36w6uo1_500.jpg").execute();
//
//        image.setImageBitmap(getBitmap("http://66.media.tumblr.com/d1dd973d8899c45a5e4cd5d5aca815aa/tumblr_nnshumSpOL1s36w6uo1_500.jpg"));
        String unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("DEVICEID", unique_id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        internetChecking = new InternetChecking(SplashScreen.this);
        try {
            if (internetChecking.isConnectingToInternet()) {
                getCurrentVersion();
//                pageRedirectCall();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pageRedirectCall() {
        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (preferences.contains("userlogout")) {
                        Intent intent = new Intent(getApplicationContext(), MatchsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
                        finish();
                    } else {
                        IntroScreenDataBaseHelper databaseHandler = new IntroScreenDataBaseHelper(SplashScreen.this);
                        String status = databaseHandler.getIntroScreenStatus();
                        Intent intent = new Intent(SplashScreen.this, IntroScreen.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
                        finish();
                    }
                }
            }
        };
        background.start();
    }

    private void getCurrentVersion() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
//        currentVersion = pInfo.versionName;
        currentVersion = String.valueOf(pInfo.versionCode);

        try {
            if (internetChecking.isConnectingToInternet()) {
                new VersionChecker().execute(DomainUrl.VersionUpdate_URL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("A New Update is Available");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.versiondialoguelayout, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        TextView title = (TextView) dialog.findViewById(R.id.connectiontext);
        TextView displaycontent = (TextView) dialog.findViewById(R.id.content);
        displaycontent.setTypeface(regular);

        Button yesbutton = (Button) dialog.findViewById(R.id.ok);
        Button nobutton = (Button) dialog.findViewById(R.id.cancel);
        yesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                finish();
            }
        });

        nobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setCancelable(false);
//        dialog = builder.show();
    }


    private class VersionChecker extends AsyncTask<String, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject regJsonObject = new JSONObject();
                regJsonObject.put("app", "2");
                regJsonObject.put("device", "1");
                mdata = regJsonObject.toString();
                Log.d("UserSet_DATA", mdata);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, mdata);
            Request request = new Request.Builder()
                    .url(params[0])
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (!response.isSuccessful())
                    return String.valueOf(response.code());
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
                try {
                    JSONObject json = new JSONObject(s);
                    String result = json.getString("result");
                    String message = json.getString("message");
                    latestVersion = json.getString("app_version");
                    Log.d("Result_versioncode", currentVersion + " vs " + latestVersion);
                    if (latestVersion != null) {
                        if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                            showUpdateDialog();
                        } else {
                            pageRedirectCall();
                        }
                    } else {
                        pageRedirectCall();
                    }
                } catch (Exception e) {
                    Log.d("Result_versioncode", String.valueOf(e.toString()));
                }
            }
        }
    }

    public static Bitmap getBitmap(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(Context context, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            try {
                image.setImageBitmap(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
