package com.march.baselib.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.march.baselib.DevelopLib;

/**
 * Project  : CommonLib </br>
 * Package  : com.march.baselib </br>
 * CreateAt : 16/8/13 </br> </br>
 * Describe : 尺寸相关操作</br>
 *
 * @author chendong </br>
 */
public class DimensionHelper {

    private static DisplayMetrics metrics;

    private static DisplayMetrics getMetrics() {
        if (metrics == null) {
            metrics = DevelopLib.getCtx().getResources().getDisplayMetrics();
        }
        return metrics;
    }

    /**
     * 屏幕宽度
     *
     * @return 屏幕宽度
     */
    public static int getScreenWidth() {
        return getMetrics().widthPixels;
    }

    /**
     * 屏幕高度
     *
     * @return 屏幕高度
     */
    public static int getScreenHeight() {
        return getMetrics().heightPixels;
    }


    /**
     * dp转px
     *
     * @param dpVal dp值
     * @return px值
     */
    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getMetrics());
    }

    /**
     * sp转px
     *
     * @param spVal sp 值
     * @return px 值
     */
    public static int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getMetrics());
    }

    /**
     * px转dp
     *
     * @param pxVal px 值
     * @return dp值
     */
    public static float px2dp(float pxVal) {
        final float scale = getMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param pxVal px值
     * @return sp值
     */
    public static float px2sp(float pxVal) {
        return (pxVal / getMetrics().scaledDensity);
    }


}
