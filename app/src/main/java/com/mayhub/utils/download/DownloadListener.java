package com.mayhub.utils.download;

import java.io.File;

/**
 * Created by Administrator on 2016/9/2.
 */
public interface DownloadListener {
    void onPrepare(Object tag, String realUrl, int index, int total);

    void onStart(Object tag, String fileName, String realUrl, int fileLength, int index, int total);

    void onProgress(Object tag, String realUrl, int progress, int index, int total);

    void onCancel(Object tag, String realUrl, int progress, int index, int total);

    void onFinish(Object tag, File file, String realUrl, int index, int total);

    void onError(Object tag, int status, String error, File file, String realUrl, int index, int total);
}
