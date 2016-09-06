package com.march.baselib.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.march.baselib.helper.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Project  : SlidingCheck
 * Package  : com.march.slidingcheck
 * CreateAt : 16/9/6
 * Describe : 仿扣扣滑动选中照片
 *
 * @author chendong
 */
public class SlidingCheckLayout extends FrameLayout {

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.AdapterDataObserver mDataChangedObserver;

    public SlidingCheckLayout(Context context) {
        this(context, null);
    }

    public SlidingCheckLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private static final float TOUCH_SLOP_RATE = 0.25f;
    // 初始化值
    private static final int INVALID_PARAM = -1;
    // 滑动选中监听
    private OnSlidingCheckListener onSlidingCheckListener;
    // 兼容滑动监听
    private RecyclerView.OnScrollListener innerOnScrollListener;

    private int offsetTop = 0;
    // rv在y轴滑动的距离
    private int mTotalScrollY = 0;
    // 横向的item数量
    private int itemSpanCount = INVALID_PARAM;
    // 数据量
    private int mDataCount = INVALID_PARAM;
    // 内部的rv
    private RecyclerView mTargetRv;
    // 是否支持7型手势操作
    private boolean isSupport7Gesture = false;
    // item宽度，默认屏幕宽度／span count
    private int itemWidth;
    // item高度，默认等于宽度，可以配置
    private int itemHeight;
    // 横轴滑动阈值，超过阈值表示触发横轴滑动
    private float xTouchSlop;
    // 纵轴滑动阈值，超过阈值表示触发纵轴滑动
    private float yTouchSlop;
    // down 事件初始值
    private float mInitialDownX;
    // down 事件初始值
    private float mInitialDownY;
    // 是否正在滑动
    private boolean isBeingSlide;
    // 行 值
    private int mColumn;
    // 列 值
    private int mRow;
    // 横向滑动距离
    private float xSlidingDist = 0;
    // 纵向滑动距离
    private float ySlidingDist = 0;
    // 上次触摸的位置
    private int preTouchPos = INVALID_PARAM;
    // 上次触摸的行数
    private int preColumn;
    // 上次触摸的列数
    private int preRow;

    private int initRow;
    private int initColumn;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled())
            return super.onInterceptTouchEvent(ev);
        ensureTarget();
        ensureLayoutManager();
        ensureAdapter();
        if (!isReadyToIntercept())
            return super.onInterceptTouchEvent(ev);
        int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // init
                mInitialDownX = ev.getX();
                mInitialDownY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                // stop
                isBeingSlide = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // handle
                float xDiff = Math.abs(ev.getX() - mInitialDownX);
                float yDiff = Math.abs(ev.getY() - mInitialDownY);
                if (yDiff < xTouchSlop && xDiff > yTouchSlop) {
                    isBeingSlide = true;
                    initRow = generateRow(ev.getX());
                    initColumn = generateColumn(ev.getY());
                }
                break;
        }
        return isBeingSlide;
    }

    private int generateRow(float x) {
        return (int) (x / itemWidth);
    }

    private int generateColumn(float y) {
        return (int) ((y + mTotalScrollY - offsetTop) / itemHeight);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                // stop
                isBeingSlide = false;
                xSlidingDist = 0;
                ySlidingDist = 0;
                preTouchPos = INVALID_PARAM;
                preRow = INVALID_PARAM;
                preColumn = INVALID_PARAM;
                break;
            case MotionEvent.ACTION_MOVE:
                handleMoveEvent(ev);
                break;
        }
        return isBeingSlide;
    }


    /**
     * 处理滑动手势
     *
     * @param ev move事件
     */
    private void handleMoveEvent(MotionEvent ev) {
        xSlidingDist = ev.getX();
        ySlidingDist = ev.getY();
        mRow = generateRow(xSlidingDist);
        if (mRow >= itemSpanCount)
            return;
        mColumn = generateColumn(ySlidingDist);
        int pos = (mRow + 3 * mColumn);
        if (onSlidingCheckListener == null || pos == preTouchPos)
            return;
        preTouchPos = pos;
        // 处理垂直滑动事件，如果支持7型手势，垂直滑动变为选中多张照片
        if (preRow != INVALID_PARAM && preRow == mRow && isSupport7Gesture) {
            // 先右划固定后上下滑选择
            if (mRow > initRow) {
                // 7型手势向下滑动
                if (preColumn < mColumn)
                    for (int row = initRow; row <= mRow; row++)
                        for (int col = preColumn + 1; col <= mColumn; col++)
                            publishSlidingCheck(col, row);
                // 7型手势向上划
                if (preColumn > mColumn)
                    for (int row = initRow; row <= mRow; row++)
                        for (int col = preColumn - 1; col >= mColumn; col--)
                            publishSlidingCheck(col, row);
            }
            //先左滑后上下滑
            else {
                // 7型手势向下滑动
                if (preColumn < mColumn)
                    for (int row = mRow; row <= initRow; row++)
                        for (int col = preColumn + 1; col <= mColumn; col++)
                            publishSlidingCheck(col, row);
                // 7型手势向上划
                if (preColumn > mColumn)
                    for (int row = mRow; row <= initRow; row++)
                        for (int col = preColumn - 1; col >= mColumn; col--)
                            publishSlidingCheck(col, row);
            }
        }
        // 简单滑动事件
        else {
            publishSlidingCheck(mColumn, mRow);
        }
        preColumn = mColumn;
        preRow = mRow;
    }

    /**
     * 计算位置,根据行列值计算，比如
     *     0 1 2(row)
     *
     * 0   0 1 2
     * 1   3 4 5
     * 2   6 7 8
     * (col)
     * 7 ＝ 1(row) + 3 * 2(col)
     * @param col 行
     * @param row 列
     * @return
     */
    private int calculatePosition(int col, int row) {
        return row + itemSpanCount * col;
    }

    /**
     * 公开滑动选中事件
     *
     * @param col 行
     * @param row 列
     */
    private void publishSlidingCheck(int col, int row) {
        int pos = calculatePosition(col, row);
        if (onSlidingCheckListener != null && pos < mDataCount) {
            onSlidingCheckListener.onSlidingCheck(pos);
        }
    }

    /**
     * 是否可以开始拦截处理事件，当recyclerView数据完全ok之后开始
     *
     * @return 是否可以开始拦截处理事件
     */
    private boolean isReadyToIntercept() {
        return mTargetRv != null && mTargetRv.getAdapter() != null && itemSpanCount != INVALID_PARAM;
    }

    /**
     * 获取RecyclerView
     */
    private void ensureTarget() {
        if (mTargetRv != null)
            return;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof RecyclerView) {
                mTargetRv = (RecyclerView) childAt;
                initRecyclerView();
                return;
            }
        }
    }

    /**
     * 为RecyclerView设置监听事件
     */
    private void initRecyclerView() {
        mTargetRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalScrollY += dy;
                if (innerOnScrollListener != null) {
                    innerOnScrollListener.onScrolled(recyclerView, dx, dy);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (innerOnScrollListener != null) {
                    innerOnScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            }
        });
    }

    /**
     * 监测是否配置adapter
     */
    public void ensureAdapter() {
        if (mTargetRv == null || mDataCount != INVALID_PARAM)
            return;
        mAdapter = mTargetRv.getAdapter();
        if (mAdapter == null)
            return;
        mDataCount = mAdapter.getItemCount();
        mDataChangedObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                ensureAdapter();
            }
        };
        mAdapter.registerAdapterDataObserver(mDataChangedObserver);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAdapter.unregisterAdapterDataObserver(mDataChangedObserver);
    }

    /**
     * 换LayoutManager需要调用
     * 获取itemCount,初始化item的高度和宽度
     */
    public void ensureLayoutManager() {
        if (mTargetRv == null || itemSpanCount != INVALID_PARAM)
            return;
        RecyclerView.LayoutManager lm = mTargetRv.getLayoutManager();
        if (lm == null)
            return;
        if (lm instanceof GridLayoutManager) {
            GridLayoutManager glm = (GridLayoutManager) lm;
            itemSpanCount = glm.getSpanCount();
            int size = (int) (getResources().getDisplayMetrics().widthPixels / (itemSpanCount * 1.0f));
            itemWidth = itemHeight = size;
            xTouchSlop = yTouchSlop = size * TOUCH_SLOP_RATE;
        } else {
            throw new IllegalStateException("only support grid layout manager now !");
        }
    }


    /**
     * 由于内部封闭了OnScrollListener，对外开放一个兼容方法
     *
     * @param innerOnScrollListener OnScrollListener
     */
    public void addOnScrollListener(RecyclerView.OnScrollListener innerOnScrollListener) {
        this.innerOnScrollListener = innerOnScrollListener;
    }

    /**
     * 设置item的高度，默认与宽度相同，可以自行设置。
     *
     * @param itemHeight 高度
     */
    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
        this.yTouchSlop = itemHeight * TOUCH_SLOP_RATE;
    }

    public void setOffsetTop(int offsetTop) {
        this.offsetTop = offsetTop;
    }

    public void setSupport7Gesture(boolean support7Gesture) {
        isSupport7Gesture = support7Gesture;
    }

    public void setOnSlidingCheckListener(OnSlidingCheckListener onSlidingCheckListener) {
        this.onSlidingCheckListener = onSlidingCheckListener;
    }

    public interface OnSlidingCheckListener {
        void onSlidingCheck(int pos);
    }
}
