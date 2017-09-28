package com.ipredictfantasy.adapter;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipredictfantasy.activity.LeadercontentFragment;
import com.ipredictfantasy.dto.LeaderboardModel;
import com.ipredictfantasy.R;

import java.util.ArrayList;

/**
 * Created by anbu0 on 22/03/2016.
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private Fragment context;
    private Typeface typeface, typefacebold;
    private ArrayList<LeaderboardModel> arrayList;

    public LeaderboardAdapter(LeadercontentFragment activity, ArrayList<LeaderboardModel> arrayList) {
        this.context = activity;
        this.arrayList = arrayList;

        typeface = Typeface.createFromAsset(context.getActivity().getAssets(), "fonts/proximanova-reg.ttf");
        typefacebold = Typeface.createFromAsset(context.getActivity().getAssets(), "fonts/proximanova-bold.ttf");

        DisplayMetrics dm = new DisplayMetrics();
        context.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_adapterlayout, parent, false);
        return new LeaderboardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(context.getActivity(), R.anim.slidetoptobottom);
        animation.setDuration(500);
        holder.manimlayout.startAnimation(animation);

        LeaderboardModel m = (LeaderboardModel) arrayList.get(position);
        holder.playername.setText(m.getPlayername());
        holder.playerpoints.setText(m.getPlayerpoints());

        holder.playername.setTypeface(typeface);
        holder.playerpoints.setTypeface(typeface);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void add(LeaderboardModel category) {
        insert(category, arrayList.size());
    }

    private void insert(LeaderboardModel category, int count) {
        arrayList.add(count, category);
        notifyItemInserted(count);
    }

    public ArrayList<LeaderboardModel> getlist() {
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
        TextView playername, playerpoints;
        CardView manimlayout;

        ViewHolder(View convertView) {
            super(convertView);
            manimlayout = (CardView) convertView.findViewById(R.id.manimlayout);
            playername = (TextView) convertView.findViewById(R.id.name);
            playerpoints = (TextView) convertView.findViewById(R.id.points);
        }
    }
}
