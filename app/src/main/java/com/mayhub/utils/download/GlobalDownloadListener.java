package com.mayhub.utils.download;


/**
 * Created by Administrator on 2016/9/2.
 */
public interface GlobalDownloadListener {

    void onStart(DownloadTask downloadTask);

    void onProgress(DownloadTask downloadTask, int progress);

    void onCancel(DownloadTask downloadTask);

    void onFinish(DownloadTask downloadTask);

    void onError(DownloadTask downloadTask);
}
