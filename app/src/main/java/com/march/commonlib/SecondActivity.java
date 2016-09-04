package com.march.commonlib;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.march.baselib.helper.DimensionHelper;
import com.march.baselib.helper.Logger;
import com.march.baselib.ui.activity.BaseActivity;
import com.march.quickrvlibs.RvViewHolder;
import com.march.quickrvlibs.SimpleRvAdapter;
import com.march.quickrvlibs.inter.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private List<String> datas;
    private GestureDetector gestureDetector;
    private float totalY;
    private float size;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_second;
    }

    @Override
    protected void onInitDatas() {
        super.onInitDatas();
        datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add(i + "");
        }
    }

    @Override
    protected String[] getPermission2Check() {
        return new String[0];
    }

    @Override
    protected void onInitViews(final Bundle save) {
        super.onInitViews(save);
        gestureDetector = new GestureDetector(mContext, new DiyGestureListener(), null, true);
        recyclerView = getView(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        SimpleRvAdapter<String> adapter = new SimpleRvAdapter<String>(mContext, datas, R.layout.item) {
            @Override
            public void onBindView(RvViewHolder holder, String data, int pos, int type) {
                View iv = holder.getView(R.id.iv);
                holder.setText(R.id.tv, pos + "");
                size = DimensionHelper.getScreenWidth() / 3.0f;
                iv.getLayoutParams().width = (int) size;
                iv.getLayoutParams().height = (int) size;
                holder.getParentView().setTag(pos);
            }
        };
        adapter.setOnItemClickListener(new OnItemClickListener<String>() {
            @Override
            public void onItemClick(int pos, RvViewHolder holder, String data) {
                Logger.e("click " + pos);
            }
        });
        recyclerView.setAdapter(adapter);

        totalY = 0;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Logger.e("onScrollStateChanged = " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalY += dy;
                Logger.e("onScrolled : " + " y = " + totalY);
            }
        });

        //如果横向滑动,rv禁止,根据滑动位置选中


        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                Logger.e("onChildViewAttachedToWindow   " + view.getTag());
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Logger.e("onChildViewDetachedFromWindow   " + view.getTag());
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Logger.e("当前事件 " + ev.getAction());
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            isFling = false;
        }
        if (isFling)
            return gestureDetector.onTouchEvent(ev);

        return recyclerView.dispatchTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
    }

    private boolean isFling = false;

    /**
     * 自定义滑动监听
     */
    class DiyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            isFling = false;
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Logger.e("onFling");
            isFling = true;
            //是否允许截断滑动事件
            if (Math.abs(velocityX) > 1000 || Math.abs(velocityY) > 1000) {
                //空白区域触发
                float minMove = 120;         //最小滑动距离
                float minVelocity = 1000;      //最小滑动速度
                float beginX = e1.getX();
                float endX = e2.getX();
                float beginY = e1.getY();
                float endY = e2.getY();
//                if (beginY - endY > minMove && Math.abs(velocityY) > minVelocity) {   //上滑
//                    FlingTop();
//                    return true;
//                } else if (endY - beginY > minMove && Math.abs(velocityY) > minVelocity) {   //下滑
//                    FlingBottom();
//                    return true;
//                } else
                if (beginX - endX > minMove && Math.abs(velocityX) > minVelocity) {   //左滑
                    FlingLeft();
                    return true;
                } else if (endX - beginX > minMove && Math.abs(velocityX) > minVelocity) {   //右滑
                    FlingRight();
                    return true;
                }
                return true;
            }
            return false;
        }
    }

    protected void FlingLeft() {
        Logger.e("界面左滑");
    }

    protected void FlingRight() {
        Logger.e("界面右滑");
    }

    protected void FlingTop() {
        Logger.e("界面上滑");
    }

    protected void FlingBottom() {
        Logger.e("界面下滑");
    }


    @Override
    protected boolean isInitTitle() {
        return false;
    }
}
