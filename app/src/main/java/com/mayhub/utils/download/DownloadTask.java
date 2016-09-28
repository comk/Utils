package com.mayhub.utils.download;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/9/2.
 */
public abstract class DownloadTask {

    public DownloadTask(){

    }

    public DownloadTask(Object tag){
        this.tag = tag;
    }

    private Object tag;

    private boolean isCancel = false;

    private int processTimes = 0;

    protected static String getCacheFileName(String fileName){
        int index = fileName.lastIndexOf(".");
        return fileName.substring(0,index) + ".cache";
    }

    protected static String getUrlFileName(String url){
        if(!TextUtils.isEmpty(url)){
            int index = url.lastIndexOf("/");
            if(index > 0 && index < url.length() - 1){
                return url.substring(index + 1);
            }
        }
        return null;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }

    public int getProcessTimes() {
        return processTimes;
    }

    public void prepareUrlFail(){

    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    public boolean prepareNextUrl(){
        return true;
    }

    public int getCurrentIndex(){
        return 0;
    }

    public int getTotal(){
        return 0;
    }

    public void setProcessTimes(int processTimes) {
        this.processTimes = processTimes;
    }

    public void incrementProcessTimes(){
        processTimes++;
    }

    public abstract String getCacheFileName();

    public abstract String getDownloadUrl();

    public abstract String getFileName();

    public abstract String getDestDir();

    public abstract DownloadListener getDownloadListener();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
