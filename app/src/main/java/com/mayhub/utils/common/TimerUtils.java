package com.mayhub.utils.common;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/25.
 */
public class TimerUtils {

    private static final String TAG = TimerUtils.class.getSimpleName();

    private static volatile TimerUtils instance;

    /**
     * 有效时间池
     */
    private HashMap<Object, Long> fixedTime = new HashMap<>();

    /**
     * 活跃时间池
     */
    private HashMap<Object, Long> activeTime = new HashMap<>();

    private volatile boolean isTicking = false;

    private volatile Object lastTickerObj;

    private volatile boolean enable = false;

    private volatile long startTime;

    private TimerUtils() {

    }

    public static TimerUtils getInstance() {
        if (instance == null) {
            synchronized (TimerUtils.class) {
                if (instance == null) {
                    instance = new TimerUtils();
                }
            }
        }
        return instance;
    }

    public String getTimer(int sec) {
        String timeContent = "0秒";
        if (sec > 0) {
            int min = sec / 60;
            sec = sec % 60;
            if (min > 0) {
                timeContent = min + "分钟" + sec + "秒";
            } else {
                timeContent = sec + "秒";
            }
        }
        return timeContent;
    }

    public void setEnable(boolean enable1) {
        enable = enable1;
    }

    /**
     * 开始对某一个标记进行计时
     * @param tag 进行计时的标记
     */
    public void startTicker(Object tag) {
        synchronized (TimerUtils.class) {
            if (!enable) {
                return;
            }
            if (isTicking) {
                stopTicker();
            }
            isTicking = true;
            lastTickerObj = tag;
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * 是否正在计时
     * @return
     */
    public boolean isTicking() {
        return isTicking;
    }

    /**
     * 继续计时
     */
    public void resumeTicker() {
        if (lastTickerObj != null) {
            startTicker(lastTickerObj);
        }
    }

    /**
     * 对当前标记停止计时
     */
    public void stopTicker() {
        synchronized (TimerUtils.class) {
            if (!enable) {
                return;
            }
            if (lastTickerObj == null) {
                return;
            }
            isTicking = false;
            long duration = System.currentTimeMillis() - startTime;
            if (activeTime.get(lastTickerObj) == null) {
                activeTime.put(lastTickerObj, duration);
            } else {
                activeTime.put(lastTickerObj, activeTime.get(lastTickerObj) + duration);
            }
        }
    }

    /**
     * 将标记的时间从有效时间中移除，加入活跃时间重开始新的计时
     * @param tag
     */
    public void removeFromFixedAndStart(Object tag) {
        synchronized (TimerUtils.class) {
            if (fixedTime.get(tag) != null) {
                long time = fixedTime.remove(tag);
                if (activeTime.get(lastTickerObj) == null) {
                    activeTime.put(lastTickerObj, time);
                } else {
                    activeTime.put(lastTickerObj, activeTime.get(lastTickerObj) + time);
                }
                startTicker(tag);
            }
        }
    }

    /**
     * 停止计时并记入有效时间池中
     */
    public void stopAndMoveToFixed() {
        synchronized (TimerUtils.class) {
            if (isTicking) {
                stopTicker();
            }
            moveToFixState(lastTickerObj);
        }
    }

    /**
     * 获取某一个标记的有效时间
     * @param tag 标记
     * @return 标记的时间
     */
    public int getFixedTimeByTag(Object tag) {
        if (fixedTime.get(tag) != null) {
            return (int) (fixedTime.get(tag) / 1000);
        }
        return 0;
    }

    /**
     * 将标记的时间移入有效时间，并活跃时间重新开始计时
     * @param tag
     */
    public void moveToFixStateAndRestart(Object tag) {
        moveToFixState(tag);
        startTicker(tag);
    }

    /**
     * 将标记的时间移入有效时间
     * @param tag
     */
    public void moveToFixState(Object tag) {
        synchronized (TimerUtils.class) {
            if (isTicking) {
                stopTicker();
            }
            if (fixedTime.get(tag) == null) {
                fixedTime.put(tag, activeTime.get(tag));
            } else {
                fixedTime.put(tag, fixedTime.get(tag) + activeTime.get(tag));
            }
            activeTime.remove(tag);
        }
    }

    /**
     * 终止计时，并将所有的时间都记入有效时间
     */
    public void terminate(){
        if(isTicking){
            stopTicker();
        }
        moveAllActiveIntoFixed();
    }

    /**
     * 将活跃时间中的值都加入到有效时间中
     */
    public void moveAllActiveIntoFixed(){
        synchronized (TimerUtils.class) {
            for (Object tag : activeTime.keySet()) {
                if (fixedTime.get(tag) == null) {
                    fixedTime.put(tag, activeTime.get(tag));
                } else {
                    fixedTime.put(tag, fixedTime.get(tag) + activeTime.get(tag));
                }
            }
            activeTime.clear();
        }
    }

    public void clear() {
        synchronized (TimerUtils.class) {
            isTicking = false;
            activeTime.clear();
            fixedTime.clear();
            lastTickerObj = null;
            startTime = 0;
            enable = false;
        }
    }

}
