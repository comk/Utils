package com.mayhub.utils.download;

import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import com.mayhub.utils.common.MLogUtil;
import com.mayhub.utils.common.NetUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/6/30.
 */
public abstract class Downloader extends Thread {
    static final long MIN_SPACE = 3 * 1024 * 1024;
    static final int HTTP_OK = 200;
    static final int HTTP_PARTIAL = 206;
    static final int HTTP_MOVED_PERM = 301;
    static final int HTTP_MOVED_TEMP = 302;
    static final int HTTP_SEE_OTHER = 303;
    static final int HTTP_NOT_MODIFIED = 304;
    static final int HTTP_TEMP_REDIRECT = 307;
    static final int DEFAULT_TIMEOUT = 3000;
    static {
        final StringBuilder builder = new StringBuilder();

        final boolean validRelease = !TextUtils.isEmpty(Build.VERSION.RELEASE);
        final boolean validId = !TextUtils.isEmpty(Build.ID);
        final boolean includeModel = "REL".equals(Build.VERSION.CODENAME)
                && !TextUtils.isEmpty(Build.MODEL);

        builder.append("MultiThreadDownloader");
        if (validRelease) {
            builder.append("/").append(Build.VERSION.RELEASE);
        }
        builder.append(" (Linux; U; Android");
        if (validRelease) {
            builder.append(" ").append(Build.VERSION.RELEASE);
        }
        if (includeModel || validId) {
            builder.append(";");
            if (includeModel) {
                builder.append(" ").append(Build.MODEL);
            }
            if (validId) {
                builder.append(" Build/").append(Build.ID);
            }
        }
        builder.append(")");

        DEFAULT_USER_AGENT = builder.toString();
    }

    public static final String DEFAULT_USER_AGENT;

    private DownloadTask downloadTask;

    public static class DownloadErrorException extends Exception{

        private String errorMsg;

        public DownloadErrorException(String message, String errorMsg1) {
            super(message);
            errorMsg = errorMsg1;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }

    public static class DownloadCancelException extends Exception{
        public DownloadCancelException(String message) {
            super(message);
        }
    }

    public interface IDListener {
        void onPrepare(String realUrl);

        void onStart(String fileName, String realUrl, int fileLength);

        void onProgress(String realUrl, int progress);

        void onCancel(String realUrl, int progress);

        void onFinish(File file, String realUrl);

        void onError(int status, String error, File file, String realUrl);
    }

    /**
     * 获取下载任务
     * @return
     */
    public abstract DownloadTask getDownloadTask();

    public abstract void downloadFinish(DownloadTask downloadTask1);

    public abstract void downloadStart(DownloadTask downloadTask1);

    public abstract void downloadProgress(DownloadTask downloadTask1, int progress);

    public abstract void downloadCache(DownloadTask downloadTask1);

    public abstract void downloadError(DownloadTask downloadTask1);

    public abstract void downloadCancel(DownloadTask downloadTask1);

    public abstract FileDownloaderManager.DownloadConf getDownloadConf();

    public abstract void threadStart(String threadName);

    public abstract void threadError(DownloadTask downloadTask1, String threadName);

    public abstract void threadEnd(String threadName);

    void addRequestHeaders(HttpURLConnection httpURLConnection, boolean isKeepAlive) {
        httpURLConnection.addRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg," +
                "application/x-shockwave-flash, application/xaml+xml," +
                "application/vnd.ms-xpsdocument, application/x-ms-xbap," +
                "application/x-ms-application, application/vnd.ms-excel," +
                "application/vnd.ms-powerpoint, application/msword, */*");
        httpURLConnection.addRequestProperty("Accept-Ranges", "bytes");
        httpURLConnection.addRequestProperty("Charset", "UTF-8");
        if(isKeepAlive){
            httpURLConnection.addRequestProperty("Connection", "Keep-Alive");
        }else if (Build.VERSION.SDK_INT > 13) {
            httpURLConnection.setRequestProperty("Connection", "close");
        }
        httpURLConnection.addRequestProperty("Accept-Encoding", "identity");
        httpURLConnection.addRequestProperty("Range", "bytes=" + 0 + "-");
        if (!httpURLConnection.getRequestProperties().containsKey("User-Agent")) {
            httpURLConnection.addRequestProperty("User-Agent", DEFAULT_USER_AGENT);
        }
    }

    private boolean blockMobile(){
        if(!getDownloadConf().isWorkUnderMobileInternet() && getDownloadConf().getContext() != null){
            return NetUtil.NetType.WIFI != NetUtil.getNetWorkType(getDownloadConf().getContext());
        }
        return false;
    }

    private void executeTask() throws Exception{
        if(blockMobile()){
            throw new DownloadCancelException(String.format("Downloading Task %s Cancel", downloadTask.getDownloadUrl()));
        }
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(downloadTask.getDownloadUrl()).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(DEFAULT_TIMEOUT);
            conn.setReadTimeout(DEFAULT_TIMEOUT);

            addRequestHeaders(conn, true);
            if(isInterrupted() || downloadTask.isCancel()){
                throw new DownloadCancelException(String.format("Downloading Task %s Cancel", downloadTask.getDownloadUrl()));
            }
            final int code = conn.getResponseCode();
            switch (code) {
                case HTTP_OK:
                case HTTP_PARTIAL:
                    downloadFile(conn);
                    if(renameToOriginalName()) {
                        fileComplete();
                        break;
                    }else{
                        throw new DownloadErrorException(conn.getResponseMessage(), conn.getResponseMessage());
//                        downloadError(downloadTask);
//                        if (downloadTask.getDownloadListener() != null) {
//                            downloadTask.getDownloadListener().onError(downloadTask.getTag(), code, conn.getResponseMessage(), null, downloadTask.getDownloadUrl(), downloadTask.getCurrentIndex(), downloadTask.getTotal());
//                        }
                    }
                case HTTP_MOVED_PERM:
                case HTTP_MOVED_TEMP:
                case HTTP_SEE_OTHER:
                case HTTP_NOT_MODIFIED:
                case HTTP_TEMP_REDIRECT:
                default:
                    throw new DownloadErrorException(conn.getResponseMessage(), conn.getResponseMessage());
//                    downloadError(downloadTask);
//                    if (downloadTask.getDownloadListener() != null) {
//                        downloadTask.getDownloadListener().onError(downloadTask.getTag(), code, conn.getResponseMessage(), null, downloadTask.getDownloadUrl(), downloadTask.getCurrentIndex(), downloadTask.getTotal());
//                    }
            }
        } finally {
            if (null != conn) conn.disconnect();
        }
    }

    private void taskFinish(){
        downloadFinish(downloadTask);
        if (downloadTask.getDownloadListener() != null) {
            downloadTask.getDownloadListener().onFinish(downloadTask.getTag(), new File(downloadTask.getDestDir(), downloadTask.getFileName()), downloadTask.getDownloadUrl(), downloadTask.getCurrentIndex(), downloadTask.getTotal());
        }
    }

    private void multiTask() throws Exception{
        while (downloadTask.prepareNextUrl()) {
            if(isInterrupted() || downloadTask.isCancel()){
                throw new DownloadCancelException(String.format("Downloading Task %s Cancel", downloadTask.getDownloadUrl()));
            }
            if(checkSpaceAndFileStatus()){
                return;
            }
        }
        taskFinish();
    }

    private boolean checkSpaceAndFileStatus() throws Exception{
        if(isSpaceEnough(downloadTask.getDestDir(), MIN_SPACE)) {
            if (getDownloadConf().isCheckLocalFileExist()) {
                if (!new File(downloadTask.getDestDir(), downloadTask.getFileName()).exists()) {
                    if(getDownloadConf().isEnableLocalCheckFirst()) {
                        downloadTask.prepareUrlFail();
                        downloadCache(downloadTask);
                        return true;
                    }else{
                        executeTask();
                    }
                }else{
                    fileComplete();
                }
            } else {
                executeTask();
            }
        }else{
            throw new DownloadErrorException("", "存储空间不足或无文件读写权限");
//            downloadError(downloadTask);
//            if(downloadTask.getDownloadListener() != null){
//                downloadTask.getDownloadListener().onError(downloadTask.getTag(), 0, "存储空间不足或无文件读写权限", null, downloadTask.getDownloadUrl(), downloadTask.getCurrentIndex(), downloadTask.getTotal());
//            }
        }
        return false;
    }

    private void fileComplete() {
        if(downloadTask instanceof MultiDownloadTask){
            downloadProgress(downloadTask, 100);
            if(downloadTask.getDownloadListener() != null){
                downloadTask.getDownloadListener().onProgress(downloadTask.getTag(), downloadTask.getDownloadUrl(), 100, downloadTask.getCurrentIndex(), downloadTask.getTotal());
            }
        }else {
            taskFinish();
        }
    }

    private void taskCancel() {
        downloadCancel(downloadTask);
        if (downloadTask.getDownloadListener() != null) {
            downloadTask.getDownloadListener().onCancel(downloadTask.getTag(), downloadTask.getDownloadUrl(), 0, downloadTask.getCurrentIndex(), downloadTask.getTotal());
        }
    }

    private void taskStart(){
        downloadStart(downloadTask);
        if(downloadTask.getDownloadListener() != null){
            downloadTask.getDownloadListener().onStart(downloadTask.getTag(), downloadTask.getFileName(), downloadTask.getDownloadUrl(), 0, downloadTask.getCurrentIndex(), downloadTask.getTotal());
        }
    }

    @Override
    public void run() {
        threadStart(getName());
        try {
            while (!isInterrupted() && (downloadTask = getDownloadTask()) != null) {
                try {
                    taskStart();
                    downloadTask.incrementProcessTimes();
                    if (downloadTask instanceof MultiDownloadTask) {
                        multiTask();
                    } else {
                        checkSpaceAndFileStatus();
                    }
                }catch (Exception e){
                    if(e instanceof DownloadCancelException){
                        taskCancel();
                    }else{
                        downloadError(downloadTask);
                        if(downloadTask.getDownloadListener() != null){
                            downloadTask.getDownloadListener().onError(downloadTask.getTag(), 0, ((DownloadErrorException) e).getErrorMsg(), null, downloadTask.getDownloadUrl(), downloadTask.getCurrentIndex(), downloadTask.getTotal());
                        }
                    }
                    MLogUtil.p(e);
                }
            }
            threadEnd(getName());
        }catch (Exception ex){
            MLogUtil.p(ex);
            threadError(downloadTask, getName());
        }
    }

    private boolean renameToOriginalName(){
        return new File(downloadTask.getDestDir(), downloadTask.getCacheFileName()).renameTo(new File(downloadTask.getDestDir(), downloadTask.getFileName()));
    }

    static synchronized boolean createFile(String path, String fileName) {
        boolean hasFile = false;
        try {
            File dir = new File(path);
            boolean hasDir = dir.exists() || dir.mkdirs();
            if (hasDir) {
                File file = new File(dir, fileName);
                hasFile = file.exists() || file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hasFile;
    }



    /**
     * 获取下载文件的大小
     * @return
     */
    private long getDownloadFileLength(HttpURLConnection conn){
        final String transferEncoding = conn.getHeaderField("Transfer-Encoding");
        if (TextUtils.isEmpty(transferEncoding)) {
            try {
                return Long.parseLong(conn.getHeaderField("Content-Length"));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    private boolean isFileExist(long totalBytes){
        File file = new File(downloadTask.getDestDir() , downloadTask.getFileName());
        if(file.exists()) {
            if(totalBytes == file.length()){
                return true;
            }else{
                //文件不完整，删除文件后再下载
                file.delete();
                return false;
            }
        }else{
            file = new File(downloadTask.getDestDir(), downloadTask.getCacheFileName());
            if(file.exists() && totalBytes == file.length()){
                return true;
            }else{
                file.delete();
                return false;
            }
        }
    }

    private boolean isSpaceEnough(String dir, long totalLength){
        File dirFile = new File(dir);
        if(dirFile.exists() || dirFile.mkdirs()){
            return dirFile.getUsableSpace() > totalLength;
        }
        return false;
    }

    private void downloadFile(HttpURLConnection conn) throws Exception {
        long totalBytes = getDownloadFileLength(conn);
        if(getDownloadConf().isOverrideLocalFileExist() || !isFileExist(totalBytes)) {
            if(isSpaceEnough(downloadTask.getDestDir(), totalBytes)) {
                createFile(downloadTask.getDestDir(), downloadTask.getCacheFileName());
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(new File(downloadTask.getDestDir(), downloadTask.getCacheFileName()));
                long totalRec = 0;
                byte[] b = new byte[4096];
                int len;
                while ((len = is.read(b)) != -1) {
                    if (isInterrupted()) {
                        fos.close();
                        is.close();
                        throw new DownloadCancelException(String.format("Downloading Task %s Cancel", downloadTask.getDownloadUrl()));
                    }
                    if(downloadTask.isCancel()){
                        fos.close();
                        is.close();
                        throw new DownloadCancelException(String.format("Downloading Task %s Cancel", downloadTask.getDownloadUrl()));
                    }
                    if(blockMobile()){
                        fos.close();
                        is.close();
                        throw new DownloadCancelException("Downloading Task is not work under mobile network");
                    }
                    fos.write(b, 0, len);
                    totalRec += len;
                    downloadProgress(downloadTask, (int) (totalRec * 100 / totalBytes));
                    if (downloadTask.getDownloadListener() != null) {
                        downloadTask.getDownloadListener().onProgress(downloadTask.getTag(), downloadTask.getDownloadUrl(), (int) (totalRec * 100 / totalBytes), downloadTask.getCurrentIndex(), downloadTask.getTotal());
                    }
                }
                fos.close();
                is.close();
            }else{//存储空间不足
                throw new DownloadErrorException("", "存储空间不足或无文件读写权限");
//                downloadError(downloadTask);
//                if(downloadTask.getDownloadListener() != null){
//                    downloadTask.getDownloadListener().onError(downloadTask.getTag(), 0, "存储空间不足或无文件读写权限", null, downloadTask.getDownloadUrl(), downloadTask.getCurrentIndex(), downloadTask.getTotal());
//                }
            }
        }

    }

}