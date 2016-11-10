package com.march.lib.adapter.helper;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;

import com.march.lib.adapter.core.AbsAdapter;


/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter.helper
 * CreateAt : 2016/11/8
 * Describe :
 *
 * @author chendong
 */

public class CommonHelper {

    /**
     * 检测type是否合法
     *
     * @param type 类型
     * @return 合法
     */
    public static boolean checkTypeValid(int type) {
        if (type == AbsAdapter.TYPE_ITEM_DEFAULT || type == AbsAdapter.TYPE_ITEM_HEADER) {
            throw new IllegalArgumentException("type not valid,type is " + type);
        }
        return true;
    }

    public interface CheckFullSpanHandler {
        boolean isFullSpan(int viewType);
    }

    /**
     * 处理GridLayoutManager跨越整行
     * @param rv recyclerView
     * @param absAdapter adapter
     * @param handler 接口
     */
    public static void handleGridLayoutManager(RecyclerView rv, final AbsAdapter absAdapter, final CheckFullSpanHandler handler) {
        final RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
        if (!(layoutManager instanceof GridLayoutManager))
            return;
        // 针对GridLayoutManager处理
        final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (handler.isFullSpan(absAdapter.getItemViewType(position))) {
                    return gridLayoutManager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });

        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

        };
    }
}
