package com.march.lib.adapter.core;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter.adapter
 * CreateAt : 2016/11/8
 * Describe : adapter基类，主要负责adapterId区分，数据更新等方法
 *
 * @author chendong
 */
abstract class BaseRvAdapter<D> extends AbsAdapter<D> {

    private long adapterId;
    private int preDataCount;

    public BaseRvAdapter(Context context) {
        super(context);
        init();
    }

    public BaseRvAdapter(Context context, List<D> datas) {
        super(context, datas);
        init();
    }

    public BaseRvAdapter(Context context, D[] datas) {
        super(context, datas);
        init();
    }


    ///////////////////////////////////// adapter id /////////////////////////////////////

    private void init() {
        adapterId = System.currentTimeMillis();
        refreshPreDataCount();
    }

    public boolean isThisAdapter(RecyclerView rv) {
        if (rv == null) {
            throw new IllegalArgumentException("RecyclerView没有初始化,为null");
        }
        if (rv.getAdapter() instanceof AbsAdapter) {
            throw new IllegalArgumentException("RecyclerView使用的Adapter不是RvAdapter或其子类");
        }
        return ((BaseRvAdapter) rv.getAdapter()).adapterId == adapterId;
    }


    ///////////////////////////////////// 数据更新 /////////////////////////////////////

    public void insert(int pos, D data) {
        this.dateSets.add(pos, data);
        notifyItemInserted(pos);
    }

    /**
     * 全部更新数据
     *
     * @param data     数据
     * @param isUpdate 是否调用更新
     */
    public void notifyDataSetChanged(List<D> data, boolean isUpdate) {
        this.dateSets = data;
        if (isUpdate) {
            notifyDataSetChanged();
        }
    }

    /**
     * @param data      数据
     * @param isAllData 是不是全部数据，true 将
     */
    public void appendTailRangeData(List<D> data, boolean isAllData) {
        if (isAllData)
            this.dateSets = data;
        else
            this.dateSets.addAll(data);
        notifyItemRangeInserted(preDataCount, this.dateSets.size() - preDataCount);
        refreshPreDataCount();
    }


    private void refreshPreDataCount() {
        this.preDataCount = dateSets.size();
    }
}
