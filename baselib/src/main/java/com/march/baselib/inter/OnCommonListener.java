package com.march.baselib.inter;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/16
 * Describe : 公用回调，返回一个位置和范型数据
 *
 * @author chendong
 */
public interface OnCommonListener<D> {
    void onEvent(int pos, D data);
}
