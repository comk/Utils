package com.mayhub.utils.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Administrator on 2016/6/22.
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentResId());
    }

    public abstract int getContentResId();
}
