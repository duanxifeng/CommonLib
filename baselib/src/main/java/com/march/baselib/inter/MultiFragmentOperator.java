package com.march.baselib.inter;

import android.support.v4.app.Fragment;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib.inter
 * CreateAt : 16/9/30
 * Describe :
 *
 * @author chendong
 */

public interface MultiFragmentOperator {

    /**
     * 当点击显示同一个
     *
     * @param showItem 显示的item
     * @return 返回false表示忽略此次点击的切换
     */
    boolean whenShowSameFragment(int showItem);


    /**
     * 获取放置fragment的控件id
     *
     * @return id
     */
    int getFragmentContainerId();

    /**
     * 构建fragment
     *
     * @param showItem 将要展示的fragment pos
     * @return fragment
     */
    Fragment makeFragment(int showItem);


    /**
     * 同步选中之后的显示状态
     *
     * @param selectImage 被选中的item
     */
    void syncSelectState(int selectImage);
}
