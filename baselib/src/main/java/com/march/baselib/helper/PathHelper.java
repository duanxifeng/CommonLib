package com.march.baselib.helper;

import android.os.Environment;
import android.widget.Toast;

import com.march.baselib.develop.DevelopLib;

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
    /**
     * 暂存路径
     */
    public static String TempPath;
    /**
     * app 父路径
     */
    public static String AppRootPath;
    /**
     * 外存卡父路径
     */
    public static String ES_PATH;

    public static String DCIM_PATH;

    /**
     * 初始化路径
     */
    public static boolean initPath() {
        if (CommonHelper.isSDCardAvailable()) {
            ES_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
            DCIM_PATH = new File(ES_PATH, "/DCIM/Camera").getAbsolutePath();
            //  /storage/sdcard/0/包名
            AppRootPath = new File(ES_PATH, DevelopLib.getCtx().getPackageName()).getAbsolutePath();

        } else {
            // data/data/包名/files（这个文件夹在apk安装的时候就会创建）
            AppRootPath = DevelopLib.getCtx().getFilesDir().getAbsolutePath();
        }

        TempPath = AppRootPath + "/temp";
        boolean mkdirs = new File(TempPath).mkdirs();
        return mkdirs;

    }
}
