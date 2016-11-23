package com.march.lib.support.helper;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : 创建路径
 *
 * @author chendong
 */
public class PathHelper {

    public static String fileKey;
    /**
     * app 父路径
     */
    public static String AppRootPath;
    /**
     * 暂存路径
     */
    public static String TEMP_PATH;
    /**
     * 相册文件夹
     */
    public static String DCIM_PATH;
    /**
     * 不可见文件夹
     */
    public static String THUMB_PATH;
    /**
     * 下载文件夹
     */
    public static String DOWNLOAD_PATH;
    /**
     * 根路径
     */
    public static String ES_PATH;

    /**
     * 初始化路径
     *
     * @param context context
     */
    public static void initPath(Context context, String key) {
        fileKey = context.getPackageName();
        if (key != null)
            fileKey = key;
        if (FileHelper.isSDCardAvailable()) {
            ES_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            ES_PATH = context.getFilesDir().getAbsolutePath();
        }
        FileHelper.checkAndMakeFile(ES_PATH, true);
        DCIM_PATH = new File(ES_PATH, "/DCIM/Camera").getAbsolutePath();
        AppRootPath = new File(ES_PATH, fileKey).getAbsolutePath();
        TEMP_PATH = AppRootPath + "/temp";
        THUMB_PATH = AppRootPath + "/.thumb";
        DOWNLOAD_PATH = AppRootPath + "/download";
    }


    public static File dcim() {
        FileHelper.checkAndMakeFile(DCIM_PATH, true);
        return new File(DCIM_PATH);
    }

    public static File thumb() {
        FileHelper.checkAndMakeFile(THUMB_PATH, true);
        return new File(THUMB_PATH);
    }

    public static File download() {
        FileHelper.checkAndMakeFile(DOWNLOAD_PATH, true);
        return new File(DOWNLOAD_PATH);
    }

    public static File temp() {
        FileHelper.checkAndMakeFile(TEMP_PATH, true);
        return new File(TEMP_PATH);
    }
}
