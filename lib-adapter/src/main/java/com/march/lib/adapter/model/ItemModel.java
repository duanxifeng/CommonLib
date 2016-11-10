package com.march.lib.adapter.model;

import com.march.lib.adapter.common.ITypeAdapterModel;

/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter.adapter
 * CreateAt : 2016/4/8
 * Describe : 数据封装，将多种不同数据封装为ItemModel进行适配
 *
 * @author chendong
 */
public class ItemModel<D> implements ITypeAdapterModel {

    private D t;
    private int type;

    public ItemModel(D t) {
        this.t = t;
        if (t instanceof ITypeAdapterModel) {
            this.type = ((ITypeAdapterModel) t).getRvType();
        }
    }

    public ItemModel(D t, int type) {
        this.t = t;
        if (t instanceof ITypeAdapterModel) {
            this.type = ((ITypeAdapterModel) t).getRvType();
        } else {
            this.type = type;
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public D get() {
        return t;
    }

    public void put(D t) {
        this.t = t;
    }

    @Override
    public int getRvType() {
        return type;
    }
}
