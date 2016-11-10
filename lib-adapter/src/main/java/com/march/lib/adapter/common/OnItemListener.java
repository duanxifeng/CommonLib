package com.march.lib.adapter.common;

import com.march.lib.adapter.core.BaseViewHolder;

/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter.common
 * CreateAt : 2016/11/9
 * Describe :
 *
 * @author chendong
 */

public interface OnItemListener<D> {
    void onClick(int pos, BaseViewHolder holder, D data);

    void onLongPress(int pos, BaseViewHolder holder, D data);

    void onDoubleClick(int pos, BaseViewHolder holder, D data);
}
