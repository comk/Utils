package com.mayhub.utils.exp.dict;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/4.
 */

public class WordAnalaysisBean {

    /**
     * 读音
     */
    private String j;

    /**
     * 中文解释
     */
    private String c;





    /**
     * 比较 compare
     */
    private String cp;

    private List<SampleSentenceBean> s = new ArrayList<>(0);





    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getJ() {
        return j;
    }

    public void setJ(String j) {
        this.j = j;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public List<SampleSentenceBean> getS() {
        return s;
    }

    public void setS(List<SampleSentenceBean> s) {
        this.s = s;
    }
}
