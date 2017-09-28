package com.ipredictfantasy.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.ipredictfantasy.activity.DomainUrl;
import com.ipredictfantasy.activity.InternetChecking;
import com.ipredictfantasy.activity.LeaderBoard;
import com.ipredictfantasy.activity.MatchsActivity;
import com.ipredictfantasy.activity.MyPointsActivity;
import com.ipredictfantasy.activity.MycompetitionActivity;
import com.ipredictfantasy.activity.Profile;
import com.ipredictfantasy.activity.SupportActivity;
import com.ipredictfantasy.activity.WelcomeScreen;
import com.ipredictfantasy.adapter.Menu_nav_adapter;
import com.ipredictfantasy.dto.CategoriesModel;
import com.ipredictfantasy.R;
import com.ipredictfantasy.ui.ImageCirclewithborder;
import com.mxn.soul.flowingdrawer_core.MenuFragment;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by UMMWDC001 on 5/13/2016.
 */
public class FlowFragment extends MenuFragment {
    private ArrayList<CategoriesModel> arrayList = new ArrayList<>();
    private InternetChecking internetChecking;
    private int nav_images[] = {R.drawable.profile, R.drawable.comp_icon, R.drawable.mypointsicon, R.drawable.leaderboardicon, R.drawable.howtoplayicon, R.drawable.rateselected, R.drawable.logout};// R.drawable.bonusicon,R.drawable.storeicon,
    private String nav_text[] = {"My Profile", "My Competition", "My Points", "Leaderboard", "Help", "Rate us", "Signout"}; //"Bonus Points", "Store",
    private SharedPreferences preferences;
    private String image;
    private ImageCirclewithborder profileimage;
    private String userid;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flowlayout, container, false);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/proximanova-reg.ttf");
        Typeface typefacebold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/proximanova-bold.ttf");

        getActivity();
        preferences = getActivity().getSharedPreferences("ipredict", Context.MODE_PRIVATE);
        String name = preferences.getString("ipredict_name", "");
        userid = preferences.getString("ipredict_userid", null);
        String email = preferences.getString("email", null);
        image = preferences.getString("ipredict_userimage", null);

        internetChecking = new InternetChecking(getActivity());
        GridViewWithHeaderAndFooter gridview = (GridViewWithHeaderAndFooter) view.findViewById(R.id.gridview);
        TextView username = (TextView) view.findViewById(R.id.username);
        TextView userlocation = (TextView) view.findViewById(R.id.userlocation);
        username.setTypeface(typeface);
        userlocation.setTypeface(typeface);

        profileimage = (ImageCirclewithborder) view.findViewById(R.id.profilepic);

        try {
            Picasso.with(getActivity()).load(image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.profile_default_yellow).into(profileimage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (internetChecking.isConnectingToInternet()) {
                    Intent mprofile = new Intent(getActivity(), Profile.class);
                    startActivity(mprofile);
                }
            }
        });

        for (int i = 0; i < nav_text.length; i++) {
            CategoriesModel m = new CategoriesModel();
            m.setNav_image(nav_images[i]);
            m.setNav_text(nav_text[i]);
            arrayList.add(m);
        }
        username.setText(name);
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        Button referfriend = (Button) view.findViewById(R.id.referfriend);

        referfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey there! This is one of the best poker app,I have ever came across.link:www.ipredictfantasy.com");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        if (gridview != null) {
            gridview.setAdapter(new Menu_nav_adapter(getActivity(), arrayList));
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()) {
                    case R.id.gridview:
                        switch (position) {
                            case 0:
                                MatchsActivity.drawerLayout.toggle();
                                if (internetChecking.isConnectingToInternet()) {
                                    Intent mprofile = new Intent(getActivity(), Profile.class);
                                    startActivity(mprofile);
                                }
                                break;
                            case 1:
                                MatchsActivity.drawerLayout.toggle();
                                if (internetChecking.isConnectingToInternet()) {
                                    Intent mcompetition = new Intent(getActivity(), MycompetitionActivity.class);
                                    startActivity(mcompetition);
                                }
                                break;
                            case 2:
                                MatchsActivity.drawerLayout.toggle();
                                if (internetChecking.isConnectingToInternet()) {
                                    Intent mpoints = new Intent(getActivity(), MyPointsActivity.class);
                                    startActivity(mpoints);
                                }
                                break;
                            case 3:
                                MatchsActivity.drawerLayout.toggle();
                                if (internetChecking.isConnectingToInternet()) {
                                    Intent mleaderboard = new Intent(getActivity(), LeaderBoard.class);
                                    startActivity(mleaderboard);
                                }
                                break;
                            case 4:
                                MatchsActivity.drawerLayout.toggle();
                                if (internetChecking.isConnectingToInternet()) {
                                    Intent howtoplayintent = new Intent(getActivity(), SupportActivity.class);
                                    startActivity(howtoplayintent);
                                }
                                break;
                            case 5:
                                MatchsActivity.drawerLayout.toggle();
                                if (internetChecking.isConnectingToInternet()) {
                                    try {
                                        final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case 6:
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.remove("userlogout");
                                editor.apply();
                                if (internetChecking.isConnectingToInternet()) {
                                    new GCMUpdate().execute(DomainUrl.GCM_UPDATE);
                                }

                                Intent intent = new Intent(getActivity(), WelcomeScreen.class);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
                                getActivity().finish();
                                break;
                        }
                        break;
                }
            }
        });
        return setupReveal(view);
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
            String mdata = null;
            try {
                JSONObject regJsonObject = new JSONObject();
                regJsonObject.put("google_reg_id", "");
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

    @Override
    public void onResume() {
        super.onResume();
        image = preferences.getString("ipredict_userimage", null);
        Picasso.with(getActivity()).load(image)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.profiledefaultimg).into(profileimage);
    }
}
