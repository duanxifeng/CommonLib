package com.march.lib.adapter.common;

import com.march.lib.adapter.core.BaseViewHolder;

/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter.common
 * CreateAt : 2016/11/9
 * Describe : 事件
 *
 * @author chendong
 */
public interface OnItemListener<D> {
    // 单击事件
    void onClick(int pos, BaseViewHolder holder, D data);
    // 长按事件
    void onLongPress(int pos, BaseViewHolder holder, D data);
    // 双击事件
    void onDoubleClick(int pos, BaseViewHolder holder, D data);

    boolean isSupportDoubleClick();
}
