package com.march.lib.adapter.common;

import com.march.lib.adapter.core.AbsAdapter;

/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter.inter
 * CreateAt : 16/8/31
 *
 * Describe : 每一项的header,作为header的数据类型实现该类
 *
 * @author chendong
 */
public abstract class AbsSectionHeader implements IAdapterModel {
    @Override
    public int getRvType() {
        return AbsAdapter.TYPE_ITEM_HEADER;
    }
}
