package com.march.lib.adapter.module;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.march.lib.adapter.core.BaseViewHolder;
import com.march.lib.adapter.helper.CommonHelper;

/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter.module
 * CreateAt : 16/8/20
 * Describe : Header模块
 *
 * @author chendong
 */
public class HFModule extends AbsModule {

    public static final int TYPE_HEADER = -1;
    public static final int TYPE_FOOTER = -2;
    public static final int NO_RES = 0;

    private int headerHeight, footerHeight;
    private View headerView;
    private View footerView;
    private boolean isStaggeredGridLayoutManager = false;
    private boolean isHeaderEnable = true;
    private boolean isFooterEnable = true;


    public HFModule(Context context, int mHeaderRes, int mFooterRes, RecyclerView recyclerView) {
        if (mHeaderRes != NO_RES) {
            headerView = LayoutInflater.from(context).inflate(mHeaderRes, recyclerView, false);
            headerHeight = headerView.getLayoutParams().height;
        }
        if (mFooterRes != NO_RES) {
            footerView = LayoutInflater.from(context).inflate(mFooterRes, recyclerView, false);
            footerHeight = footerView.getLayoutParams().height;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (!isHasFooter() && !isHasFooter())
            return;

        //如果是StaggeredGridLayoutManager,放在创建ViewHolder里面来处理
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof StaggeredGridLayoutManager) {
            isStaggeredGridLayoutManager = true;

        } else {
            CommonHelper.handleGridLayoutManager(recyclerView, mAttachAdapter, new CommonHelper.CheckFullSpanHandler() {
                @Override
                public boolean isFullSpan(int viewType) {
                    return viewType == TYPE_HEADER || viewType == TYPE_FOOTER;
                }
            });
        }
    }

    public boolean isHasHeader() {
        return headerView != null;
    }

    public boolean isHasFooter() {
        return footerView != null;
    }

    public void setFooterEnable(boolean footerEnable) {
        if (footerView == null)
            return;
        isFooterEnable = footerEnable;
        if (isFooterEnable) {
            footerView.getLayoutParams().height = footerHeight;
            footerView.setVisibility(View.VISIBLE);
        } else {
            footerView.getLayoutParams().height = 0;
            footerView.setVisibility(View.GONE);
            mAttachAdapter.notifyItemChanged(mAttachAdapter.getItemCount() - 1);
        }
    }

    public void setHeaderEnable(boolean headerEnable) {
        if (headerView == null)
            return;
        isHeaderEnable = headerEnable;
        if (isHeaderEnable) {
            headerView.getLayoutParams().height = headerHeight;
            headerView.setVisibility(View.VISIBLE);
        } else {
            headerView.getLayoutParams().height = 0;
            headerView.setVisibility(View.GONE);
            mAttachAdapter.notifyItemChanged(0);
        }
    }


    public BaseViewHolder getHFViewHolder(int viewType) {
        BaseViewHolder holder = null;
        boolean isFooter = isHasFooter() && viewType == TYPE_FOOTER;
        boolean isHeader = isHasHeader() && viewType == TYPE_HEADER;
        if (isFooter) {
            holder = new BaseViewHolder(mAttachAdapter.getContext(),footerView);
        } else if (isHeader) {
            holder = new BaseViewHolder(mAttachAdapter.getContext(),headerView);
        }
        if (isStaggeredGridLayoutManager && (isFooter || isHeader)) {
            ViewGroup.LayoutParams originLp = holder.getParentView().getLayoutParams();
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams
                            (originLp.width,
                                    originLp.height);
            layoutParams.setFullSpan(true);
            holder.getParentView().setLayoutParams(layoutParams);
        }
        return holder;
    }

    public View getFooterView() {
        return footerView;
    }

    public View getHeaderView() {
        return headerView;
    }
}
