package com.mayhub.utils.exp.dict;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/4.
 */

public class WordProperty {
    /**
     * 词性
     */
    private String p;

    /**
     * 词义
     */
    private List<WordAnalaysisBean> s = new ArrayList<>(0);

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public List<WordAnalaysisBean> getS() {
        return s;
    }

    public void setS(List<WordAnalaysisBean> s) {
        this.s = s;
    }
}
