package com.march.baselib.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : 文件相关操作
 *
 * @author chendong
 */
public class FileHelper {

    /**
     * 复制文件到
     *
     * @param srcFile  原文件
     * @param destFile 目标文件
     * @return 复制成功
     */
    public static boolean copyTo(File srcFile, File destFile, boolean isDeleteSrcFile) {
        if (!srcFile.exists()) {
            return false;
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            destFile.getParentFile().mkdirs();
            destFile.createNewFile();

            bis = new BufferedInputStream(new FileInputStream(srcFile));
            bos = new BufferedOutputStream(new FileOutputStream(destFile));
            int size;
            byte[] temp = new byte[1024];
            while ((size = bis.read(temp, 0, temp.length)) != -1) {
                bos.write(temp, 0, size);
            }
            bos.flush();
            if (isDeleteSrcFile)
                srcFile.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CommonHelper.closeStream(bis, bos);
        }
        return false;
    }


    /**
     * 针对系统文夹只需要扫描|||不然会重复!!!
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    public static void scanIntoMediaStore(Context context, String filePath) {
        if (!checkFile(filePath))
            return;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(intent);
    }


    /**
     * 插入时初始化公共字段
     *
     * @param saveFile 文件
     * @return ContentValues
     */
    private static ContentValues initCommonContentValues(File saveFile) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, saveFile.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, saveFile.getName());
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.SIZE, saveFile.length());
        return values;
    }

    /**
     * 插入image,针对非系统资源文件夹保存图片操作
     *
     * @param context    上下文
     * @param filePath   文件路径
     * @param createTime 创建时间
     */
    public static void insertImageToMediaStore(Context context, String filePath, long createTime) {
        if (!checkFile(filePath))
            return;
        if (createTime == 0)
            createTime = System.currentTimeMillis();
        File saveFile = new File(filePath);
        ContentValues values = initCommonContentValues(saveFile);
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, createTime);
        values.put(MediaStore.Images.ImageColumns.ORIENTATION, 0);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * 插入video,针对非系统资源文件夹保存video操作
     *
     * @param context    上下文
     * @param filePath   文件路径
     * @param createTime 创建时间
     * @param duration   时长，必须是毫秒级别
     */
    public static void insertVideoToMediaStore(Context context, String filePath, long createTime, long duration) {
        if (!checkFile(filePath))
            return;
        if (createTime == 0)
            createTime = System.currentTimeMillis();
        File saveFile = new File(filePath);
        ContentValues values = initCommonContentValues(saveFile);
        values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, createTime);
        values.put(MediaStore.Video.VideoColumns.DURATION, duration);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "video/3gp");
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static boolean checkFile(String filePath) {
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }
}
