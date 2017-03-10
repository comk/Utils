package com.mayhub.utils.common;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by comkdai on 2017/3/6.
 */
public class TickerUtils {

    private static TickerUtils instance;

    private Handler handler;

    private volatile long count; //seconds

    private volatile boolean isPause = false;

    private volatile boolean isTicking = false;

    private TickerListener tickerListener;

    private static final Object lock = new Object();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lock) {
                if (!isPause) {
                    count++;
                    if (tickerListener != null) {
                        tickerListener.onTicker(count);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        }
    };

    public interface TickerListener {
        void onTicker(long count);
    }

    private TickerUtils(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    Looper.prepare();
                    handler = new Handler(Looper.myLooper());
                }
                Looper.loop();
            }
        }).start();
    }

    public static TickerUtils getInstance(){
        synchronized (lock) {
            if (instance == null) {
                synchronized (TickerUtils.class) {
                    if (instance == null) {
                        instance = new TickerUtils();
                    }
                }
            }
            return instance;
        }
    }

    public long getCount(){
        return count;
    }

    public boolean isTicking(){
        return isTicking;
    }

    public boolean isPause(){
        return isPause;
    }

    public void startTicker(){
        synchronized (lock) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);
            isTicking = true;
        }
    }

    public void startTicker(TickerListener tickerListener1){
        synchronized (lock) {
            tickerListener = tickerListener1;
            startTicker();
        }
    }

    public void stopTicker(){
        synchronized (lock) {
            handler.removeCallbacks(runnable);
            tickerListener = null;
            count = 0;
            isPause = false;
            isTicking = false;
        }
    }

    public void destroy(){
        synchronized (lock){
            tickerListener = null;
            if(handler != null) {
                handler.removeCallbacks(runnable);
                handler.getLooper().quit();
                handler = null;
            }
            instance = null;
        }
    }

    public void pauseTicker(){
        synchronized (lock) {
            isPause = true;
        }
    }

    public void resumeOrStart(){
        synchronized (lock) {
            isPause = false;
            if (!isTicking) {
                startTicker();
            }
        }
    }

}
