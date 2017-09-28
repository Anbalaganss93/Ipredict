package com.ipredictfantasy.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.ipredictfantasy.dto.CategoriesModel;
import com.ipredictfantasy.R;

import java.util.ArrayList;

/**
 * Created by anbu0 on 26/01/2016.
 */
public class Menu_nav_adapter extends BaseAdapter {
    private Context context;
    private ArrayList<CategoriesModel> arrayList;
    private LayoutInflater inflater;
    private Typeface typeface;

    public Menu_nav_adapter(FragmentActivity activity, ArrayList<CategoriesModel> arrayList) {
        this.context = activity;
        this.arrayList = arrayList;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova-reg.ttf");
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
        ImageView image;
        TextView text;
        convertView = inflater.inflate(R.layout.nav_adapter_layout, parent, false);
        image = (ImageView) convertView.findViewById(R.id.list_icons);
        text = (TextView) convertView.findViewById(R.id.list_text);

        CategoriesModel m = (CategoriesModel) arrayList.get(position);
        image.setImageResource(m.getNav_image());
        text.setText(m.getNav_text());
        text.setTextColor(ContextCompat.getColor(context, R.color.black));
        text.setTypeface(typeface);
        return convertView;
    }
}

