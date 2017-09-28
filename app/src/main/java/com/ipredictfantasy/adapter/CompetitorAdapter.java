package com.ipredictfantasy.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipredictfantasy.activity.CompetitorScore;
import com.ipredictfantasy.dto.WinnerAnalistModel;
import com.ipredictfantasy.R;

import java.util.ArrayList;

/**
 * Created by anbu0 on 27/03/2016.
 */
public class CompetitorAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<WinnerAnalistModel> arrayList;
    private LayoutInflater inflater;
    private Typeface typeface;

    public CompetitorAdapter(CompetitorScore activity, ArrayList<WinnerAnalistModel> arrayList) {
        this.context = activity;
        this.arrayList = arrayList;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);

        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova-reg.ttf");
        Typeface typefacebold = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova-bold.ttf");
    }

    @Override
    public int getCount() {
        int count = 5;
        if (arrayList.size() != 0) {
            count = arrayList.size();
        }
        return count;
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
            convertView = inflater.inflate(R.layout.competitoradapter_layout, parent, false);
            holder.manimlayout = (RelativeLayout) convertView.findViewById(R.id.manimlayout);
            holder.question = (TextView) convertView.findViewById(R.id.question_text);
            holder.comp_answer = (TextView) convertView.findViewById(R.id.answer);
            holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
            holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
            holder.opponentanswer = (TextView) convertView.findViewById(R.id.opponentanswer);
            holder.questionicon = (ImageView) convertView.findViewById(R.id.questionicon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.slideinright);
        holder.manimlayout.setAnimation(anim);

        final WinnerAnalistModel m = (WinnerAnalistModel) arrayList.get(position);

        holder.question.setText(m.getQuestion());
        holder.textView1.setText(m.getUsername() + " :");
        String opponent_name=m.getOpponentname().trim().equals("") ? "Opponent" : m.getOpponentname();
        holder.textView2.setText(opponent_name + " :");
        if(m.getOpponentname().equals("")){
            holder.textView2.setText("Opponent :");
        }

        holder.comp_answer.setText(m.getUseranswer());
        holder.opponentanswer.setText(m.getOpponent_useranswer());
//        holder.question.setTextColor(Color.parseColor(m.getUsercolor()));
        holder.comp_answer.setTextColor(Color.parseColor(m.getUsercolor()));
        holder.opponentanswer.setTextColor(Color.parseColor(m.getOpponentcolor()));

        holder.question.setTypeface(typeface);
        holder.comp_answer.setTypeface(typeface);
        /*holder.questionicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = m.getQuestion();
                questionalert(question);
            }
        });*/

        return convertView;
    }

    private void questionalert(String question) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.questionalert_layout);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView content = (TextView) alertDialog.findViewById(R.id.content);
        content.setText(question);

        content.setTypeface(typeface);
        alertDialog.show();
    }

    private class ViewHolder {
        TextView question, comp_answer, opponentanswer, textView1, textView2;
        RelativeLayout manimlayout;
        ImageView questionicon;
    }
}
