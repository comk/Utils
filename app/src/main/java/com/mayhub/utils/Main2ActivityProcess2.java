package com.mayhub.utils;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mayhub.utils.common.LocalValueUtils;

public class Main2ActivityProcess2 extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_activity_process2);
        tv = (TextView) findViewById(R.id.tv);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalValueUtils.initInstance(getApplicationContext());
                tv.setText(LocalValueUtils.getInstance().getString("String"));
                LocalValueUtils.getInstance().save("String", "Main2ActivityProcess2");
                LocalValueUtils.destroyInstance();
            }
        });
    }

}
