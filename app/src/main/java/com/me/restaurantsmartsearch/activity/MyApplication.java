package com.me.restaurantsmartsearch.activity;

import android.support.multidex.MultiDexApplication;

import com.me.restaurantsmartsearch.utils.Common;
import com.me.restaurantsmartsearch.utils.Utils;

/**
 * Created by Laptop88T on 11/15/2016.
 */
public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Common.screenSize = Utils.getDisplaySize(getApplicationContext());
    }
}
