package com.ipredictfantasy.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ipredictfantasy.adapter.Monthlypoints_Adapter;
import com.ipredictfantasy.adapter.Myfragmentpoints_Adapter;
import com.ipredictfantasy.dto.MypointsModel;
import com.ipredictfantasy.R;
import com.ipredictfantasy.utility.GlobalActivity;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by anbu0 on 24/03/2016.
 */
public class Mypointsfragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks {

    private static int pagecount = 0;
    SuperRecyclerView monthlylist;
    private LinearLayoutManager mLayoutManager;
    private Handler mHandler;
    LinearLayout emptymypoints;
    Myfragmentpoints_Adapter adapter;
    Monthlypoints_Adapter monlyadapter;
    String[] points = new String[]{"45", "65", "75", "30"};
    ArrayList<MypointsModel> arrayList = new ArrayList<>();
    ArrayList<MypointsModel> monthlyarraylist = new ArrayList<>();
    Typeface typeface, typefacebold;
    TextView leaderboard_points, leaderboardlable, emptycontent;
    private InternetChecking internetChecking;
    private AVLoadingIndicatorView avi;
    private String userid, gameid;
    private SharedPreferences preferences;

    public static Fragment newInstance(String gameid) {
        Mypointsfragment f = new Mypointsfragment();
        Bundle localBundle = new Bundle(1);
        localBundle.putString("EXTRA_MESSAGE", gameid);
        f.setArguments(localBundle);
        Log.d("Position", String.valueOf(gameid));
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mypointsfragmentlayout, (ViewGroup) null);

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/proximanova-reg.ttf");
        typefacebold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/proximanova-bold.ttf");

        getActivity();
        preferences = getActivity().getSharedPreferences("ipredict", Context.MODE_PRIVATE);
        internetChecking = new InternetChecking(getActivity());

        avi = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        gameid = preferences.getString("gameid", "1");
        emptymypoints = (LinearLayout) v.findViewById(R.id.emptymypoints);
        monthlylist = (SuperRecyclerView) v.findViewById(R.id.monthlylist);
        leaderboard_points = (TextView) v.findViewById(R.id.leaderboard_points);
        leaderboardlable = (TextView) v.findViewById(R.id.leaderboardlable);
        emptycontent = (TextView) v.findViewById(R.id.emptycontent);

        mLayoutManager = new LinearLayoutManager(getActivity());
        monthlylist.setLayoutManager(mLayoutManager);
        mHandler = new Handler(Looper.getMainLooper());
        monthlylist.setRefreshListener(Mypointsfragment.this);
        monthlylist.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);

        monlyadapter = new Monthlypoints_Adapter(Mypointsfragment.this, monthlyarraylist);
        monthlylist.setAdapter(monlyadapter);

         /*monthlylist.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                // Fetch more from Api or DB
                if (internetChecking.isConnectingToInternet()) {
                    pagecount++;
                    new MypointServercall().execute(DomainUrl.MYPOINTS + "/" + userid+"/"+gameid);
                }
            }
        }, 3);*/

        monthlylist.setVisibility(View.GONE);
        emptymypoints.setVisibility(View.GONE);

        for (String point : points) {
            MypointsModel m = new MypointsModel();
            m.setDailypoints(point);
            arrayList.add(m);
        }

        userid = preferences.getString("ipredict_userid", null);
        startAnim();

        if (internetChecking.isConnectingToInternet()) {
            new MypointServercall().execute(DomainUrl.MYPOINTS + "/" + userid + "/" + gameid);
        }

        emptycontent.setTypeface(typeface);
        leaderboard_points.setTypeface(typeface);
        leaderboardlable.setTypeface(typeface);
        return v;
    }

    void startAnim() {
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim() {
        avi.hide();
        // or avi.smoothToHide();
    }

    @Override
    public void onRefresh() {
        pagecount = 0;
        if (internetChecking.isConnectingToInternet()) {
            new MypointServercall().execute(DomainUrl.MYPOINTS + "/" + userid + "/" + gameid);
        }
    }

    @Override
    public boolean canDismiss(int position) {
        return false;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {

    }

    private class MypointServercall extends AsyncTask<String, Void, String> {
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
                    JSONObject response = new JSONObject(s);
                    if (pagecount == 0) {
                        monlyadapter.clear();
                    }
                    JSONArray array = new JSONArray(response.getString("points"));
                    if (array.length() != 0) {
                        emptymypoints.setVisibility(View.GONE);
                        for (int i = 0; i < array.length(); i++) {
                            MypointsModel m = new MypointsModel();
                            m.setDate_text(array.getJSONObject(i).getString("day"));
                            m.setDailypoints(array.getJSONObject(i).getString("score"));
                            monlyadapter.add(m);
                        }
                    } else {
                        emptymypoints.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        stopAnim();
                    }
                }, GlobalActivity.delaytime);

            }
            monthlylist.hideMoreProgress();
            monthlylist.hideProgress();
        }
    }

}
