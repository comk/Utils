package com.mayhub.utils.common;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Administrator on 2016/10/28.
 */
public class TimeCostUtils {

    private static final String TAG = TimeCostUtils.class.getSimpleName();

    private static volatile TimeCostUtils instance;

    private long lastStartTime;

    private String lastTag;

    private TimeCostUtils(){

    }

    public static TimeCostUtils getInstance(){
        if(instance == null){
            synchronized (TimeCostUtils.class){
                if(instance == null){
                    instance = new TimeCostUtils();
                }
            }
        }
        return instance;
    }

    public void clear(){
        if(lastStartTime > 0 && !TextUtils.isEmpty(lastTag)){
            Log.e(TAG, String.format("%s cost %s mills",lastTag,  System.currentTimeMillis() - lastStartTime));
        }
        lastStartTime = 0;
        lastTag = null;
    }

    public void startTicker(String tag){
        if(lastStartTime > 0 && !TextUtils.isEmpty(lastTag)){
            Log.e(TAG, String.format("%s cost %s mills",lastTag, System.currentTimeMillis() - lastStartTime));
        }
        lastStartTime = System.currentTimeMillis();
        lastTag = tag;
    }
}
