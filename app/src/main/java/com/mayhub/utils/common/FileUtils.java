package com.mayhub.utils.common;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by Administrator on 2016/7/11.
 */
public class FileUtils {

    /**
     * 判断File对象所指的目录或文件是否存在
     *
     * @param file File对象
     * @return true表示存在 false反之
     */
    public static boolean isExist(File file) {
        return file.exists() && file.length() > 0;
    }

    public static boolean isExist(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.length() > 0;
    }

    /**
     * 根据URL路径获取文件名
     *
     * @param url URL路径
     * @return 文件名
     */
    public static String getFileNameFromUrl(String url) {
        if(!TextUtils.isEmpty(url) && url.contains("/")) {
            return url.substring(url.lastIndexOf("/"));
        }
        return null;
    }

    /**
     * 大于指定大小的文件夹是否存在
     * @param filePath 文件全路径
     * @param size 预期的文件夹大小
     * @return true or false;
     */
    public static boolean isDirExistWithSize(String filePath, long size){
        long dirSize = getFileSize(filePath);
        return dirSize >= size;
    }


    /**
     * 文件个大于指定个数的文件夹是否存在
     * @param filePath 文件全路径
     * @param fileCount 预期的文件夹文件数量
     * @return
     */
    public static boolean isDirExistWithFileCount(String filePath, int fileCount){
        if(!TextUtils.isEmpty(filePath)){
            File file = new File(filePath);
            return file.exists() && file.isDirectory() && file.list().length >= fileCount;
        }
        return false;
    }

    /**
     * 获取路径下的文件大小
     * @param filePath 文件全路径
     * @return 文件大小
     */
    public static long getFileSize(String filePath){
        if(!TextUtils.isEmpty(filePath)){
            return getFileSize(new File(filePath));
        }
        return 0;
    }

    /**
     * 获取文件的大小
     * @param file 文件
     * @return 文件大小
     */
    public static long getFileSize(File file){
        if(file != null && file.exists()) {
            long total = 0;
            if (file.isDirectory()) {
                for (File subFile : file.listFiles()) {
                    total += getFileSize(subFile);
                }
            } else {
                total += file.length();
            }
            return total;
        }
        return 0;
    }

    /**
     * 磁盘空间是否足够
     * @return true or false;
     */
    public static boolean isDiskHasEnoughSpace(String filePath, long fileSize){
        if(!TextUtils.isEmpty(filePath)){
            return isDiskHasEnoughSpace(new File(filePath), fileSize);
        }
        return false;
    }

    /**
     * 磁盘空间是否足够
     * @return true or false;
     */
    public static boolean isDiskHasEnoughSpace(File file, long fileSize){
        return file.exists() && file.isDirectory() && file.getUsableSpace() > fileSize;
    }


}
