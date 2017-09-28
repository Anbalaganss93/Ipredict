package com.ipredictfantasy.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipredictfantasy.activity.CompetitorScore;
import com.ipredictfantasy.activity.InternetChecking;
import com.ipredictfantasy.activity.QuestionActivity;
import com.ipredictfantasy.dto.MycompitionModel;
import com.ipredictfantasy.R;
import com.ipredictfantasy.ui.BlurTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by anbu0 on 26/03/2016.
 */
public class MycompetitionFragment_Adapter extends RecyclerView.Adapter<MycompetitionFragment_Adapter.ViewHolder> {
    private Context context;
    private Typeface typeface, typefacebold, typefacebull;
    private ArrayList<MycompitionModel> arrayList;
    private int mycompitioncheck;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private InternetChecking internetChecking;

    public MycompetitionFragment_Adapter(Context activity, ArrayList<MycompitionModel> arrayList) {
        this.context = activity;
        this.arrayList = arrayList;

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        internetChecking = new InternetChecking(context);

        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova-reg.ttf");
        typefacebold = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova-bold.ttf");
        typefacebull = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova-reg.ttf");

        preferences = context.getSharedPreferences("ipredict", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycompetitionfragment_adapter, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slidetoptobottom);
        animation.setDuration(500);
        holder.mlayout.startAnimation(animation);

        final MycompitionModel m = (MycompitionModel) arrayList.get(position);
        String points = "points: " + m.getMycompition_points();
        String set = "set: " + m.getMycompition_set();

        holder.mpoint.setText(points);
        holder.comp_match.setText(m.getMycompition_matches_name());
        holder.set.setText(set);

        Picasso.with(context).load(m.getMycompition_matchlogo()).transform(new BlurTransformation(18)).into(holder.img);

        holder.mpoint.setTypeface(typeface);
        holder.set.setTypeface(typeface);
        holder.comp_match.setTypeface(typefacebold);
        holder.group.setTypeface(typeface);
//        targettag();
        holder.mlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetChecking.isConnectingToInternet()) {
                    mycompitioncheck = 1;
                    if (!m.getMycompition_matchstatus().equals("1")) {

                        MycompitionModel m = (MycompitionModel) arrayList.get(holder.getAdapterPosition());
                        editor = preferences.edit();
                        editor.putString("playid", m.getMycompition_match_id());
                        editor.putString("userset", m.getMycompition_set());
                        editor.apply();

                        Intent intent = new Intent(context, CompetitorScore.class);
                        intent.putExtra("matchid", m.getMycompition_match_id());
                        context.startActivity(intent);
//                        ((Activity) context).finish();
//                        ((Activity) context).overridePendingTransition(R.anim.slidein, R.anim.slide_out_right);
                    } else {
                        MatchcompletedDialog();
                    }
                }
            }
        });


        holder.editicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetChecking.isConnectingToInternet()) {
                    mycompitioncheck = 2;
                    if (m.getMycompition_matchstatus().equals("1")) {

                        MycompitionModel m = (MycompitionModel) arrayList.get(holder.getAdapterPosition());
                        editor = preferences.edit();
                        editor.putString("matchid", m.getMycompition_match_id());
                        editor.putString("userset", m.getMycompition_set());
                        editor.putString("questionid", m.getMycompition_question_id());
                        editor.putString("fromcompetition","1");
                        editor.apply();

                        Intent intent = new Intent(context, QuestionActivity.class);
                        context.startActivity(intent);
                    } else {
                        MatchcompletedDialog();
                    }
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void add(MycompitionModel category) {
        insert(category, arrayList.size());
    }

    private void insert(MycompitionModel category, int count) {
        arrayList.add(count, category);
        notifyItemInserted(count);
    }

    public ArrayList<MycompitionModel> getlist() {
        return arrayList;
    }

    public void remove(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = arrayList.size();
        arrayList.clear();
        notifyItemRangeRemoved(0, size);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mpoint, set, comp_match, group;
        RelativeLayout mlayout;
        ImageView img, editicon;
        private FrameLayout competitor_fl_container;

        ViewHolder(View convertView) {
            super(convertView);
            mlayout = (RelativeLayout) convertView.findViewById(R.id.mlayout);
            competitor_fl_container = (FrameLayout) convertView.findViewById(R.id.competitor_fl_container);
            mpoint = (TextView) convertView.findViewById(R.id.mpoint);
            set = (TextView) convertView.findViewById(R.id.set);
            comp_match = (TextView) convertView.findViewById(R.id.comp_match);
            group = (TextView) convertView.findViewById(R.id.group);
            img = (ImageView) convertView.findViewById(R.id.img);
            editicon = (ImageView) convertView.findViewById(R.id.editicon);
        }
    }

    private void MatchcompletedDialog() {
        DisplayMetrics matrix = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(matrix);
        int width = matrix.widthPixels;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.matchcompletedlayout);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView connectiontext = (TextView) dialog.findViewById(R.id.connectiontext);
        TextView internetcontent = (TextView) dialog.findViewById(R.id.internetcontent);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (width * 0.9);
        params.setMargins(10, 10, 10, 10);
        internetcontent.setLayoutParams(params);

        if (mycompitioncheck == 1) {
            String mtemp = "You can't allow until Match started";
            internetcontent.setText(mtemp);
        } else {
            String mMatchstart = "Match already started.you can not enter into the match.";
            internetcontent.setText(mMatchstart);
        }

        Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        connectiontext.setTypeface(typeface);
        internetcontent.setTypeface(typeface);
        ok.setTypeface(typefacebold);

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
}
