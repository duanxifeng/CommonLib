package com.march.baselib.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * com.march.baselib.ui.activity
 * CommonLib
 * Created by chendong on 16/8/17.
 * Copyright © 2016年 chendong. All rights reserved.
 * Desc :
 */
/**
 * Project  : CommonLib </br>
 * Package  : com.march.baselib </br>
 * CreateAt : 16/8/15 </br> </br>
 * Describe : 向外公开的基类。实现部分简化操作的方法 </br>
 *
 * @author chendong </br>
 */

public abstract class BaseActivity extends AbsActivityWrap {

    /**
     * 启动activity
     * @param cls 被启动的activity
     */
    protected void startActivity(Class cls) {
        startActivity(new Intent(mContext, cls));
    }

    /**
     * 简化获取View
     * @param id view id
     * @param <V> View范型
     * @return 控件
     */
    protected <V extends View> V getView(int id) {
        return (V) findViewById(id);
    }

    /**
     * 监测并显示dialog
     * @param dialog 对话框
     * @return 返回true时表示已经显示，返回false表示没有初始化
     */
    protected boolean checkDialog2Show(Dialog dialog) {
        if (dialog == null)
            return false;
        if (!dialog.isShowing()) {
            dialog.show();
        }
        return true;
    }
}
