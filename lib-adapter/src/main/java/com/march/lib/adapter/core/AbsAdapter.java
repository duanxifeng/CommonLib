package com.march.lib.adapter.core;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.march.lib.adapter.common.OnItemListener;
import com.march.lib.adapter.module.HFModule;
import com.march.lib.adapter.module.LoadMoreModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter.adapter
 * CreateAt : 2016/19/7
 * Describe : adapter基类，主要负责数据适配的相关逻辑，事件，module
 *
 * @author chendong
 */
public abstract class AbsAdapter<D>
        extends RecyclerView.Adapter<BaseViewHolder> {

    //每一项的Item的header类型
    public static final int TYPE_ITEM_HEADER = 0x10;
    //默认数据类型
    public static final int TYPE_ITEM_DEFAULT = 0x11;

    protected Context context;
    protected List<D> dateSets;
    protected LayoutInflater mLayoutInflater;
    protected RecyclerView mAttachRv;


    protected OnItemListener<D> itemListener;


    //加载更多模块
    protected LoadMoreModule mLoadMoreModule;
    //header+footer
    protected HFModule mHFModule;


    public AbsAdapter(Context context) {
        this.context = context;
        this.dateSets = new ArrayList<>();
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public AbsAdapter(Context context, List<D> datas) {
        this(context);
        this.dateSets = datas;
    }

    public AbsAdapter(Context context, D[] datas) {
        this(context);
        Collections.addAll(this.dateSets, datas);
    }

    public List<D> getDateSets() {
        return dateSets;
    }

    public void setItemListener(OnItemListener<D> itemListener) {
        this.itemListener = itemListener;
    }

    public Context getContext() {
        return context;
    }

        /*
         * 私有辅助方法
         */
    private View getInflateView(int resId, ViewGroup parent) {
        if (resId <= 0)
            return null;
        return mLayoutInflater.inflate(resId, parent, false);
    }

    private int judgePos(int pos) {
        if (mHFModule != null && mHFModule.isHasHeader()) {
            return pos - 1;
        } else {
            return pos;
        }
    }


    /*
     * 模块部分
     */
    public HFModule getHFModule() {
        return mHFModule;
    }

    public LoadMoreModule getLoadMoreModule() {
        return mLoadMoreModule;
    }

    public void addHFModule(HFModule hfModule) {
        this.mHFModule = hfModule;
        mHFModule.onAttachAdapter(this);
    }

    public void addLoadMoreModule(LoadMoreModule loadMoreModule) {
        this.mLoadMoreModule = loadMoreModule;
        mLoadMoreModule.onAttachAdapter(this);
    }


    /*
     * 绑定数据部分
     */
    @Override
    public BaseViewHolder<D> onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder = null;
        if (mHFModule != null)
            holder = mHFModule.getHFViewHolder(viewType);
        if (holder == null) {
            holder = new <D>BaseViewHolder(context,getInflateView(getLayout4Type(viewType), parent));
            int headerCount = mHFModule == null || !mHFModule.isHasHeader() ? 0 : 1;
            if (!dispatchClickEvent(holder, viewType))
                holder.setOnItemListener(headerCount,itemListener);
        }
        return holder;
    }


    //子类是否拦截点击事件,子类可以根据type拦截点击事件自己做特殊处理
    protected boolean dispatchClickEvent(BaseViewHolder holder, int viewType) {
        return false;
    }

    public boolean isFullSpan4GridLayout(int viewType) {
        return false;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (mHFModule == null) {
            int pos = judgePos(position);
            holder.getParentView().setTag(dateSets.get(pos));
            onBindView(holder, dateSets.get(pos), pos, getItemViewType(position));
        } else if (mHFModule.isHasFooter() && position == getItemCount() - 1) {
            onBindFooter(holder);
        } else if (mHFModule.isHasHeader() && position == 0) {
            onBindHeader(holder);
        } else {
            int pos = judgePos(position);
            holder.getParentView().setTag(dateSets.get(pos));
            onBindView(holder, dateSets.get(pos), pos, getItemViewType(position));
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mAttachRv = recyclerView;
        if (mLoadMoreModule != null)
            mLoadMoreModule.onAttachedToRecyclerView(recyclerView);
        if (mHFModule != null)
            mHFModule.onAttachedToRecyclerView(recyclerView);
    }

    public void notifyLayoutManagerChanged() {
        mAttachRv.setAdapter(this);
    }


    @Override
    public int getItemCount() {
        int pos = this.dateSets.size();
        if (mHFModule == null)
            return pos;
        if (mHFModule.isHasHeader())
            pos++;
        if (mHFModule.isHasFooter())
            pos++;
        return pos;
    }


    @Override
    public int getItemViewType(int position) {

        if (mHFModule == null)
            return getOriginItemType(position);

        //如果没有header没有footer直接返回
        if (!mHFModule.isHasHeader() && !mHFModule.isHasFooter())
            return getOriginItemType(position);

        //有header且位置0
        if (mHFModule.isHasHeader() && position == 0)
            return HFModule.TYPE_HEADER;

        //pos超出
        if (mHFModule.isHasFooter() && position == getItemCount() - 1)
            return HFModule.TYPE_FOOTER;

        //如果有header,下标减一个
        if (mHFModule.isHasHeader())
            return getOriginItemType(position - 1);
        else
            //没有header 按照原来的
            return getOriginItemType(position);
    }


    /**
     * 当是不是Header和Footer时，返回类型
     *
     * @param pos 位置
     * @return 类型
     */
    protected abstract int getOriginItemType(int pos);

    /**
     * @param viewType 根据类型返回不同的
     * @return 根据类型返回该类型的资源id
     */
    protected abstract int getLayout4Type(int viewType);

    /**
     * 绑定数据
     *
     * @param holder ViewHolder数据持有者
     * @param data   数据集
     * @param pos    数据集中的位置
     * @param type   类型
     */
    public abstract void onBindView(BaseViewHolder holder, D data, int pos, int type);

    /**
     * 绑定header的数据 和  监听
     *
     * @param header header holder
     */
    public void onBindHeader(BaseViewHolder header) {
    }

    /**
     * 绑定footer的数据和监听
     *
     * @param footer footer holder
     */
    public void onBindFooter(BaseViewHolder footer) {
    }

}
