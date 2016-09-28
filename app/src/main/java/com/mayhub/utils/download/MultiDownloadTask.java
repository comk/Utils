package com.mayhub.utils.download;

import java.util.List;
import java.util.Vector;

/**
 * Created by Administrator on 2016/9/2.
 */
public class MultiDownloadTask extends DownloadTask {

    private Vector<String> downloadUrls = new Vector<>();
    private String downloadUrl;
    private DownloadListener downloadListener;
    private String destDir;
    private String fileName;
    private String cacheFileName;
    private volatile int currentIdx = -1;

    public MultiDownloadTask(List<String> downloadUrls, DownloadListener downloadListener, String destDir) {
        this.downloadUrls.addAll(downloadUrls);
        this.downloadListener = downloadListener;
        this.destDir = destDir;
    }

    public int getCurrentIndex(){
        return currentIdx;
    }

    public int getTotal(){
        return downloadUrls.size();
    }

    public List<String> getDownloadUrls(){
        return downloadUrls;
    }

    @Override
    public void prepareUrlFail() {
        currentIdx --;
    }

    public boolean prepareNextUrl(){
        currentIdx ++;
        if(currentIdx == downloadUrls.size()){
            return false;
        }else{
            downloadUrl = downloadUrls.get(currentIdx);
            fileName = getUrlFileName(downloadUrl);
            cacheFileName = getCacheFileName(fileName);
            return true;
        }
    }

    @Override
    public String getCacheFileName() {
        return cacheFileName;
    }

    @Override
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getDestDir() {
        return destDir;
    }

    @Override
    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiDownloadTask that = (MultiDownloadTask) o;

        return (getTag() != null && getTag().equals(that.getTag())) ||
                (downloadUrls.size() == that.getTotal() &&
                downloadListener == that.getDownloadListener() &&
                isUrlsEqual(that));
    }

    private boolean isUrlsEqual(MultiDownloadTask that){
        for (int i = 0; i < downloadUrls.size(); i++) {
            if(!downloadUrls.get(i).equals(that.getDownloadUrls().get(i))){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return downloadListener.hashCode();
    }
}
