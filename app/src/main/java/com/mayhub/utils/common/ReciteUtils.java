package com.mayhub.utils.common;

import android.text.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by comkdai on 2017/5/15.
 */
public class ReciteUtils {

    private static ReciteUtils instance;

    private ArrayList<String> list = new ArrayList<>();

    private ArrayList<String> listAgain = new ArrayList<>();

    private ReciteUtils(){

    }

    public static ReciteUtils getInstance() {
        if(instance == null){
            synchronized (ReciteUtils.class){
                if(instance == null){
                    instance = new ReciteUtils();
                }
            }
        }
        return instance;
    }

    public void initReciteList(ArrayList<String> reciteList){
        list.clear();
        list.addAll(reciteList);
    }

    public String reciteNext(){
        if(list.size() > 0){
            Collections.shuffle(list);
            return list.get(list.size() - 1);
        }
        if(listAgain.size() > 0){
            Collections.shuffle(listAgain);
            return listAgain.get(listAgain.size() - 1);
        }
        return null;
    }

    public String reciteWithGetNext(String word, boolean isRight){
        list.remove(word);
        listAgain.remove(word);
        String next = reciteNext();
        if(!isRight){
            listAgain.add(word);
        }
        save(word, isRight);
        if(TextUtils.isEmpty(next)){
            return word;
        }else {
            return next;
        }
    }

    private void save(String word, boolean isRight){

    }

    static class ReciteBean{
        private String word;
        private int rightCount;
        private int wrongCount;
        private float lastAccuracy;
    }

}
