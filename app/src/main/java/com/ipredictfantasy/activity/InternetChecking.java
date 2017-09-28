package com.ipredictfantasy.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipredictfantasy.R;


/**
 * Created by intel on 5/27/2016.
 */
public class InternetChecking {
    private Context _context;

    private Typeface bold, regular;
    SharedPreferences preferences;

    public InternetChecking(Context _context) {
        this._context = _context;

        regular = Typeface.createFromAsset(_context.getAssets(), "fonts/Aller_Rg.ttf");
        bold = Typeface.createFromAsset(_context.getAssets(), "fonts/Aller_Bd.ttf");


    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                return true;
            } else {
                openDialog();
            }
        }
        return false;
    }

    private void openDialog() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) _context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final Dialog dialog = new Dialog(IpredictApplication.context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.internetconnectionlayout);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView connectiontext = (TextView) dialog.findViewById(R.id.connectiontext);
        TextView internetcontent = (TextView) dialog.findViewById(R.id.internetcontent);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (width * 0.9);
        connectiontext.setLayoutParams(params);

        Button ok = (Button) dialog.findViewById(R.id.ok);

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        connectiontext.setTypeface(bold);
        internetcontent.setTypeface(regular);
        ok.setTypeface(bold);
        dialog.show();

    }
}
