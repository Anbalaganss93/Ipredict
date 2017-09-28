package com.ipredictfantasy.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ipredictfantasy.activity.MatchsActivity;
import com.ipredictfantasy.R;
import com.ipredictfantasy.interfaces.MymatchInterface;
import com.kingfisherphuoc.quickactiondialog.QuickActionDialogFragment;

/**
 * Created by anbu0 on 13/08/2016.
 */
public class MatchcategoryDialogue extends QuickActionDialogFragment {
    private SharedPreferences.Editor editor;
    private MymatchInterface mymatchInterface;
    private TextView mymatchtitle;

    @Override
    protected int getArrowImageViewId() {
        return R.id.ivArrow; //return 0; that mean you do not have an up arrow icon
    }

    @Override
    protected int getLayout() {
        return R.layout.matchcategorydialoguelayout;
    }

    @Override
    protected boolean isStatusBarVisible() {
        return true; //optional: if status bar is visible in your app
    }

    @Override
    protected boolean isCanceledOnTouchOutside() {
        return true; //optional
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        getActivity();
        SharedPreferences preferences = getActivity().getSharedPreferences("ipredict", Context.MODE_PRIVATE);
        editor = preferences.edit();
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/proximanova-reg.ttf");

        mymatchtitle = (TextView) getActivity().findViewById(R.id.toolbartext_back);
        TextView cricket = (TextView) view.findViewById(R.id.cricket);
        TextView football = (TextView) view.findViewById(R.id.football);

        cricket.setTypeface(typeface);
        football.setTypeface(typeface);

        cricket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("gameid", "1");
                editor.apply();
                mymatchtitle.setText(R.string.cricket);
                mymatchInterface.mymatchgameid("1");
                dismiss();
            }
        });
        football.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("gameid", "2");
                editor.commit();
                mymatchtitle.setText(R.string.football);
                mymatchInterface.mymatchgameid("2");
                dismiss();
            }
        });
        // Set listener, view, data for your dialog fragment

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mymatchInterface = (MymatchInterface) context;
    }
}