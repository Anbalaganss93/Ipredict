package com.ipredictfantasy.ui;

import android.graphics.Bitmap;

import com.commit451.nativestackblur.NativeStackBlur;
import com.squareup.picasso.Transformation;

/**
 * Created by UMMWDC001 on 3/1/2016.
 */
public class BlurTransformation implements Transformation {

    private int mBlurRadius;

    public BlurTransformation(int blurRadius) {
        mBlurRadius = blurRadius;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap bm = NativeStackBlur.process(source, mBlurRadius);
        source.recycle();
        return bm;
    }

    @Override
    public String key() {
        return getClass().getCanonicalName() + "-" + mBlurRadius;
    }
}