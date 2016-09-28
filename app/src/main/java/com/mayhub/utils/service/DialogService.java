package com.mayhub.utils.service;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.mayhub.utils.R;

/**
 * Created by Administrator on 2016/6/22.
 */
public class DialogService extends Service {

    public static final String TASK_PARAMS = "task_params";

    public static final String TASK_LOADING = "task_loading";

    public static final String TASK_SHOW_MSG = "task_show_msg";

    public static final String TASK_SHOW_MSG_TITLE = "task_show_msg_title";

    public static final String TASK_SHOW_MSG_CONTENT = "task_show_msg_content";

    public static final String TASK_SHOW_MSG_BTN = "task_show_msg_btn";

    private Dialog mDialog;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDialog = new Dialog(DialogService.this);
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra(TASK_PARAMS);
        View contentView = null;
        if(!TextUtils.isEmpty(action)) {
            contentView = createViewByTask(action, intent);
        }
        if(contentView != null){
            View decorView = mDialog.getWindow().getDecorView();
            decorView.setBackgroundResource(0);
            if(decorView instanceof ViewGroup){
                ((ViewGroup)decorView).removeAllViews();
                ((ViewGroup)decorView).removeAllViewsInLayout();
                ((ViewGroup)decorView).addView(contentView);
            }
            mDialog.show();
        }

        return START_NOT_STICKY;//super.onStartCommand(intent, flags, startId);
    }

    private View createViewByTask(String task, Intent intent){
        if (task.equals(TASK_LOADING)){
            return View.inflate(getApplicationContext(), R.layout.layout_loading, null);
        }else if(task.equals(TASK_SHOW_MSG)){
            View rootView = View.inflate(getApplicationContext(), R.layout.layout_show_msg, null);
            TextView content = (TextView) rootView.findViewById(R.id.tv_content);
            if(!TextUtils.isEmpty(intent.getStringExtra(TASK_SHOW_MSG_CONTENT))){
                content.setText(intent.getStringExtra(TASK_SHOW_MSG_CONTENT));
            }
            TextView btn = (TextView) rootView.findViewById(R.id.tv_sure_btn);
            if(!TextUtils.isEmpty(intent.getStringExtra(TASK_SHOW_MSG_BTN))){
                btn.setText(intent.getStringExtra(TASK_SHOW_MSG_BTN));
            }
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }
            });
            return rootView;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }
}
