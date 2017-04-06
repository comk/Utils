package com.mayhub.utils.common;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by Administrator on 2017/3/19.
 */

public class ThreadUtils {

    private static ThreadUtils instance;

    private Handler workHandler;

    private Handler uiHandler;

    private Thread thread;

    private ThreadUtils(){
        synchronized (ThreadUtils.class) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    workHandler = new Handler(Looper.myLooper());
                    Looper.loop();
                }
            });
            thread.start();
            uiHandler = new Handler(Looper.getMainLooper());
            int wordHandlerInitTime = 0;
            while (workHandler == null) {
                if(wordHandlerInitTime > 1000){
                    break;
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wordHandlerInitTime += 20;
                //do nothing just waiting thread alive
            }
        }
    }

    public static ThreadUtils getInstance(){
        if(instance == null){
            synchronized (ThreadUtils.class) {
                if(instance == null){
                    instance = new ThreadUtils();
                }
            }
        }
        return instance;
    }

    public void startBackgroundWork(Runnable runnable){
        synchronized (ThreadUtils.class) {
            if (workHandler != null) {
                workHandler.post(runnable);
            }
        }
    }

    public Handler getWorkHandler() {
        return workHandler;
    }

    public void startUIWork(Runnable runnable){
        if(uiHandler != null){
            uiHandler.post(runnable);
        }
    }

    public void destroyThread(){
        synchronized (ThreadUtils.class) {
            if (thread != null && workHandler != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    workHandler.getLooper().quitSafely();
                } else {
                    workHandler.getLooper().quit();
                }
            }
        }
    }

}
