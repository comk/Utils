package com.mayhub.utils.activity;

import android.app.Application;

import com.mayhub.utils.common.LocalValueUtils;

/**
 * Created by comkdai on 2017/4/6.
 */
public class App extends Application {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LocalValueUtils.initInstance(this);
    }
}
