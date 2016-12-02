package com.march.lib.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Project  : CommonLib
 * Package  : com.march.lib.core.widget
 * CreateAt : 2016/12/1
 * Describe :
 *
 * @author chendong
 */

public class MyScrollView extends ScrollView {
    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    private OnScrollListener onScrollListener;

    public interface OnScrollListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }
}
