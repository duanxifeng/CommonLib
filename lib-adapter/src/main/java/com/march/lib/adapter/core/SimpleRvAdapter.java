package com.march.lib.adapter.core;

import android.content.Context;

import java.util.List;

/**
 * Created by march on 16/7/2.
 * RvQuickAdapter的简化版本,可用于单类型适配
 */
public abstract class SimpleRvAdapter<D> extends BaseRvAdapter<D> {

    protected int resId;
    /**
     * 单类型适配
     *
     * @param context context
     * @param datas   数据源
     * @param res     layout资源
     */
    public SimpleRvAdapter(Context context, List<D> datas, int res) {
        super(context,datas);
        this.resId = res;
    }

    public SimpleRvAdapter(Context context, D[] datas, int res) {
        super(context,datas);
        this.resId = res;
    }

    /**
     * 当不是Header和Footer时，返回类型
     * @param pos 位置
     * @return 类型
     */
    @Override
    protected int getOriginItemType(int pos) {
        return 0;
    }

    @Override
    protected int getLayout4Type(int viewType) {
        return resId;
    }
}
