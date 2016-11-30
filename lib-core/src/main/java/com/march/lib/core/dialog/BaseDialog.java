package com.march.lib.core.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.march.lib.core.R;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : dialog基类
 *
 * @author chendong
 */
public abstract class BaseDialog extends AppCompatDialog {
    /**
     * match_parent
     */
    protected int MATCH = ViewGroup.LayoutParams.MATCH_PARENT;
    /**
     * wrap_content
     */
    protected int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public BaseDialog(Context context) {
        this(context,R.style.dialog_theme);
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param theme   主题
     */
    public BaseDialog(Context context, int theme) {
        super(context, theme);
        setContentView(getLayoutId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setWindowParams();
    }

    protected abstract void initViews();

    protected abstract int getLayoutId();

    protected abstract void setWindowParams();

    protected void setBottomToCenterAnimation() {
        Window window = getWindow();
        if (window == null)
            return;
        window.setWindowAnimations(R.style.dialog_anim_bottom_to_center);
    }

    protected void buildDefaultParams(int width, int height, float alpha, float dim, int gravity) {
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        Window window = getWindow();
        if (window == null)
            return;
        WindowManager.LayoutParams params = window.getAttributes();
        // setContentView设置布局的透明度，0为透明，1为实际颜色,该透明度会使layout里的所有空间都有透明度，不仅仅是布局最底层的view
        params.alpha = alpha;
        // 窗口的背景，0为透明，1为全黑
        params.dimAmount = dim;
        params.width = width;
        params.height = height;
        params.gravity = gravity;
        window.setAttributes(params);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    protected void buildDefaultParams(int width, int height, int gravity) {
        buildDefaultParams(width, height, 1f, .6f, gravity);
    }

    protected void buildDefaultParams() {
        buildDefaultParams(WRAP, WRAP, 1f, .6f, Gravity.CENTER);
    }

    protected <V extends View> V getView(int id) {
        return (V) findViewById(id);
    }
}
