package com.march.baselib.inter;

/**
 * Project  : CommonLib </br>
 * Package  : com.march.baselib </br>
 * CreateAt : 16/8/16 </br> </br>
 * Describe : 公用回调，返回一个位置和范型数据 </br>
 *
 * @author chendong </br>
 */
public interface OnCommonListener<D> {
    void onEvent(int pos, D data);
}
