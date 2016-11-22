package com.march.lib.adapter.module;

import android.support.v7.widget.RecyclerView;

import com.march.lib.adapter.core.AbsAdapter;


/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter.adapter
 * CreateAt : 2016/20/8
 * Describe : 模块基类
 *
 * @author chendong
 */
abstract class AbsModule {

    protected AbsAdapter mAttachAdapter;
    protected RecyclerView mAttachRecyclerView;

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.mAttachRecyclerView = recyclerView;
    }

    public void onAttachAdapter(AbsAdapter adapter) {
        this.mAttachAdapter = adapter;
    }
}
