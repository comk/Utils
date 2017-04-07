package com.mayhub.utils.common;

import android.util.SparseBooleanArray;

/**
 * Created by comkdai on 2017/4/7.
 */
public class ScoreUtils {

    private SparseBooleanArray result = new SparseBooleanArray(120);

    private static ScoreUtils instance;

    private ScoreUtils(){

    }

    public static ScoreUtils getInstance() {
        if(instance == null){
            synchronized (ScoreUtils.class){
                if(instance == null){
                    instance = new ScoreUtils();
                }
            }
        }
        return instance;
    }


}
