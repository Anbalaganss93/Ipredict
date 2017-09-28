package com.ipredictfantasy.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipredictfantasy.adapter.LeaderboardAdapter;
import com.ipredictfantasy.dto.LeaderboardModel;
import com.ipredictfantasy.R;
import com.ipredictfantasy.utility.GlobalActivity;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by anbu0 on 22/03/2016.
 */
public class LeadercontentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks {
    ArrayList<LeaderboardModel> arrayList = new ArrayList<>();
    LeaderboardAdapter adapter;
    private static int pagecount = 0;
    private String gameid;
    SuperRecyclerView boardlist;
    Typeface typeface, typefacebold;
    TextView leaderboardcontent, myleaderboardscore;
    private LinearLayout emptyleaderboard;
    private AVLoadingIndicatorView avi;
    private InternetChecking internetChecking;
    private String userid;
    private String username;

    public static Fragment newInstance(String gameid) {
        LeadercontentFragment f = new LeadercontentFragment();
        Bundle localBundle = new Bundle(1);
        localBundle.putString("EXTRA_MESSAGE", gameid);
        f.setArguments(localBundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.leadearcontentfragment_layout, container, false);

        getActivity();
        SharedPreferences preferences = getActivity().getSharedPreferences("ipredict", Context.MODE_PRIVATE);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/proximanova-reg.ttf");
        typefacebold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/proximanova-bold.ttf");

        userid = preferences.getString("ipredict_userid", null);
        username = preferences.getString("ipredict_name", null);
        gameid = preferences.getString("gameid", "1");
        internetChecking = new InternetChecking(getActivity());

        leaderboardcontent = (TextView) v.findViewById(R.id.leaderboardcontent);
        myleaderboardscore = (TextView) v.findViewById(R.id.myleaderboardscore);
        emptyleaderboard = (LinearLayout) v.findViewById(R.id.emptyleaderboard);
        avi = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        boardlist = (SuperRecyclerView) v.findViewById(R.id.boardlist);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        boardlist.setLayoutManager(mLayoutManager);
        boardlist.setRefreshListener(LeadercontentFragment.this);
        boardlist.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        adapter = new LeaderboardAdapter(LeadercontentFragment.this, arrayList);
        boardlist.setAdapter(adapter);

        emptyleaderboard.setVisibility(View.GONE);
         /*boardlist.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                // Fetch more from Api or DB
                if (internetChecking.isConnectingToInternet()) {
                    pagecount++;
                     new LeaderboardCricket().execute(DomainUrl.LEADERBOARD);
                }
            }
        }, 3);*/

        myleaderboardscore.setTypeface(typeface);
        leaderboardcontent.setTypeface(typeface);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        startAnim();
        if (internetChecking.isConnectingToInternet()) {
            new LeaderboardCricket().execute(DomainUrl.LEADERBOARD + "/" + gameid + "/" + userid);
        }
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
            new LeaderboardCricket().execute(DomainUrl.LEADERBOARD + "/" + gameid + "/" + userid);
        }
    }

    @Override
    public boolean canDismiss(int position) {
        return false;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {

    }

    private class LeaderboardCricket extends AsyncTask<String, Void, String> {
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
                try {
                    Log.d("REsult", s);
                    if (pagecount == 0) {
                        adapter.clear();
                    }
                    JSONObject json = new JSONObject(s);
                    String allscore = json.getString("score");
                    String userpoint = json.getString("userpoint");

                    JSONArray array = new JSONArray(allscore);
                    if (array.length() != 0) {
                        emptyleaderboard.setVisibility(View.GONE);
                        myleaderboardscore.setVisibility(View.VISIBLE);
                        for (int i = 0; i < array.length(); i++) {
                            LeaderboardModel m = new LeaderboardModel();
                            m.setPlayername(array.getJSONObject(i).getString("username"));
                            m.setPlayerid(array.getJSONObject(i).getString("user_id"));
                            if (userid.equals(m.getPlayerid())) {
                                m.setPlayername(array.getJSONObject(i).getString("username") + "(You)");
                                int positionofplayer = i + 1;
                                myleaderboardscore.setText(username + "\n Leaderboard score - " + userpoint + "\n position - " + positionofplayer);
                            }
                            m.setPlayerpoints(array.getJSONObject(i).getString("point"));


                            adapter.add(m);
                        }
                    } else {
                        emptyleaderboard.setVisibility(View.VISIBLE);
                        myleaderboardscore.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                Toast.makeText(getActivity(), R.string.resulterror, Toast.LENGTH_SHORT).show();
            }
            stopAnim();
            boardlist.hideMoreProgress();
            boardlist.hideProgress();
        }
    }

}
