package com.ipredictfantasy.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ipredictfantasy.activity.Mypointsfragment;
import com.ipredictfantasy.dto.MypointsModel;
import com.ipredictfantasy.R;

import java.util.ArrayList;

/**
 * Created by anbu0 on 24/03/2016.
 */
public class Myfragmentpoints_Adapter extends BaseAdapter {
    private Fragment context;
    private Typeface typeface, typefacebold;
    private ArrayList<MypointsModel> arrayList;
    private LayoutInflater inflater;

    public Myfragmentpoints_Adapter(Mypointsfragment activity, ArrayList<MypointsModel> arrayList) {
        this.context = activity;
        this.arrayList = arrayList;

        typeface = Typeface.createFromAsset(context.getActivity().getAssets(), "fonts/proximanova-reg.ttf");
        typefacebold = Typeface.createFromAsset(context.getActivity().getAssets(), "fonts/proximanova-bold.ttf");

        inflater = (LayoutInflater) context.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DisplayMetrics dm = new DisplayMetrics();
        context.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.daily_adapterlayout, parent, false);
            holder.manimlayout = (FrameLayout) convertView.findViewById(R.id.manimlayout);
            holder.point = (TextView) convertView.findViewById(R.id.point);
            holder.total = (TextView) convertView.findViewById(R.id.total);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.pointscored = (TextView) convertView.findViewById(R.id.pointscored);
            holder.bonuspoints = (TextView) convertView.findViewById(R.id.bonuspoints);
            holder.bonuspercentage = (TextView) convertView.findViewById(R.id.bonuspercentage);
            holder.totallable = (TextView) convertView.findViewById(R.id.totallable);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Animation anim = AnimationUtils.loadAnimation(context.getActivity(), R.anim.slideinright);
        holder.manimlayout.setAnimation(anim);

        MypointsModel m = (MypointsModel) arrayList.get(position);
        holder.point.setText(m.getDailypoints());

        holder.date.setTypeface(typefacebold);
        holder.point.setTypeface(typeface);
        holder.pointscored.setTypeface(typeface);
        holder.bonuspoints.setTypeface(typeface);
        holder.bonuspercentage.setTypeface(typeface);
        holder.total.setTypeface(typefacebold);
        holder.totallable.setTypeface(typefacebold);

        return convertView;
    }

    public class ViewHolder {
        TextView point, total, date, pointscored, bonuspoints, bonuspercentage, totallable;
        FrameLayout manimlayout;
    }

}