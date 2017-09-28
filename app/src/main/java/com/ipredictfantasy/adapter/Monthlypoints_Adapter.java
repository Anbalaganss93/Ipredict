package com.ipredictfantasy.adapter;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipredictfantasy.activity.Mypointsfragment;
import com.ipredictfantasy.dto.MypointsModel;
import com.ipredictfantasy.R;

import java.util.ArrayList;

/**
 * Created by anbu0 on 26/03/2016.
 */
public class Monthlypoints_Adapter extends RecyclerView.Adapter<Monthlypoints_Adapter.ViewHolder> {
    private Fragment context;
    private Typeface typeface, typefacebold;
    private ArrayList<MypointsModel> arrayList;

    public Monthlypoints_Adapter(Mypointsfragment activity, ArrayList<MypointsModel> monthlyarraylist) {
        this.context = activity;
        this.arrayList = monthlyarraylist;

        DisplayMetrics dm = new DisplayMetrics();

        context.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        typeface = Typeface.createFromAsset(context.getActivity().getAssets(), "fonts/proximanova-reg.ttf");
        typefacebold = Typeface.createFromAsset(context.getActivity().getAssets(), "fonts/proximanova-bold.ttf");

        int width = dm.widthPixels;
        int height = dm.heightPixels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.montlyadapter_layout, parent, false);
        return new Monthlypoints_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(context.getActivity(), R.anim.slidetoptobottom);
        animation.setDuration(500);
        holder.manimlayout.startAnimation(animation);

        MypointsModel m = (MypointsModel) arrayList.get(position);
        holder.date.setText(m.getDate_text());
        holder.date.setTypeface(typeface);
        holder.points.setTypeface(typefacebold);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void add(MypointsModel category) {
        insert(category, arrayList.size());
    }

    private void insert(MypointsModel category, int count) {
        arrayList.add(count, category);
        notifyItemInserted(count);
    }

    public ArrayList<MypointsModel> getlist() {
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
        TextView date, points;
        LinearLayout manimlayout;

        ViewHolder(View convertView) {
            super(convertView);
            manimlayout = (LinearLayout) convertView.findViewById(R.id.manimlayout);
            date = (TextView) convertView.findViewById(R.id.datetext);
            points = (TextView) convertView.findViewById(R.id.daypoints);
        }
    }

}