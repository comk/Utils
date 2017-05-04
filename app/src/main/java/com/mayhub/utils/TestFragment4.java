package com.mayhub.utils;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by comkdai on 2017/5/4.
 */
public class TestFragment4 extends Fragment {

    private static final String TAG = "TestFragment4";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView tv = new TextView(container.getContext());
        tv.setTextColor(Color.RED);
        tv.setText("FRAG _ " + TAG);
        return tv;
    }
}
