package com.march.lib.adapter.core;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.march.lib.adapter.R;
import com.march.lib.adapter.common.OnItemListener;

import java.lang.reflect.Field;


/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter.core
 * CreateAt : 2016/11/8
 * Describe : view holder
 *
 * @author chendong
 */
public class BaseViewHolder<D> extends RecyclerView.ViewHolder {

    private OnItemListener<D> itemListener;
    private SparseArray<View> cacheViews;

    private int mHeaderCount = 0;
    private View itemView;


    public BaseViewHolder(Context context, final View itemView) {
        super(itemView);
        this.itemView = itemView;
        cacheViews = new SparseArray<>(5);
        initItemEvent(context, itemView);
    }

    public View getParentView() {
        return itemView;
    }


    /**
     * 初始化事件
     *
     * @param context  上下文
     * @param itemView parent view
     */
    private void initItemEvent(Context context, final View itemView) {
        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (itemListener != null && itemListener.isSupportDoubleClick()) {
                    itemListener.onClick(getAdapterPosition() - mHeaderCount, BaseViewHolder.this, (D) itemView.getTag(R.id.adapter_data));
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (itemListener != null && !itemListener.isSupportDoubleClick()) {
                    itemListener.onClick(getAdapterPosition() - mHeaderCount, BaseViewHolder.this, (D) itemView.getTag(R.id.adapter_data));
                }
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (itemListener != null) {
                    itemListener.onDoubleClick(getAdapterPosition() - mHeaderCount, BaseViewHolder.this, (D) itemView.getTag(R.id.adapter_data));
                }
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (itemListener != null) {
                    itemListener.onLongPress(getAdapterPosition() - mHeaderCount, BaseViewHolder.this, (D) itemView.getTag(R.id.adapter_data));
                }
            }
        };
        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(context, gestureListener);
        itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }


    /**
     * 使用资源id找到view
     *
     * @param resId 资源id
     * @param <T>   泛型,View的子类
     * @return 返回泛型类
     */
    public <T extends View> T getView(int resId) {
        T v = (T) cacheViews.get(resId);
        if (v == null) {
            v = (T) itemView.findViewById(resId);
            if (v != null) {
                cacheViews.put(resId, v);
            }
        }
        return v;
    }

    /**
     * 使用类反射找到字符串id代表的view
     *
     * @param idName String类型ID
     * @return 返回
     */
    public View getView(String idName) {
        View view = null;
        if (idName != null) {
            Class<R.id> idClass = R.id.class;
            try {
                Field field = idClass.getDeclaredField(idName);
                field.setAccessible(true);
                int id = field.getInt(idClass);
                view = getView(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    void setOnItemListener(int mHeaderCount, OnItemListener<D> itemListener) {
        this.mHeaderCount = mHeaderCount;
        this.itemListener = itemListener;
    }

    /**
     * 设置是否可见
     *
     * @param resId   资源ID
     * @param visible visible
     * @return this
     */
    public BaseViewHolder setVisibility(int resId, int visible) {
        getView(resId).setVisibility(visible);
        return this;
    }

    /**
     * 为text view设置文本
     *
     * @param resId 控件资源id
     * @param txt   设置的文本
     * @return this
     */
    public BaseViewHolder setText(int resId, String txt) {
        ((TextView) getView(resId)).setText(txt);
        return this;
    }

    /**
     * 为ImageView使用图片资源id设置图片
     *
     * @param resId    控件资源id
     * @param imgResId 图片资源id
     * @return VH
     */
    public BaseViewHolder setImg(int resId, int imgResId) {
        ((ImageView) getView(resId)).setImageResource(imgResId);
        return this;
    }


    /**
     * 设置监听
     *
     * @param resId    资源id
     * @param listener 监听
     * @return this
     */
    public BaseViewHolder setClickLis(int resId, View.OnClickListener listener) {
        getView(resId).setOnClickListener(listener);
        return this;
    }
}
