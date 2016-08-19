package com.march.baselib.helper;

import android.os.Environment;

import com.march.baselib.DevelopLib;

import java.io.File;

/**
 * Project  : CommonLib <p>
 * Package  : com.march.baselib <p>
 * CreateAt : 16/8/15 <p> 
 * Describe : 创建路径<p>
 *
 * @author chendong <p>
 */
public class PathHelper {
    /**
     * 暂存路径
     */
    public static String TempPath;
    /**
     * 父路径
     */
    public static String ParentPath;

    /**
     *初始化路径
     */
    public static void initPath() {
        if (CommonHelper.isSDCardAvailable()) {
            // 使用自己设置的sdcard缓存路径，需要应用里设置清除缓存按钮
            //  /storage/sdcard0/包名
            ParentPath = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + DevelopLib.getCtx().getPackageName();
            // /storage/sdcard0/Android/data/com.example.qymh/cache
        } else {
            // data/data/包名/files（这个文件夹在apk安装的时候就会创建）
            ParentPath = DevelopLib.getCtx().getFilesDir().getAbsolutePath();
        }

        TempPath = ParentPath + "/temp";
        new File(TempPath).mkdirs();
    }
}
