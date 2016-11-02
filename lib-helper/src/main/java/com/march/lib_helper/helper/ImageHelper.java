package com.march.lib_helper.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.widget.TextView;

import com.march.lib_helper.model.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : 图像操作
 *
 * @author chendong
 */
public class ImageHelper {

    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    public static final String ALL_IMAGE_KEY = "全部图片";
    /**
     * 简化给TextView设置drawable
     *
     * @param context  context
     * @param textView view
     * @param res      资源id
     * @param pos      位置
     */
    public static void setTvDrawable(Context context, TextView textView, int res, int pos) {
        switch (pos) {
            case TOP:
                textView.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(context, res), null, null);
                break;
            case BOTTOM:
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getDrawable(context, res));
                break;
            case LEFT:
                textView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(context, res), null, null, null);
                break;
            case RIGHT:
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(context, res), null);
                break;
        }
    }

    /**
     * 使用资源获取drawable
     *
     * @param context context
     * @param res     资源
     * @return drawable
     */
    public static Drawable getDrawable(Context context, int res) {
        return ContextCompat.getDrawable(context, res);
    }

    /**
     * 获取全部照片信息
     *
     * @param context 上下文
     * @return 照片信息列表
     */
    public static List<ImageInfo> getImagesByMediaStore(Context context) {
        List<ImageInfo> mImageInfoList = new ArrayList<>();
        Cursor imageCursor;
        try {
            final String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_MODIFIED};
            final String sortOrder = MediaStore.Images.Media._ID;
            // 下面两个方法都行
            // imageCursor =
            // context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            // projection, null, null, sortOrder);
            imageCursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);
            if (imageCursor != null && imageCursor.getCount() > 0) {
                while (imageCursor.moveToNext()) {
                    ImageInfo imageInfo = new ImageInfo();
                    // 返回data在第几列，并获取地址
                    int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    String path = imageCursor.getString(dataColumnIndex);
                    File file = new File(path);
                    // 该路径下的文件存在则添加到集合中
                    if (file.exists()) {
                        // 添加路径到对象中
                        imageInfo.setPath(path);
                        // 插入修改时间
                        int modifiedColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
                        String modifiedDate = imageCursor.getString(modifiedColumnIndex);
                        // 添加修改时间到对象中
                        imageInfo.setDate(modifiedDate);
                        // 添加名字
                        mImageInfoList.add(imageInfo);
                    }
                }
                imageCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 按降序排
        Collections.sort(mImageInfoList);
        return mImageInfoList;
    }

    /**
     * 目录 + 目录下的照片列表
     *
     * @param imageInfoList 全部的照片信息
     * @return (目录 照片信息列表)
     */
    public static Map<String, List<ImageInfo>> formatImages4EachDir(List<ImageInfo> imageInfoList) {
        Map<String, List<ImageInfo>> map = new ArrayMap<>();
        map.clear();
        List<ImageInfo> imageInfoAll = new ArrayList<>();
        imageInfoAll.addAll(imageInfoList);
        map.put(ALL_IMAGE_KEY, imageInfoAll);

        for (ImageInfo imageInfo : imageInfoList) {
            if (imageInfo != null && imageInfo.getPath() != null) {
                File file = new File(imageInfo.getPath());
                if (file.exists()) {
                    if (file.getParentFile() != null) {
                        if (map.containsKey(file.getParentFile().getName())) {
                            // 如果key已经存在
                            map.get(file.getParentFile().getName()).add(imageInfo);
                        } else {
                            // 如果key不存在
                            List<ImageInfo> tempImageInfoList = new ArrayList<>();
                            tempImageInfoList.add(imageInfo);
                            map.put(file.getParentFile().getName(), tempImageInfoList);
                        }
                    }
                }
            }
        }
        return map;
    }

}
