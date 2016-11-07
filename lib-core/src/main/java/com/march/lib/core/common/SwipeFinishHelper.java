package com.march.lib.core.common;

import android.app.Activity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Project  : CommonLib
 * Package  : com.march.lib.core.common
 * CreateAt : 2016/11/7
 * Describe : 侧滑删除帮助类
 *
 * @author chendong
 */
public class SwipeFinishHelper {

    public interface OnSwipeListener {
        boolean onSwipeRight();
    }

    private OnSwipeListener listener;
    private int minXSwipe;
    //手势操作
    private GestureDetector gestureDetector;
    private boolean isEnable = true;


    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }


    public void unRegisterSwipeFinish() {
        gestureDetector = null;
    }

    public void registerSwipeFinish(Activity activity, OnSwipeListener listener) {
        minXSwipe = activity.getResources().getDisplayMetrics().widthPixels / 3;
        gestureDetector = new GestureDetector(activity, new DiyGestureListener(), null, true);
        this.listener = listener;
    }

    private class DiyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e("chendong", "fling ...   " + velocityX + "  " + velocityY);
            Log.e("chendong", "fling ...   " + Math.abs(e2.getX() - e1.getX()) + "  " + minXSwipe);
            //是否允许截断滑动事件
            if (!isEnable)
                return false;
            float minMove = minXSwipe;         //最小滑动距离
            float minVelocity = 2000;      //最小滑动速度
            if (Math.abs(velocityX) > minVelocity || Math.abs(velocityY) > minVelocity) {
                //空白区域触发
                float beginX = e1.getX();
                float endX = e2.getX();
                float beginY = e1.getY();
                float endY = e2.getY();

                if (beginY - endY > minMove && Math.abs(velocityY) > minVelocity) {   //上滑
                    return FlingTop();
                } else if (endY - beginY > minMove && Math.abs(velocityY) > minVelocity) {   //下滑
                    return FlingBottom();
                } else if (beginX - endX > minMove && Math.abs(velocityX) > minVelocity) {   //左滑
                    return FlingLeft();
                } else if (endX - beginX > minMove && Math.abs(velocityX) > minVelocity) {   //右滑
                    return FlingRight();
                }
                return true;
            }
            return false;
        }
    }

    private boolean FlingLeft() {
        return false;
    }

    /**
     * 向右滑动触发事件
     */
    private boolean FlingRight() {
        return listener != null && listener.onSwipeRight();
    }

    private boolean FlingTop() {
        return false;
    }

    private boolean FlingBottom() {
        return false;
    }
}
