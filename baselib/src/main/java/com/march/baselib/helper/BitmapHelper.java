package com.march.baselib.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.march.baselib.develop.DevelopLib;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : Bitmap处理类
 *
 * @author chendong
 */
public class BitmapHelper {

    private static int mDesiredWidth;
    private static int mDesiredHeight;

    /**
     * 压缩图片到文件
     * @param bmp 位图
     * @param file 文件
     * @param quality 质量
     * @param isScan 是否通知相册刷新
     * @return 存入的file
     */
    public static File compressImage(Bitmap bmp, File file, int quality, boolean isScan) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            // 直接压缩80%
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
            if (bmp.isRecycled())
                bmp.recycle();
            if (isScan)
                FileHelper.scanFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 从Resources中加载图片
     * @param resId 资源id
     * @param reqWidth 要求的宽度
     * @param reqHeight 要求的高度
     * @return 处理后的bitmap
     */
    public static Bitmap decodeRes(int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置成了true,不占用内存，只获取bitmap宽高
        options.inJustDecodeBounds = true;
        // 初始化options对象
        BitmapFactory.decodeResource(DevelopLib.getCtx().getResources(), resId, options);
        // 得到计算好的options，目标宽、目标高
        options = getBestOptions(options, reqWidth, reqHeight);
        Bitmap src = BitmapFactory.decodeResource(DevelopLib.getCtx().getResources(), resId, options); // 载入一个稍大的缩略图
        return createScaleBitmap(src, mDesiredWidth, mDesiredHeight); // 进一步得到目标大小的缩略图
    }


    /**
     * 从SD卡上加载图片
     * @param pathName  文件路径
     * @param reqWidth 要求的宽度
     * @param reqHeight 要求的高度
     * @return 处理后的bitmap
     */
    public static Bitmap decodeFile(String pathName, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options = getBestOptions(options, reqWidth, reqHeight);
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, mDesiredWidth, mDesiredHeight);
    }

    /**
     * 计算目标宽度，目标高度，inSampleSize
     *
     * @return BitmapFactory.Options对象
     */
    private static BitmapFactory.Options getBestOptions(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 读取图片长宽
        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;
        // Then compute the dimensions we would ideally like to decode to.
        mDesiredWidth = getResizedDimension(reqWidth, reqHeight, actualWidth, actualHeight);
        mDesiredHeight = getResizedDimension(reqHeight, reqWidth, actualHeight, actualWidth);
        // 根据现在得到计算inSampleSize
        options.inSampleSize = calculateBestInSampleSize(actualWidth, actualHeight, mDesiredWidth, mDesiredHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return options;
    }

    /**
     * Scales one side of a rectangle to fit aspect ratio. 最终得到重新测量的尺寸
     *
     * @param maxPrimary      Maximum size of the primary dimension (i.e. width for max
     *                        width), or zero to maintain aspect ratio with secondary
     *                        dimension
     * @param maxSecondary    Maximum size of the secondary dimension, or zero to maintain
     *                        aspect ratio with primary dimension
     * @param actualPrimary   Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary) {
        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth   Actual width of the bitmap
     * @param actualHeight  Actual height of the bitmap
     * @param desiredWidth  Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    private static int calculateBestInSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float inSampleSize = 1.0f;
        while ((inSampleSize * 2) <= ratio) {
            inSampleSize *= 2;
        }

        return (int) inSampleSize;
    }

    /**
     * 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     */
    private static Bitmap createScaleBitmap(Bitmap tempBitmap, int desiredWidth, int desiredHeight) {
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
            // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
            Bitmap bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
            tempBitmap.recycle(); // 释放Bitmap的native像素数组
            return bitmap;
        } else {
            return tempBitmap; // 如果没有缩放，那么不回收
        }
    }
}
