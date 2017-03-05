package com.mayhub.utils.exp.dict;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/4.
 */

public class WordBean {

    /**
     * 单词
     */
    private String w;

    /**
     * 发音
     */
    private String p;

    /**
     * 单词释义
     */
    private String e;

    /**
     * 参考
     */
    private String r;

    /**
     * 语法 program
     */
    private String pg;

    /**
     * 注意 note
     */
    private String n;

    /**
     * 比较 compare
     */
    private String cp;

    /**
     * 词性
     */
    private List<WordProperty> o = new ArrayList<>(0);

    /**
     * 常用搭配
     */
    private List<WordBean> u = new ArrayList<>(0);

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getPg() {
        return pg;
    }

    public void setPg(String pg) {
        this.pg = pg;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public List<WordProperty> getO() {
        return o;
    }

    public void setO(List<WordProperty> o) {
        this.o = o;
    }

    public List<WordBean> getU() {
        return u;
    }

    public void setU(List<WordBean> u) {
        this.u = u;
    }
}
