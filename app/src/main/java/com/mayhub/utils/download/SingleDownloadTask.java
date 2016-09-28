package com.mayhub.utils.download;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/9/2.
 */
public class SingleDownloadTask extends DownloadTask {

    public SingleDownloadTask(String downloadUrl, DownloadListener downloadListener, String destDir) {
        this(downloadUrl, downloadListener, destDir, null);
    }

    public SingleDownloadTask(String downloadUrl, DownloadListener downloadListener, String destDir, String fileName) {
        this.downloadUrl = downloadUrl;
        this.downloadListener = downloadListener;
        this.destDir = destDir;
        if(TextUtils.isEmpty(fileName)){
            this.fileName = getUrlFileName(downloadUrl);
        }else {
            this.fileName = fileName;
        }
        cacheFileName = getCacheFileName(this.fileName);
    }

    private String downloadUrl;
    private DownloadListener downloadListener;
    private String destDir;
    private String fileName;
    private String cacheFileName;

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

        DownloadTask that = (DownloadTask) o;

        return (getTag() != null &&  getTag().equals(that.getTag())) || downloadUrl.equals(that.getDownloadUrl());

    }

    @Override
    public int hashCode() {
        return downloadUrl.hashCode();
    }
}
