package com.mayhub.utils.common;

import android.text.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by comkdai on 2017/5/15.
 */
public class ReciteUtils {

    interface ReciteListener{
        void onRecite(String word, boolean isRight);
        void onIllegal(List<String> illegalList);
        boolean filter(String word);
    }

    private static ReciteUtils instance;

    private ArrayList<String> candidate = new ArrayList<>();

    private ArrayList<String> wrong = new ArrayList<>();

    private String reciteWord;

    private ReciteListener reciteListener;

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
        filterReciteList(reciteList);
        candidate.clear();
        candidate.addAll(reciteList);
    }

    private void filterReciteList(ArrayList<String> reciteList){
        if(reciteListener != null){
            List<String> illegalList = new ArrayList<>();
            for (String word :
                    reciteList) {
                if (reciteListener.filter(word)) {
                    illegalList.add(word);
                }
            }
            if(illegalList.size() > 0){
                reciteListener.onIllegal(illegalList);
                for (String word:illegalList) {
                    reciteList.remove(word);
                }
            }
        }
    }

    public void setReciteListener(ReciteListener reciteListener) {
        this.reciteListener = reciteListener;
    }

    public String reciteNext(){
        reciteWord = null;
        if(candidate.size() > 0){
            Collections.shuffle(candidate);
            reciteWord = candidate.get(candidate.size() - 1);
        }
        if(wrong.size() > 0){
            Collections.shuffle(wrong);
            reciteWord = wrong.get(wrong.size() - 1);
        }
        return reciteWord;
    }

    public String reciteWithGetNext(boolean isRight){
        final String word = reciteWord;
        candidate.remove(word);
        wrong.remove(word);
        String next = reciteNext();
        if(!isRight){
            wrong.add(word);
        }
        if(reciteListener != null){
            reciteListener.onRecite(word, isRight);
        }
        if(TextUtils.isEmpty(next)){
            return reciteNext();
        }else {
            return next;
        }
    }

}
