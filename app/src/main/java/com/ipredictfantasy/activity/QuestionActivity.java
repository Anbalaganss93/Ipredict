package com.ipredictfantasy.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ipredictfantasy.R;
import com.ipredictfantasy.dto.MatchesModel;
import com.squareup.picasso.Picasso;
import com.vungle.publisher.AdConfig;
import com.vungle.publisher.EventListener;
import com.vungle.publisher.Orientation;
import com.vungle.publisher.VunglePub;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    public static Dialog dialog = null;
    public static String selected_anwer = "";
    final String placementIdForLevel = "DEFAULT42695";
    public LinearLayout linearLayout1, linearLayout2;
    private InterstitialAd mInterstitialAd;
    private RadioGroup mradiogroup;
    private TextView noofquestion;
    private TextView noofset;
    private TextView ipredictquestion;
    private Button next;
    private AVLoadingIndicatorView avi;
    private Typeface typeface, typefacebold;
    private ImageView questionimage;
    private TextView tvDay, tvHour, tvMinute, tvSecond, tvEvent;
    private Handler handler;
    private Runnable runnable;
    private InternetChecking internetChecking;
    private String userid;
    private String usermatchid;
    private String questionid = "";
    private String userset = "";
    private String question_data;
    private String match_completed_answer;
    private String total_set = "0";
    private String question;
    private String question_no;
    private String previous_question_id = "";
    private String next_question_id = "";
    private String org_start_date;
    private String popup = "0";
    private Intent intent;
    private ArrayList<MatchesModel> marcharraylist = new ArrayList();
    private SharedPreferences preferences;
    private String app_id = "597aa449035bbfbe7f000014";
    final VunglePub vunglePub = VunglePub.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        vunglePub.init(this, app_id);
        vunglePub.setEventListeners(vungleDefaultListener, vungleSecondListener);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");
        typefacebold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");
        internetChecking = new InternetChecking(QuestionActivity.this);
        preferences = getSharedPreferences("ipredict", MODE_PRIVATE);

        intent = getIntent();

        String username = preferences.getString("ipredict_name", " ");
        String useremail = preferences.getString("email", null);
        userid = preferences.getString("ipredict_userid", null);
        String userimage = preferences.getString("ipredict_userimage", null);
        usermatchid = preferences.getString("matchid", null);
        String gameid = preferences.getString("gameid", "1");
        userset = preferences.getString("userset", "0");
        if (preferences.contains("fromcompetition")) {
            next_question_id = preferences.getString("questionid", "");
        }

        DisplayMetrics matrix = new DisplayMetrics();
        QuestionActivity.this.getWindowManager().getDefaultDisplay().getMetrics(matrix);

        questionimage = (ImageView) findViewById(R.id.questionimage);
        linearLayout1 = (LinearLayout) findViewById(R.id.ll1);
        linearLayout2 = (LinearLayout) findViewById(R.id.ll2);
        tvDay = (TextView) findViewById(R.id.txtTimerDay);
        tvHour = (TextView) findViewById(R.id.txtTimerHour);
        tvMinute = (TextView) findViewById(R.id.txtTimerMinute);
        tvSecond = (TextView) findViewById(R.id.txtTimerSecond);

        tvEvent = (TextView) findViewById(R.id.tvevent);
        ImageView backicon = (ImageView) findViewById(R.id.backicon);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        mradiogroup = (RadioGroup) findViewById(R.id.rgroup);
        next = (Button) findViewById(R.id.next);
        Button previous = (Button) findViewById(R.id.previous);
        TextView timmer = (TextView) findViewById(R.id.timmer);
        noofset = (TextView) findViewById(R.id.noofset);
        ipredictquestion = (TextView) findViewById(R.id.question);
        TextView faqtooltext = (TextView) findViewById(R.id.faqtooltext);
        noofquestion = (TextView) findViewById(R.id.noofquestion);

        String toolbartitle = (gameid.equals("1")) ? "Cricket" : "Football";
        faqtooltext.setText(toolbartitle);
        faqtooltext.setTypeface(typefacebold);

       /* mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (internetChecking.isConnectingToInternet()) {
                    startAnim();
                    CategoriesQuestioncall(userid, usermatchid, questionid, selected_anwer, next_question_id, userset);
                }
            }
        });*/

//        requestNewInterstitial();
        countDownStart();
        startAnim();
        next.setText(R.string.nextbutton);

        if (internetChecking.isConnectingToInternet()) {
            String useranswer = "";
            CategoriesQuestioncall(userid, usermatchid, questionid, useranswer, next_question_id, userset);
        } else {
            stopAnim();
        }
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        backicon.setOnClickListener(this);

        previous.setTypeface(typefacebold);
        next.setTypeface(typefacebold);
        noofquestion.setTypeface(typefacebold);
        timmer.setTypeface(typefacebold);
        noofset.setTypeface(typefacebold);
        ipredictquestion.setTypeface(typefacebold);
        faqtooltext.setTypeface(typefacebold);

    }

    /*private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }*/

    void startAnim() {
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim() {
        avi.hide();
        // or avi.smoothToHide();
    }

    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    // Here Set your Event Date
                    Date futureDate = dateFormat.parse(org_start_date);
                    Date currentDate = new Date();
                    if (!currentDate.after(futureDate)) {
                        long diff = futureDate.getTime()
                                - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        tvDay.setText(String.format("%02d", days));
                        tvHour.setText("" + String.format("%02d", hours));
                        tvMinute.setText("" + String.format("%02d", minutes));
                        tvSecond.setText("" + String.format("%02d", seconds));
                        tvDay.setTypeface(typefacebold);
                        tvHour.setTypeface(typefacebold);
                        tvMinute.setTypeface(typefacebold);
                        tvSecond.setTypeface(typefacebold);
                    } else {
                        linearLayout1.setVisibility(View.VISIBLE);
                        linearLayout2.setVisibility(View.GONE);
                        tvEvent.setText(R.string.finishtext);
                        handler.removeCallbacks(runnable);
                    }
                    tvEvent.setTypeface(typeface);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous:
                if (internetChecking.isConnectingToInternet()) {
                    if (!previous_question_id.equals("0")) {
                        CategoriesQuestioncall(userid, usermatchid, questionid, selected_anwer, previous_question_id, userset);
                    } else {
                        Toast.makeText(QuestionActivity.this, "No more questions.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.next:
                if (internetChecking.isConnectingToInternet()) {
                    mradiogroup.clearCheck();
                    String fromCMP = preferences.getString("fromcompetition", "");
                    if (preferences.contains("fromcompetition")) {
                        match_completed_answer = "0";
                    }
                    if (match_completed_answer.equals("0")) {
                        if (!selected_anwer.equals("")) {
                            next.setText(R.string.nextbutton);

                            if (question_no.equals("3")) {
                                PlayAd();
                                /*if (mInterstitialAd.isLoaded()) {
                                    mInterstitialAd.show();
                                    PlayAd();
                                } else {
                                    CategoriesQuestioncall(userid, usermatchid, questionid, selected_anwer, next_question_id, userset);
                                }*/
                            } else {
                                CategoriesQuestioncall(userid, usermatchid, questionid, selected_anwer, next_question_id, userset);
                            }
                            if (question_no.equals("5")) {
                                next.setText(R.string.submitbutton);
                            }
                        }
                        Toast.makeText(QuestionActivity.this, "Sample = " + selected_anwer, Toast.LENGTH_SHORT).show();
                    } else {
                        openDialog("2");
                    }
                }
                break;

            case R.id.backicon:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void CategoriesQuestioncall(String userid, String usermatchid, String questionid, String useranswer, String nextquestionid, String userset) {
        try {
            JSONObject regJsonObject = new JSONObject();
            regJsonObject.put("user_id", userid);
            regJsonObject.put("user_match_id", usermatchid);
            regJsonObject.put("question_id", questionid);
            regJsonObject.put("answer", useranswer);
            regJsonObject.put("next_question_id", nextquestionid);
            regJsonObject.put("set", userset);
            question_data = regJsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            stopAnim();
        }

        try {
            if (internetChecking.isConnectingToInternet()) {
                new QuestionServerCall().execute(DomainUrl.categories_question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openDialog(final String dialogue_no) {
        DisplayMetrics matrix = new DisplayMetrics();
        QuestionActivity.this.getWindowManager().getDefaultDisplay().getMetrics(matrix);
        int width = matrix.widthPixels;

        dialog = new Dialog(QuestionActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.questionalert);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        final TextView connectiontext = (TextView) dialog.findViewById(R.id.connectiontext);
        final TextView internetcontent = (TextView) dialog.findViewById(R.id.internetcontent);
        final Button ok = (Button) dialog.findViewById(R.id.ok);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);

        String dialoguecontent = dialogue_no.equals("1") ? "Are you want play another set in this match." : "No More Questions.Are you want play another match";
        internetcontent.setText(dialoguecontent);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (width * 0.9);
        params.setMargins(0, 20, 0, 0);
        connectiontext.setLayoutParams(params);

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialogue_no.equals("1")) {
                    CategoriesQuestioncall(userid, usermatchid, questionid, selected_anwer, next_question_id, userset);
                } else {
                    Intent intent = new Intent(QuestionActivity.this, MatchsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogue_no.equals("1")) {
                    finish();
                } else {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });

        connectiontext.setTypeface(typeface);
        internetcontent.setTypeface(typeface);
        ok.setTypeface(typefacebold);
        cancel.setTypeface(typefacebold);

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }


    private class QuestionServerCall extends AsyncTask<String, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnim();
        }

        @Override
        protected String doInBackground(String... urls) {
            RequestBody body = RequestBody.create(JSON, question_data);
            Request request = new Request.Builder()
                    .url(urls[0])
                    .post(body)
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
            stopAnim();
            if (!s.isEmpty()) {
                try {
                    if (marcharraylist.size() != 0) {
                        marcharraylist.clear();
                    }
                    JSONObject jsonObject = new JSONObject(s);
                    Log.d("REsult_Question", s);
                    String result = jsonObject.getString("result");
                    String message = jsonObject.getString("message");

                    if (result.equals("success")) {
                        String complete = jsonObject.getString("complete");
                        String totalquestion_number = jsonObject.getString("total_no_questions");

                        if (jsonObject.has("question")) {
                            question = jsonObject.getString("question");
                            question_no = jsonObject.getString("question_no");
                            if (question_no.equals("6")) {
                                question_no = "1";
                            }
                            userset = jsonObject.getString("set");
                            total_set = jsonObject.getString("total_set");
                            questionid = jsonObject.getString("question_id");
                            previous_question_id = jsonObject.getString("previous_question_id");
                            next_question_id = jsonObject.getString("next_question_id");
                            org_start_date = jsonObject.getString("org_start_date");
                            popup = jsonObject.getString("popup");
                            match_completed_answer = jsonObject.getString("match_completed_answer");
                            String image = jsonObject.getString("matches_logo");
                            Picasso.with(QuestionActivity.this).load(image).placeholder(R.drawable.profiledefaultimg).into(questionimage);
                            selected_anwer = jsonObject.getString("selected_anwer");
                            JSONArray array1 = new JSONArray(jsonObject.getString("answeroption"));
                            RadioButton rdbtn = null;
                            for (int row = 0; row < 1; row++) {
                                final RadioGroup ll = new RadioGroup(QuestionActivity.this);
                                ((ViewGroup) findViewById(R.id.rgroup)).removeAllViews();
                                ll.setOrientation(LinearLayout.VERTICAL);
                                ll.setGravity(Gravity.CENTER_VERTICAL);

                                DisplayMetrics matrix = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(matrix);

                                for (int i = 0; i < array1.length(); i++) {
                                    rdbtn = new RadioButton(QuestionActivity.this);
                                    rdbtn.setId(i + 1);
                                    RadioGroup.LayoutParams params_rb = new RadioGroup.LayoutParams(
                                            (int) (0.8 * matrix.widthPixels),
                                            RadioGroup.LayoutParams.WRAP_CONTENT);
                                    params_rb.setMargins(30, 30, 30, 30);
                                    rdbtn.setLayoutParams(params_rb);
                                    rdbtn.setPadding(10, 10, 10, 10);
                                    rdbtn.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.black));
                                    rdbtn.setBackgroundColor(ContextCompat.getColor(QuestionActivity.this, R.color.red));
                                    rdbtn.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                                    rdbtn.setText(array1.getJSONObject(i).getString("option"));
                                    rdbtn.setBackgroundResource(R.drawable.radiobuttonborder);
                                    rdbtn.setButtonDrawable(R.drawable.radiobuttonbackground);
                                    ll.addView(rdbtn);

                                    ll.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                                            RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
//                                            checkedRadioButton.setBackgroundResource(R.drawable.radiobuttonselectedborder);
//                                            radiobuttoncolorChange();
//                                            checkedRadioButton.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.white));
                                            selected_anwer = String.valueOf(checkedId);
//                                            notify();
                                        }
                                    });
                                }

                                ((ViewGroup) findViewById(R.id.rgroup)).addView(ll);

                                if (selected_anwer.length() != 0 && !selected_anwer.equals("0")) {
                                    int selectedid = Integer.parseInt(selected_anwer) - 1;
                                    ((RadioButton) ll.getChildAt(selectedid)).setChecked(true);
                                }
                            }
                        }

                        ipredictquestion.setText(String.valueOf(question));
                        noofquestion.setText("Q" + String.valueOf(question_no) + "/" + 5);
                        noofset.setText("Set" + userset + "/" + String.valueOf(total_set));
                        String nextbuttontext = question_no.equals("5") ? "Submit" : "Next";
                        next.setText(nextbuttontext);
                        if (popup.equals("1")) {
                            openDialog("1");
                        }
                        if (!preferences.contains("fromcompetition")) {
                            if (match_completed_answer.equals("1")) {
                                openDialog("2");
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), String.valueOf(e.toString()), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), String.valueOf(e.toString()), Toast.LENGTH_SHORT).show();
                }
                stopAnim();
            } else {
                Toast.makeText(QuestionActivity.this, R.string.resulterror, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final EventListener vungleDefaultListener = new EventListener() {

        @Override
        public void onAdStart() {
            // Called before playing an ad.
        }

        @Override
        public void onAdUnavailable(String reason) {
            // Called when VunglePub.playAd() was called but no ad is available to show to the user.
        }

        @Override
        public void onAdEnd(boolean wasSuccessfulView, boolean wasCallToActionClicked) {
            // Called when the user leaves the ad and control is returned to your application.
        }

        @Override
        public void onAdPlayableChanged(boolean isAdPlayable) {
            // Called when ad playability changes.
            Log.d("DefaultListener", "This is a default eventlistener.");
            final boolean enabled = isAdPlayable;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Called when ad playability changes.
                }
            });
        }
    };

    private final EventListener vungleSecondListener = new EventListener() {
        // Vungle SDK allows for multiple listeners to be attached. This secondary event listener is only
        // going to print some logs for now, but it could be used to Pause music, update a badge icon, etc.

        @Override
        public void onAdStart() {
            Log.d("SecondListener", "This is a second event listener. Ad is about to play now!");
        }

        @Override
        public void onAdUnavailable(String reason) {
            Log.d("SecondListener", String.format("This is a second event listener. Ad is unavailable to play - %s", reason));
            CategoriesQuestioncall(userid, usermatchid, questionid, selected_anwer, next_question_id, userset);
        }

        @Override
        public void onAdEnd(boolean wasSuccessfulView, boolean wasCallToActionClicked) {
            Log.d("SecondListener", String.format("This is a second event listener. Ad finished playing, wasSuccessfulView - %s, wasCallToActionClicked - %s", wasSuccessfulView, wasCallToActionClicked));

        }

        @Override
        public void onAdPlayableChanged(boolean isAdPlayable) {
            Log.d("SecondListener", String.format("This is a second event listener. Ad playability has changed, and is now: %s", isAdPlayable));
        }
    };

    private void PlayAd() {
        vunglePub.playAd();
    }

    private void PlayAdOptions() {
        // create a new AdConfig object
        final AdConfig overrideConfig = new AdConfig();

        // set any configuration options you like.
        overrideConfig.setOrientation(Orientation.autoRotate);
        overrideConfig.setSoundEnabled(false);
        overrideConfig.setBackButtonImmediatelyEnabled(false);
        overrideConfig.setPlacement("StoreFront");
        //overrideConfig.setExtra1("LaunchedFromNotification");

        // the overrideConfig object will only affect this ad play.
        vunglePub.playAd(overrideConfig);
    }

    private void PlayAdIncentivized() {
        // create a new AdConfig object
        final AdConfig overrideConfig = new AdConfig();

        // set incentivized option on
        overrideConfig.setIncentivized(true);
        overrideConfig.setIncentivizedCancelDialogTitle("Careful!");
        overrideConfig.setIncentivizedCancelDialogBodyText("If the video isn't completed you won't get your reward! Are you sure you want to close early?");
        overrideConfig.setIncentivizedCancelDialogCloseButtonText("Close");
        overrideConfig.setIncentivizedCancelDialogKeepWatchingButtonText("Keep Watching");

        // the overrideConfig object will only affect this ad play.
        vunglePub.playAd(overrideConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        vunglePub.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vunglePub.onResume();
    }

    @Override
    protected void onDestroy() {
        // onDestroy(), remove eventlisteners.
        vunglePub.removeEventListeners(vungleDefaultListener, vungleSecondListener);
        super.onDestroy();
    }
}
