package com.ipredictfantasy.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.ipredictfantasy.adapter.MycompetitionFragment_Adapter;
import com.ipredictfantasy.dto.MycompitionModel;
import com.ipredictfantasy.R;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
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

/**
 * Created by anbu0 on 26/03/2016.
 */
public class MycompetitionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks {
    public static int fragmentposition;
    private static int pagecount = 0;
    ArrayList<MycompitionModel> arrayList = new ArrayList<>();
    MycompetitionFragment_Adapter adapter;
    TextView daypoints, points, emptycontent;
    private LinearLayout emptycompetition;
    SuperRecyclerView boardlist;
    Typeface typefacebold,typeface;
    private static String gameid;
    private LinearLayoutManager mLayoutManager;
    private Handler mHandler;
    private String mdata, userid;
    private InternetChecking internetChecking;
    private AVLoadingIndicatorView avi;
    private SharedPreferences preferences;

    public static Fragment newInstance(String usergameid) {
        MycompetitionFragment f = new MycompetitionFragment();
        gameid = usergameid;
        Bundle localBundle = new Bundle(1);
        localBundle.putString("EXTRA_MESSAGE", usergameid);
        f.setArguments(localBundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mycompetitionfragmentlayout, (ViewGroup) null);

        typefacebold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/proximanova-bold.ttf");
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/proximanova-reg.ttf");
        preferences = getActivity().getSharedPreferences("ipredict", Context.MODE_PRIVATE);
        internetChecking = new InternetChecking(getActivity());

        userid = preferences.getString("ipredict_userid", null);
        avi = (AVLoadingIndicatorView) v.findViewById(R.id.avi);

        boardlist = (SuperRecyclerView) v.findViewById(R.id.mycompetitionlist);
        mLayoutManager = new LinearLayoutManager(getActivity());
        boardlist.setLayoutManager(mLayoutManager);
        mHandler = new Handler(Looper.getMainLooper());
        boardlist.setRefreshListener(MycompetitionFragment.this);
        boardlist.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);

        daypoints = (TextView) v.findViewById(R.id.daypoints);
        points = (TextView) v.findViewById(R.id.points);
        emptycompetition = (LinearLayout) v.findViewById(R.id.emptycompetition);
        emptycontent = (TextView) v.findViewById(R.id.emptycontent);

        daypoints.setTypeface(typefacebold);
        points.setTypeface(typefacebold);
        emptycontent.setTypeface(typeface);

        adapter = new MycompetitionFragment_Adapter(getActivity(), arrayList);
        boardlist.setAdapter(adapter);

       /* boardlist.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                // Fetch more from Api or DB
                if (internetChecking.isConnectingToInternet()) {
                    pagecount++;
                    new Mycompitionservercall().execute(new String[]{DomainUrl.MYCOMBITION});
                }
            }
        }, 3);*/

       /* boardlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (internetChecking.isConnectingToInternet()) {
                    Intent intent = new Intent(getActivity(), CompetitorScore.class);
                    startActivity(intent);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
                }
            }
        });*/

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (internetChecking.isConnectingToInternet()) {
            startAnim();
            new Mycompitionservercall().execute(DomainUrl.MYCOMBITION);
        }
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
    public boolean canDismiss(int position) {
        return false;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {

    }

    @Override
    public void onRefresh() {
        if (internetChecking.isConnectingToInternet()) {
            pagecount = 0;
            new Mycompitionservercall().execute(DomainUrl.MYCOMBITION);
        }
    }


    private class Mycompitionservercall extends AsyncTask<String, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String rm_url;
        String data;

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
            boardlist.hideMoreProgress();
            if (s != null) {
                try {
                    JSONObject json = new JSONObject(s);
                    if (pagecount == 0) {
                        adapter.clear();
                    }
                    if(json.has("match")) {
                        JSONArray array = new JSONArray(json.getString("match"));
                        if (array.length() != 0) {
                            emptycompetition.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                MycompitionModel m = new MycompitionModel();
                                m.setMycompition_match_id(array.getJSONObject(i).getString("match_id"));
                                m.setMycompition_matchlogo(array.getJSONObject(i).getString("matches_logo"));
                                m.setMycompition_matches_name(array.getJSONObject(i).getString("matches_name"));
                                m.setMycompition_set(array.getJSONObject(i).getString("set"));
                                m.setMycompition_matchstatus(array.getJSONObject(i).getString("match_completed_status"));
                                m.setMycompition_question_id(array.getJSONObject(i).getString("question_id"));
                                m.setMycompition_next_question_id(array.getJSONObject(i).getString("next_question_id"));
                                m.setMycompition_org_start_date(array.getJSONObject(i).getString("org_start_date"));
                                m.setMycompition_points(array.getJSONObject(i).getString("points"));
                                m.setMycompition_start_at(array.getJSONObject(i).getString("start_at"));
                                adapter.add(m);
                            }
                        } else {
                            emptycompetition.setVisibility(View.VISIBLE);
                        }
                    }else{
                        emptycompetition.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            stopAnim();
        }
    }
}
