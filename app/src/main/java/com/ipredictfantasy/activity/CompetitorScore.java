package com.ipredictfantasy.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipredictfantasy.adapter.CompetitorAdapter;
import com.ipredictfantasy.dto.WinnerAnalistModel;
import com.ipredictfantasy.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CompetitorScore extends AppCompatActivity {
    private ListView listview;
    private ArrayList<WinnerAnalistModel> arrayList = new ArrayList<>();
    private String mdata;
    private String playid;
    private String userid;
    private String gameid;
    private String userset;
    private TextView user1;
    private TextView user2;
    private RelativeLayout competitor_score_rl_container;
    private TextView useronescore;
    private TextView usertwoscore;
    private TextView waitingopponentanswer;
    private CircularImageView myimg;
    private CircularImageView competitorimg;
    private AVLoadingIndicatorView avi;
    private TextView mMatchTime;
    private TextView mMatchDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitor_score);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");
        Typeface Namefont = Typeface.createFromAsset(getAssets(), "fonts/CinzelDecorative-Black.ttf");
        Typeface typefacebold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");
        Typeface typefacebull = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");

        SharedPreferences preferences = getSharedPreferences("ipredict", MODE_PRIVATE);
        gameid = preferences.getString("gameid", "1");
        String name = preferences.getString("ipredict_name", null);
        String email = preferences.getString("email", null);
        userset = preferences.getString("userset", null);
        String image = preferences.getString("ipredict_userimage", null);
        playid = preferences.getString("playid", null);
        userid = preferences.getString("ipredict_userid", null);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("matchid")) {
            String playid = intent.getStringExtra("matchid");
        }
        InternetChecking internetChecking = new InternetChecking(CompetitorScore.this);

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView back = (ImageView) findViewById(R.id.back);
        listview = (ListView) findViewById(R.id.user_resultlist);
//        LinearLayout relative_container = (LinearLayout) findViewById(R.id.relative_container);
        LinearLayout mTeamOneContainer = (LinearLayout) findViewById(R.id.competitor_score_ll_team1container);
        LinearLayout mTeamTwoContainer = (LinearLayout) findViewById(R.id.competitor_score_ll_team2container);
        TextView vs = (TextView) findViewById(R.id.vs);
        waitingopponentanswer = (TextView) findViewById(R.id.waitingopponentanswer);
//        competitor_score_rl_container = (RelativeLayout) findViewById(R.id.competitor_score_rl_container);
        user1 = (TextView) findViewById(R.id.competitor_score_teamone_name);
        user2 = (TextView) findViewById(R.id.competitor_score_teamtwo_name);
        mMatchDate = (TextView) findViewById(R.id.competitor_score_date);
        mMatchTime = (TextView) findViewById(R.id.competitor_score_time);
        useronescore = (TextView) findViewById(R.id.useronescore);
        usertwoscore = (TextView) findViewById(R.id.usertwoscore);
        TextView toolbartext_back = (TextView) findViewById(R.id.toolbartext_back);
        competitorimg = (CircularImageView) findViewById(R.id.competitorimg);
        myimg = (CircularImageView) findViewById(R.id.myimg);
        ImageView sportimage = (ImageView) findViewById(R.id.competitor_score_sportimage);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setHomeButtonEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        FrameLayout.LayoutParams team1param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        team1param.width = (int) (metrics.widthPixels * 0.4);
        team1param.setMargins((int) (metrics.widthPixels * 0.1), (int) (metrics.widthPixels * 0.2), 0, 0);
        team1param.gravity = Gravity.LEFT;
        user1.setLayoutParams(team1param);

        FrameLayout.LayoutParams team2param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        team2param.width = (int) (metrics.widthPixels * 0.4);
        team2param.setMargins(0, (int) (metrics.heightPixels * 0.3), (int) (metrics.widthPixels * 0.12), 0);
        team2param.gravity = Gravity.RIGHT;
        user2.setLayoutParams(team2param);

        /*LinearLayout.LayoutParams frameparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        frameparam.width = (int) (metrics.widthPixels);
        frameparam.height = (int) (0.5 * metrics.heightPixels);
        relative_container.setLayoutParams(frameparam);*/

        LinearLayout.LayoutParams profileimageparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        profileimageparam.width = (int) (0.12 * metrics.widthPixels);
        profileimageparam.height = (int) (0.12 * metrics.widthPixels);
        profileimageparam.gravity = Gravity.CENTER_VERTICAL;
        competitorimg.setLayoutParams(profileimageparam);
        myimg.setLayoutParams(profileimageparam);

        FrameLayout.LayoutParams sportimageparam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        sportimageparam.setMargins((int) (metrics.widthPixels * 0.05), (int) (metrics.heightPixels * 0.37), 0, 0);
        sportimageparam.gravity = (Gravity.CENTER_HORIZONTAL);
        vs.setLayoutParams(sportimageparam);

        FrameLayout.LayoutParams sportcontainerparam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        sportcontainerparam.width = (int) (0.35 * metrics.heightPixels);
        sportcontainerparam.height = (int) (0.5 * metrics.heightPixels);
        sportcontainerparam.gravity = (Gravity.CENTER);
        sportimage.setLayoutParams(sportcontainerparam);

        if(gameid.equals("1")){
            sportimage.setImageResource(R.drawable.cricketsample2);
        }else{
            sportimage.setImageResource(R.drawable.footsample3);
        }
        user1.bringToFront();
        user2.bringToFront();

        waitingopponentanswer.setVisibility(View.GONE);
        try {
            if (internetChecking.isConnectingToInternet()) {
                new Myscoreservercall().execute(DomainUrl.MYSCORE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        vs.setTypeface(typefacebull);
        user1.setTypeface(Namefont);
        user2.setTypeface(Namefont);
        toolbartext_back.setTypeface(typefacebold);
        waitingopponentanswer.setTypeface(typefacebold);

    }

    void startAnim() {
        avi.setVisibility(View.VISIBLE);
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim() {
        avi.setVisibility(View.GONE);
        avi.hide();
        // or avi.smoothToHide();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class Myscoreservercall extends AsyncTask<String, Void, String> {
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
                regJsonObject.put("game_id", gameid);
                regJsonObject.put("user_id", userid);
                regJsonObject.put("match_id", playid);
                regJsonObject.put("set", userset);
                mdata = regJsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, mdata);
            Request request = new Request.Builder()
                    .url(params[0])
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
            if (s != null) {
                try {
                    Log.d("Result", s);
                    JSONObject json = new JSONObject(s);
                    String result = json.getString("result");
                    String message = json.getString("message");

                    if (result.equals("success")) {
                        String user_id = json.getString("user_id");
                        String user_name = json.getString("user_name");
                        String user_image = json.getString("user_image");
                        String opponent_id = json.getString("opponent_id");
                        String opponent_name = json.getString("opponent_name");
                        String opponent_image = json.getString("opponent_image");
                        String winner_id = json.getString("winner_id");
                        String date = json.getString("start_day");
                        String time = json.getString("start_time");
                        String userscore = json.getString("userscore");
                        String opponentscore = json.getString("opponentscore");
                        JSONArray array = new JSONArray(json.getString("crosscheck"));
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                waitingopponentanswer.setVisibility(View.GONE);
                                WinnerAnalistModel m = new WinnerAnalistModel();
                                m.setQuestion(array.getJSONObject(i).getString("question"));
                                m.setUseranswer(array.getJSONObject(i).getString("useranswer"));
                                m.setCorrect_answer(array.getJSONObject(i).getString("correct_answer"));
                                String opponentanswer = array.getJSONObject(i).getString("opponent_useranswer").equals("") ? " - " : array.getJSONObject(i).getString("opponent_useranswer");
                                m.setOpponent_useranswer(opponentanswer);
                                m.setUsercolor(array.getJSONObject(i).getString("usercolor"));
                                String opponentcolor = array.getJSONObject(i).getString("opponentcolor").equals("") ? "#000000" : array.getJSONObject(i).getString("opponentcolor");
                                m.setOpponentcolor(opponentcolor);
                                m.setUsername(user_name);
                                m.setOpponentname(opponent_name);
                                arrayList.add(m);
                            }
                        } else {
                            waitingopponentanswer.setVisibility(View.VISIBLE);
                        }
                        CompetitorAdapter adapter = new CompetitorAdapter(CompetitorScore.this, arrayList);
                        listview.setAdapter(adapter);
                        user1.setAllCaps(true);
                        user2.setAllCaps(true);
                        if (winner_id.equals(user_id)) {
                            user1.setText(user_name);
                            user2.setText(opponent_name);
                            if (opponent_name.trim().equals("")) {
                                user2.setAllCaps(false);
                                user2.setText("Mr.X");
                            }
                            useronescore.setText(userscore);
                            usertwoscore.setText(opponentscore);
                            Picasso.with(CompetitorScore.this).load(user_image).placeholder(R.drawable.profiledefaultimg).into(myimg);
                            Picasso.with(CompetitorScore.this).load(opponent_image).placeholder(R.drawable.profile_default_yellow).into(competitorimg);
                        } else {
//                            String opponent = opponent_name.equals("") ? "?" : opponent_name;
                            user1.setText(opponent_name);
                            if (opponent_name.trim().equals("")) {
                                user1.setAllCaps(false);
                                user1.setText("Mr.X");
                            }
                            user2.setText(user_name);
                            useronescore.setText(opponentscore);
                            usertwoscore.setText(userscore);
                            Picasso.with(CompetitorScore.this).load(opponent_image).placeholder(R.drawable.profiledefaultimg).into(myimg);
                            Picasso.with(CompetitorScore.this).load(user_image).placeholder(R.drawable.profile_default_yellow).into(competitorimg);
                        }
                        mMatchDate.setText(date);
                        mMatchTime.setText(time);

                    } else {
                        if (message.equals("Check User and Opponent User has completed Play !!!")) {
                            waitingopponentanswer.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("No", String.valueOf(e.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(CompetitorScore.this, R.string.resulterror, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
