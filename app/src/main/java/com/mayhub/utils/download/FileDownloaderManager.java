package com.mayhub.utils.download;

import android.content.Context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2016/6/30.
 */
public class FileDownloaderManager {
    public final static int DOWNLOAD_STATUS_DOWNLOADING = 0x101;
    public final static int DOWNLOAD_STATUS_ERROR = 0x102;
    public final static int DOWNLOAD_STATUS_FINISH = 0x103;
    public final static int DOWNLOAD_STATUS_WAITING = 0x104;
    public final static int DOWNLOAD_STATUS_CANCEL = 0x105;
    public final static int DOWNLOAD_STATUS_UNKNOWN = 0x106;
    private static final ConcurrentHashMap<String,FileDownloaderManager> INSTANCE_CONTAINER = new ConcurrentHashMap<>(16);
    private final ConcurrentHashMap<Object,Integer> TASK_STATUS_CACHE = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Object,Integer> TASK_PROGRESS_CACHE = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Object,DownloadTask> downloadAllTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String,Thread> THREAD_INSTANCE_CONTAINER = new ConcurrentHashMap<>(16);
    private final LinkedBlockingDeque<DownloadTask> downloadTasks = new LinkedBlockingDeque<>();
    private final LinkedBlockingDeque<DownloadTask> downloadCacheTasks = new LinkedBlockingDeque<>();
    private final LinkedBlockingDeque<DownloadTask> downloadingTasks = new LinkedBlockingDeque<>();
    private DownloadConf downloadConf = new DownloadConf();
    private AtomicInteger threadCounter = new AtomicInteger();
    private String tag;
    private final Object lock = new Object();
    private class DownloaderImpl extends Downloader {

        @Override
        public synchronized DownloadTask getDownloadTask() {
            return getNextDownloadTask();
        }

        @Override
        public void downloadFinish(DownloadTask downloadTask1) {
            synchronized (lock) {
                cacheDownloadStatus(downloadTask1, DOWNLOAD_STATUS_FINISH);
                clearDownloadProgress(downloadTask1);
                downloadingTasks.remove(downloadTask1);
                if(downloadConf.globalListener != null){
                    downloadConf.globalListener.onFinish(downloadTask1);
                }
            }
        }

        @Override
        public void downloadProgress(DownloadTask downloadTask1, int progress) {
            synchronized (lock) {
                cacheDownloadStatus(downloadTask1, DOWNLOAD_STATUS_DOWNLOADING);
                cacheDownloadStatus(downloadTask1, progress);
                if (downloadConf.globalListener != null) {
                    downloadConf.globalListener.onProgress(downloadTask1, progress);
                }
            }
        }

        @Override
        public void downloadCache(DownloadTask downloadTask1) {
            synchronized (lock) {
                downloadCacheTasks.add(downloadTask1);
            }
        }

        @Override
        public void downloadError(DownloadTask downloadTask1) {
            synchronized (lock) {
                cacheDownloadStatus(downloadTask1, DOWNLOAD_STATUS_ERROR);
                clearDownloadProgress(downloadTask1);
                downloadingTasks.remove(downloadTask1);
                if(downloadConf.globalListener != null){
                    downloadConf.globalListener.onError(downloadTask1);
                }
                if (downloadTask1.getProcessTimes() < downloadConf.maxRetryCount) {
                    downloadTask1.prepareUrlFail();
                    downloadTasks.add(downloadTask1);
                }
            }
        }

        @Override
        public void downloadCancel(DownloadTask downloadTask1) {
            synchronized (lock) {
                cacheDownloadStatus(downloadTask1, DOWNLOAD_STATUS_CANCEL);
                clearDownloadProgress(downloadTask1);
                downloadingTasks.remove(downloadTask1);
                if(downloadConf.globalListener != null){
                    downloadConf.globalListener.onFinish(downloadTask1);
                }
            }
        }

        @Override
        public DownloadConf getDownloadConf() {
            return downloadConf;
        }

        @Override
        public void threadStart(String threadName) {

        }

        @Override
        public void threadError(DownloadTask downloadTask1, String threadName) {
            synchronized (lock) {
                downloadingTasks.remove(downloadTask1);
                THREAD_INSTANCE_CONTAINER.remove(threadName);
                if (downloadTask1.getProcessTimes() < downloadConf.maxRetryCount) {
                    downloadTasks.add(downloadTask1);
                }
            }
        }

        @Override
        public void threadEnd(String threadName) {
            synchronized (lock) {
                threadCounter.decrementAndGet();
                THREAD_INSTANCE_CONTAINER.remove(threadName);
                if (threadCounter.get() == 0) {
                    if(!downloadConf.isEnableLocalCheckFirstOnly && downloadConf.isEnableLocalCheckFirst && downloadCacheTasks.size() > 0){
                        startDownloadCacheTask();
                    }else {
                        INSTANCE_CONTAINER.get(tag).release();
                    }
                }
            }
        }
    }

    private void cacheDownloadStatus(DownloadTask downloadTask , int status){
        if(downloadTask instanceof SingleDownloadTask){
            TASK_STATUS_CACHE.put(downloadTask.getDownloadUrl(), status);
        }else{
            TASK_STATUS_CACHE.put(downloadTask.getTag(), status);
        }
    }

    private void cacheDownloadProgress(DownloadTask downloadTask , int progress){
        if(downloadTask instanceof SingleDownloadTask){
            TASK_PROGRESS_CACHE.put(downloadTask.getDownloadUrl(), progress);
        }else{
            TASK_PROGRESS_CACHE.put(downloadTask.getTag(), progress);
        }
    }

    private void clearDownloadProgress(DownloadTask downloadTask){
        if(downloadTask instanceof SingleDownloadTask){
            TASK_PROGRESS_CACHE.remove(downloadTask.getDownloadUrl());
        }else{
            TASK_PROGRESS_CACHE.remove(downloadTask.getTag());
        }
    }

    public static class DownloadConf{

        public DownloadConf() {
        }

        public DownloadConf(int maxThreadCount, DownloadWay downloadWay) {
            this.downloadWay = downloadWay;
            this.maxThreadCount = maxThreadCount;
        }

        public DownloadConf(DownloadWay downloadWay, int maxThreadCount, boolean isCheckLocalFileExist) {
            this.downloadWay = downloadWay;
            this.maxThreadCount = maxThreadCount;
            this.isCheckLocalFileExist = isCheckLocalFileExist;
        }

        public DownloadConf(boolean isOverrideLocalFileExist, DownloadWay downloadWay, int maxThreadCount, boolean isCheckLocalFileExist) {
            this.downloadWay = downloadWay;
            this.maxThreadCount = maxThreadCount;
            this.isCheckLocalFileExist = isCheckLocalFileExist;
            this.isOverrideLocalFileExist = isOverrideLocalFileExist;
        }

        public DownloadConf(boolean isOverrideLocalFileExist, int maxThreadCount, DownloadWay downloadWay) {
            this.isOverrideLocalFileExist = isOverrideLocalFileExist;
            this.maxThreadCount = maxThreadCount;
            this.downloadWay = downloadWay;
        }

        public DownloadConf(DownloadWay downloadWay, int maxThreadCount, boolean isCheckLocalFileExist, int maxRetryCount) {
            this.downloadWay = downloadWay;
            this.maxThreadCount = maxThreadCount;
            this.isCheckLocalFileExist = isCheckLocalFileExist;
            this.maxRetryCount = maxRetryCount;
        }

        private Context context;

        /**
         * 下载的方式 先进先出(默认)  后进先出
         */
        private DownloadWay downloadWay = DownloadWay.FIFO;

        /**
         * 全局的下载广播监听
         */
        private GlobalDownloadListener globalListener;

        /**
         * 最大的线程数量
         */
        private int maxThreadCount = 3;

        private boolean isEnableStatusCache = false;

        private boolean isEnableLocalCheckFirst = false;

        private boolean isEnableLocalCheckFirstOnly = false;

        private boolean isCheckLocalFileExist = true;

        private boolean isOverrideLocalFileExist = false;

        /**
         * 是否在移动网络状态下工作
         */
        private boolean isWorkUnderMobileInternet = false;

        /**
         * 下载失败后的尝试次数
         */
        private int maxRetryCount = 1;

        public int getMaxRetryCount() {
            return maxRetryCount;
        }

        public void setMaxRetryCount(int maxRetryCount) {
            this.maxRetryCount = maxRetryCount;
        }

        public int getMaxThreadCount() {
            return maxThreadCount;
        }

        public GlobalDownloadListener getGlobalListener() {
            return globalListener;
        }

        public void setGlobalListener(GlobalDownloadListener globalListener) {
            this.globalListener = globalListener;
        }

        public void setMaxThreadCount(int maxThreadCount) {
            this.maxThreadCount = maxThreadCount;
        }

        public DownloadWay getDownloadWay() {
            return downloadWay;
        }

        public void setDownloadWay(DownloadWay downloadWay) {
            this.downloadWay = downloadWay;
        }

        public boolean isEnableStatusCache() {
            return isEnableStatusCache;
        }

        public void setEnableStatusCache(boolean enableStatusCache) {
            isEnableStatusCache = enableStatusCache;
        }

        public boolean isEnableLocalCheckFirst() {
            return isEnableLocalCheckFirst;
        }

        public void setEnableLocalCheckFirst(boolean enableLocalCheckFirst) {
            isEnableLocalCheckFirst = enableLocalCheckFirst;
        }

        public boolean isCheckLocalFileExist() {
            return isCheckLocalFileExist;
        }

        public void setCheckLocalFileExist(boolean checkLocalFileExist) {
            isCheckLocalFileExist = checkLocalFileExist;
        }

        public boolean isOverrideLocalFileExist() {
            return isOverrideLocalFileExist;
        }

        public void setOverrideLocalFileExist(boolean overrideLocalFileExist) {
            isOverrideLocalFileExist = overrideLocalFileExist;
        }

        public boolean isEnableLocalCheckFirstOnly() {
            return isEnableLocalCheckFirstOnly;
        }

        public void setEnableLocalCheckFirstOnly(boolean enableLocalCheckFirstOnly) {
            isEnableLocalCheckFirstOnly = enableLocalCheckFirstOnly;
        }

        public boolean isWorkUnderMobileInternet() {
            return isWorkUnderMobileInternet;
        }

        public void setWorkUnderMobileInternet(boolean workUnderMobileInternet) {
            if(workUnderMobileInternet) {
                isWorkUnderMobileInternet = workUnderMobileInternet;
            }else{
                if(context == null){
                    throw new NullPointerException("check net status need Context been initialize");
                }
            }
        }

        public Context getContext() {
            return context;
        }

        public DownloadConf setContext(Context context) {
            this.context = context;
            return this;
        }
    }

    public enum DownloadWay{
        FIFO/*default*/,LIFO
    }

    private FileDownloaderManager(String tag){
        this.tag = tag;
    }

    public static FileDownloaderManager getInstance(String tag){
        if(INSTANCE_CONTAINER.get(tag) == null) {
            synchronized (FileDownloaderManager.class) {
                if(INSTANCE_CONTAINER.get(tag) == null){
                    INSTANCE_CONTAINER.put(tag,new FileDownloaderManager(tag));
                }
            }
        }
        return INSTANCE_CONTAINER.get(tag);
    }

    public FileDownloaderManager initDownloadConf(DownloadConf conf){
        downloadConf = conf;
        return this;
    }

    public DownloadConf getDownloadConf() {
        return downloadConf;
    }

    private DownloadTask getNextDownloadTask(){
        synchronized (lock) {
            if (!downloadTasks.isEmpty()) {
                DownloadTask downloadTask = null;
                switch (downloadConf.downloadWay) {
                    case FIFO:
                        downloadTask = downloadTasks.removeFirst();
                        break;
                    case LIFO:
                        downloadTask = downloadTasks.removeLast();
                }
                if(downloadTask.isCancel()){
                    getNextDownloadTask();
                }else {
                    downloadingTasks.add(downloadTask);
                    return downloadTask;
                }
            }
            return null;
        }
    }

    public void addTaskAndStart(DownloadTask downloadTask){
        addTask(downloadTask, true);
    }

    public void cancel(Object tag){
        synchronized (lock){
            final DownloadTask downloadTask = downloadAllTasks.get(tag);
            downloadTask.setCancel(true);
            downloadTasks.remove(downloadTask);
            downloadingTasks.remove(downloadTask);
        }
    }

    public void cancel(DownloadTask downloadTask){
        if(downloadTask != null) {
            synchronized (lock) {
                downloadTask.setCancel(true);
                downloadTasks.remove(downloadTask);
                downloadingTasks.remove(downloadTask);
            }
        }
    }

    public void startDownloadCacheTask(){
        synchronized (lock) {
            if(downloadCacheTasks.size() > 0) {
                downloadConf.isEnableLocalCheckFirst = false;
                downloadTasks.addAll(downloadCacheTasks);
                downloadCacheTasks.clear();
                startDownload();
            }
        }
    }

    public void cancelAll(){
        synchronized (lock) {
            for (Thread thread : THREAD_INSTANCE_CONTAINER.values()) {
                thread.interrupt();
            }
            while (!downloadTasks.isEmpty()) {
                DownloadTask d = downloadTasks.pop();
                d.getDownloadListener().onCancel(d.getTag(), d.getDownloadUrl(), 0, d.getCurrentIndex(), d.getTotal());
            }
            THREAD_INSTANCE_CONTAINER.clear();
            downloadingTasks.clear();
            downloadTasks.clear();
        }
    }

    public int getDownloadStatus(Object tag){
        Integer integer = TASK_STATUS_CACHE.get(tag);
        return integer != null ? integer : DOWNLOAD_STATUS_UNKNOWN;
    }

    public void release(){
        synchronized (lock) {
            for (Thread thread : THREAD_INSTANCE_CONTAINER.values()) {
                thread.interrupt();
            }
            downloadingTasks.clear();
            downloadTasks.clear();
        }
    }

    public void addTask(DownloadTask downloadTask, boolean isStart){
        synchronized (lock) {
            if(downloadTask instanceof SingleDownloadTask){
                downloadAllTasks.put(downloadTask.getDownloadUrl(), downloadTask);
            }else {
                downloadAllTasks.put(downloadTask.getTag(), downloadTask);
            }
            cacheDownloadStatus(downloadTask, DOWNLOAD_STATUS_WAITING);
            if(!downloadTasks.contains(downloadTask) &&
                    !downloadingTasks.contains(downloadTask)) {
                downloadTasks.addLast(downloadTask);
                if(isStart){
                    startDownload();
                }
            }
        }
    }

    public void startDownload(){
        synchronized (lock) {
            if (threadCounter.get() < downloadConf.maxThreadCount) {
                threadCounter.incrementAndGet();
                DownloaderImpl downloader = new DownloaderImpl();
                THREAD_INSTANCE_CONTAINER.put(downloader.getName(), downloader);
                THREAD_INSTANCE_CONTAINER.get(downloader.getName()).start();
            }
        }
    }

}
