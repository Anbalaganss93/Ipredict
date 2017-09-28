package com.ipredictfantasy.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ipredictfantasy.adapter.MatchlistAdapter;
import com.ipredictfantasy.asynctasks.EmailLoginTask;
import com.ipredictfantasy.dto.MatchesModel;
import com.ipredictfantasy.Fragment.FlowFragment;
import com.ipredictfantasy.Fragment.MatchcategoryDialogue;
import com.ipredictfantasy.R;
import com.ipredictfantasy.interfaces.MymatchInterface;
import com.kingfisherphuoc.quickactiondialog.AlignmentFlag;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;
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

public class MatchsActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks, MymatchInterface {
    public static LeftDrawerLayout drawerLayout;
    public static TextView toolbartext_back;
    private static int pagecount = 0;
    private MatchlistAdapter adapter;
    private SuperRecyclerView listview;
    private ArrayList<MatchesModel> arraylist = new ArrayList<>();
    private String userid, gameid = "1";
    private LinearLayout emptymatch;
    private String mdata;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private EmailLoginTask mMatchcall;
    private AVLoadingIndicatorView avi;
    private InternetChecking internetChecking;
    private String refreshedToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mathes);

        preferences = getSharedPreferences("ipredict", MODE_PRIVATE);
        userid = preferences.getString("ipredict_userid", null);

        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        /*TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String a=telephonyManager.getDeviceId();*/

        internetChecking = new InternetChecking(MatchsActivity.this);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");
        Typeface typefacebold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");

        listview = (SuperRecyclerView) findViewById(R.id.matchlist);
        gameid = preferences.getString("gameid", "1");
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        toolbartext_back = (TextView) findViewById(R.id.toolbartext_back);
        TextView nomatch = (TextView) findViewById(R.id.nomatchcontent);
        emptymatch = (LinearLayout) findViewById(R.id.emptymatch);
        drawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        ImageView drawericon = (ImageView) findViewById(R.id.drawericon);

        String toolbartitle = (gameid.equals("1")) ? "Cricket" : "Football";
        toolbartext_back.setText(toolbartitle);

        Window window = MatchsActivity.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(MatchsActivity.this.getResources().getColor(R.color.colorPrimaryDark));
        }

        emptymatch.setVisibility(View.GONE);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(MatchsActivity.this);
        listview.setLayoutManager(mLayoutManager);
        listview.setRefreshListener(MatchsActivity.this);
        listview.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);

        editor = preferences.edit();
        editor.putString("userlogout", "true");
        editor.apply();

        adapter = new MatchlistAdapter(MatchsActivity.this, arraylist);
        listview.setAdapter(adapter);


        if (internetChecking.isConnectingToInternet()) {
            startAnim();
            new Matchservercall().execute(DomainUrl.MATCHBY);
        }

        if (internetChecking.isConnectingToInternet()) {
            new GCMUpdate().execute(DomainUrl.GCM_UPDATE);
        }
       /* listview.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                // Fetch more from Api or DB
                if (internetChecking.isConnectingToInternet()) {
                    pagecount++;
                    matchservercall(gameid, userid);
                }
            }
        }, 3);*/

        FragmentManager fm = getSupportFragmentManager();
        FlowFragment mMenuFragment = (FlowFragment) fm.findFragmentById(R.id.id_container_menu);
        FlowingView mFlowingView = (FlowingView) findViewById(R.id.sv);
        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new FlowFragment()).commit();
        }
        drawerLayout.setFluidView(mFlowingView);
        drawerLayout.setMenuFragment(mMenuFragment);

        nomatch.setTypeface(typeface);
        toolbartext_back.setTypeface(typefacebold);

        drawericon.setOnClickListener(this);
        toolbartext_back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbartext_back:
                View toolbartext = (TextView) findViewById(R.id.toolbartext_back);
                MatchcategoryDialogue dialogue = new MatchcategoryDialogue();
                dialogue.setAnchorView(toolbartext);
                dialogue.setAligmentFlags(AlignmentFlag.ALIGN_ANCHOR_VIEW_LEFT | AlignmentFlag.ALIGN_ANCHOR_VIEW_BOTTOM);
                dialogue.show(getSupportFragmentManager(), null);
                break;
            case R.id.drawericon:
                drawerLayout.toggle();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRefresh() {
        if (internetChecking.isConnectingToInternet()) {
            stopAnim();
            pagecount = 0;
            new Matchservercall().execute(DomainUrl.MATCHBY);
        }
    }

    @Override
    public boolean canDismiss(int position) {
        return false;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {

    }

    void startAnim() {

        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim() {
        if (avi != null && avi.isShown()) {
            avi.hide();
        }
        // or avi.smoothToHide();
    }

    @Override
    public void mymatchgameid(String mgameid) {
        gameid = mgameid;
        if (internetChecking.isConnectingToInternet()) {
            startAnim();
            new Matchservercall().execute(DomainUrl.MATCHBY);
        }
    }


    private class Matchservercall extends AsyncTask<String, Void, String> {
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
                mdata = regJsonObject.toString();
                Log.d("UserSet_DATA", mdata);
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
            listview.hideMoreProgress();
            if (s != null) {
                try {
                    listview.hideMoreProgress();
                    Log.d("REsult", s);

                    if (pagecount == 0) {
                        adapter.clear();
                    }

                    JSONObject jsonObject = new JSONObject(s);
                    String result = jsonObject.getString("result");
                    String message = jsonObject.getString("message");
                    if (result.equals("success")) {
                        JSONArray array = new JSONArray(jsonObject.getString("match"));
                        if (array.length() != 0) {
                            emptymatch.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                if (array.getJSONObject(i).has("match_time")) {
                                    MatchesModel m = new MatchesModel();
                                    m.setGame_id(array.getJSONObject(i).getString("game_id"));
                                    m.setGame_name(array.getJSONObject(i).getString("game_name"));
                                    m.setTournament_id(array.getJSONObject(i).getString("tournament_id"));
                                    m.setTournament_name(array.getJSONObject(i).getString("tournament_name"));
                                    m.setMatch_id(array.getJSONObject(i).getString("match_id"));
                                    m.setMatches_name(array.getJSONObject(i).getString("matches_name"));
                                    m.setMatches_logo(array.getJSONObject(i).getString("matches_logo"));
                                    m.setOrg_start_date(array.getJSONObject(i).getString("org_start_date"));
                                   /* if (array.getJSONObject(i).has("match_completed_answer")) {
                                        m.setMatch_answered(array.getJSONObject(i).getString("match_completed_answer"));
                                    }*/
                                    m.setMatch_answered(array.getJSONObject(i).getString("match_completed_answer"));
                                    m.setMatch_time(array.getJSONObject(i).getString("match_time"));
                                    m.setMatch_day(array.getJSONObject(i).getString("match_day"));
                                    m.setMatch_month(array.getJSONObject(i).getString("match_month"));
                                    m.setMatch_date(array.getJSONObject(i).getString("match_date"));
                                    adapter.add(m);
                                }
                            }
                        } else {
                            emptymatch.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stopAnim();
            } else {
                Toast.makeText(MatchsActivity.this, R.string.resulterror, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class GCMUpdate extends AsyncTask<String, Void, String> {
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
                regJsonObject.put("google_reg_id", refreshedToken);
                regJsonObject.put("user_id", userid);
                mdata = regJsonObject.toString();
                Log.d("UserSet_DATA", mdata);
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
                Log.d("GCMT_UPDATE", s);
            }
        }
    }
}








