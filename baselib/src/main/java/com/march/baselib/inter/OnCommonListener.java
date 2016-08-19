package com.march.baselib.inter;

/**
 * Project  : CommonLib <p>
 * Package  : com.march.baselib <p>
 * CreateAt : 16/8/16 <p>
 * Describe : 公用回调，返回一个位置和范型数据 <p>
 *
 * @author chendong <p>
 */
public interface OnCommonListener<D> {
    void onEvent(int pos, D data);
}
