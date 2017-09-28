package com.ipredictfantasy.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import android.widget.TextView;

import com.ipredictfantasy.activity.MycompetitionActivity;
import com.ipredictfantasy.activity.QuestionActivity;
import com.ipredictfantasy.dto.MatchesModel;
import com.ipredictfantasy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by anbu0 on 31/07/2016.
 */
public class MatchlistAdapter extends RecyclerView.Adapter<MatchlistAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MatchesModel> arrayList;
    private int height;
    private Typeface typeface, typefacebold;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public MatchlistAdapter(FragmentActivity activity, ArrayList<MatchesModel> arrayList) {
        this.context = activity;
        this.arrayList = arrayList;

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        height = dm.heightPixels;

        preferences = context.getSharedPreferences("ipredict", MODE_PRIVATE);
        editor = preferences.edit();

        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova-reg.ttf");
        typefacebold = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova-bold.ttf");
    }

    @Override
    public MatchlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.matche_adapter_layout, parent, false);
        context = parent.getContext();
        return new MatchlistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slidetoptobottom);
        animation.setDuration(500);
        holder.matchcontainer.startAnimation(animation);

        holder.event.setVisibility(View.GONE);

        final MatchesModel m = (MatchesModel) arrayList.get(position);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = (int) (height * 0.21);
        holder.container.setLayoutParams(params);
        holder.match_name.setText(m.getMatches_name());
        holder.tornament_name.setText(m.getTournament_name());

        holder.timeday.setText(R.string.comingsoon);
        holder.timeday.setTextColor(Color.parseColor("#33D1AD"));
        holder.matchdate.setText(m.getMatch_day() + "," + m.getMatch_time());
        holder.date.setText(m.getMatch_date());
        holder.month.setText(m.getMatch_month());

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.date.setTextColor(color);

        holder.match_name.setTypeface(typefacebold);
        holder.tornament_name.setTypeface(typeface);
        holder.matchdate.setTypeface(typeface);
        holder.date.setTypeface(typefacebold);
        holder.month.setTypeface(typeface);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.matchimage.setClipToOutline(true);
        }

        Picasso.with(context).load(m.getMatches_logo()).placeholder(R.drawable.dhoni).into(holder.matchimage);
        holder.matchcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatchesModel m = (MatchesModel) arrayList.get(holder.getAdapterPosition());
                if (m.getMatch_answered().equals("0")) {
                    editor = preferences.edit();
                    editor.putString("matchid", m.getMatch_id());
                    editor.remove("fromcompetition");
                    editor.apply();

                    Intent intent = new Intent(context, QuestionActivity.class);
                    context.startActivity(intent);
                } else {
                    MatchcompletedDialog();
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

    public void add(MatchesModel category) {
        insert(category, arrayList.size());
    }

    private void insert(MatchesModel category, int count) {
        arrayList.add(count, category);
        notifyItemInserted(count);
    }

    public ArrayList<MatchesModel> getlist() {
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

    private void MatchcompletedDialog() {
        DisplayMetrics matrix = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(matrix);
        int width = matrix.widthPixels;

        String name = preferences.getString("ipredict_name", "User");

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.matchcompletedlayout);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView connectiontext = (TextView) dialog.findViewById(R.id.connectiontext);
        TextView internetcontent = (TextView) dialog.findViewById(R.id.internetcontent);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (width * 0.9);
        internetcontent.setLayoutParams(params);

        internetcontent.setText("Hai " + name + ",All the questions are answered.if you want to edit please go to Mycompition page.");
        Button ok = (Button) dialog.findViewById(R.id.ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MycompetitionActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView match_name, tornament_name, date, matchdate, month, timeday, event;
        FrameLayout container;
        ImageView matchimage;
        LinearLayout datecontainer, timcontainer, matchcontainer;

        ViewHolder(View convertView) {
            super(convertView);
            match_name = (TextView) convertView.findViewById(R.id.matchname);
            tornament_name = (TextView) convertView.findViewById(R.id.tornamentname);
            matchdate = (TextView) convertView.findViewById(R.id.matchdate);
            matchimage = (ImageView) convertView.findViewById(R.id.matchimage);
            date = (TextView) convertView.findViewById(R.id.date);
            month = (TextView) convertView.findViewById(R.id.month);
            timcontainer = (LinearLayout) convertView.findViewById(R.id.timcontainer);
            matchcontainer = (LinearLayout) convertView.findViewById(R.id.matchcontainer);
            timeday = (TextView) convertView.findViewById(R.id.days);
            event = (TextView) convertView.findViewById(R.id.tvevent);
            container = (FrameLayout) convertView.findViewById(R.id.container);
            datecontainer = (LinearLayout) convertView.findViewById(R.id.datecontainer);
        }
    }
}

