package com.ipredictfantasy.activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.ipredictfantasy.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class CroperActivity extends AppCompatActivity {
    private CropImageView cropimage;
    private TextView close;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_croper);

        SharedPreferences preferences = getSharedPreferences("ipredict", MODE_PRIVATE);
        editor = preferences.edit();

        try {
            cropimage = (CropImageView) findViewById(R.id.cropimage);
            cropimage.setImageBitmap(Profile.currentimage);
            cropimage.setFixedAspectRatio(true);
            cropimage.setAutoZoomEnabled(false);
            TextView done = (TextView) findViewById(R.id.done);
            close = (TextView) findViewById(R.id.close);

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Profile.encodedImage = "";
                    Bitmap cropped = cropimage.getCroppedImage();
                    Profile.currentimage = cropped;
                    converimgaetostring(cropped);
                    Profile.profileimage.setImageBitmap(cropped);
                    Profile.backprofile.setImageBitmap(cropped);
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void converimgaetostring(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            Profile.encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            editor.putString("base64image", Base64.encodeToString(b, Base64.DEFAULT));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
