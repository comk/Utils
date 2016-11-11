package com.mayhub.utils;

import android.app.Application;

/**
 * Created by Administrator on 2016/11/1.
 */
public class MyApplication extends Application {

    private static MyApplication myApplication;

    public static MyApplication getInstance(){
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }
}
