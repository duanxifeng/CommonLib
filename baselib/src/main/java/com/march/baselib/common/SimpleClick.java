package com.march.baselib.common;

import android.view.View;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : 防止多次点击
 *
 * @author chendong
 */
public abstract class SimpleClick implements View.OnClickListener {
    public static boolean checkDoubleClick = false;
    public static int intervalTime2Check = 500;
    private long lastClickTime = 0;

    /**
     * 初始化监测时间和是否监测
     * @param isCheck 是否监测连续点击
     * @param time 间隔时间
     */
    public static void init(boolean isCheck, int time) {
        checkDoubleClick = isCheck;
        intervalTime2Check = time;
    }

    @Override
    public void onClick(View v) {
        if (!checkDoubleClick) {
            onClickAfterCheck(v);
            return;
        }
        long time = System.currentTimeMillis();
        if (time - lastClickTime > intervalTime2Check) {
            onClickAfterCheck(v);
            lastClickTime = time;
        }

    }

    public abstract void onClickAfterCheck(View view);
}
