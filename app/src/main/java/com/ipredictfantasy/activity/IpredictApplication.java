package com.ipredictfantasy.activity;

import android.app.Application;
import android.support.multidex.MultiDex;

/**
 * Created by anbu0 on 02/07/2017.
 */

public class IpredictApplication extends Application {
    public static IpredictApplication context;
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        context=this;
    }
}
