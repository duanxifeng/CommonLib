package com.march.baselib.module;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.march.baselib.R;

/**
 * Project  : CommonLib <p>
 * Package  : com.march.baselib <p>
 * CreateAt : 16/8/15 <p>
 * Describe : 实现UI添加公共title操作<p>
 *
 * @author chendong <p>
 */
public class TitleModule {

    /**
     * 左边位置
     */
    public static final int POS_Left = 0;
    /**
     * 右边位置
     */
    public static final int POS_Right = 1;
    /**
     * 中间标题位置
     */
    public static final int POS_Center = 2;

    private TextView mLeftTv;
    private TextView mRightTv;
    private TextView mCenterTv;
    private ViewGroup mBarView;
    private Activity mActivity;

    /**
     * 构造函数
     * @param context 上下文
     */
    public TitleModule(Activity context) {
        this.mActivity = context;
        this.mBarView = (ViewGroup) mActivity.getLayoutInflater()
                .inflate(R.layout.widget_title_bar, null);
        mLeftTv = (TextView) mBarView.findViewById(R.id.tv_title_bar_left);
        mRightTv = (TextView) mBarView.findViewById(R.id.tv_title_bar_right);
        mCenterTv = (TextView) mBarView.findViewById(R.id.tv_title_bar_center);
        mLeftTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
    }


    /**
     * 获取某个位置的TextView
     * @param pos 位置
     * @return TextView
     */
    public TextView get(int pos) {
        switch (pos) {
            case POS_Left:
                return mLeftTv;
            case POS_Right:
                return mRightTv;
            case POS_Center:
                return mCenterTv;
        }
        return null;
    }

    /**
     * 给某个位置的TextViews设置文本
     * @param pos 位置
     * @param txt 文本
     */
    public void setText(int pos, String txt) {
        get(pos).setText(txt);
    }

    /**
     * 设置文本
     * @param leftTxt 左边显示文本，null时不显示
     * @param centerTxt 中间显示文本，null时不显示
     * @param rightTxt 右边显示文本，null时不显示
     */
    public void setText(String leftTxt, String centerTxt, String rightTxt) {
        if (leftTxt != null)
            mLeftTv.setText(leftTxt);
        if (centerTxt != null)
            mCenterTv.setText(centerTxt);
        if (rightTxt != null)
            mRightTv.setText(rightTxt);
    }

    /**
     * 给某个位置的TextView设置监听
     * @param pos 位置
     * @param listener 监听
     */
    public void setListener(int pos, View.OnClickListener listener) {
        get(pos).setOnClickListener(listener);
    }

    /**
     * 左边默认有返回监听，可清除
     */
    public void clearBackListener() {
        get(POS_Left).setOnClickListener(null);
    }

    /**
     * 隐藏title
     */
    public void hideTitle() {
        mBarView.setVisibility(View.GONE);
    }

    /**
     * 隐藏某个位置的显示
     * @param pos 位置
     */
    public void hide(int pos) {
        get(pos).setVisibility(View.GONE);
    }

    /**
     * 获取title
     * @return Title最外层ViewGroup
     */
    public ViewGroup getBarView() {
        return mBarView;
    }
}
