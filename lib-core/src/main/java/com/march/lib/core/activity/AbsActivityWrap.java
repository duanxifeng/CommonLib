package com.march.lib.core.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.march.lib.core.common.PermissionHelper;
import com.march.lib.core.widget.TitleBarView;

import java.util.Map;

/**
 * com.march.baselib.ui.activity
 * CommonLib
 * Created by chendong on 16/8/13.
 * Copyright © 2016年 chendong. All rights reserved.
 * Desc :
 */

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : 基类，添加title和权限检测
 *
 * @author chendong
 */
abstract class AbsActivityWrap extends AbsActivity {
    /**
     * title bar
     */
    protected TitleBarView mTitleBarView;

    @Override
    protected View getLayoutView() {
        if (isInitTitle()) {
            mTitleBarView = new TitleBarView(mActivity);
            getLayoutInflater().inflate(getLayoutId(), mTitleBarView, true);
        }
        return mTitleBarView;
    }

    /**
     * 创建Intent,由于共用了ILife加载周期，要使用该方法构建Intent，所有数据
     * @param context 上下文
     * @param cls 启动的class
     * @param bundle 数据存储bundle
     * @return 构建之后的Intent
     */
    protected static Intent buildIntent(Context context, Class cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(INTENT_DEFAULT_DATA, bundle);
        return intent;
    }

    /**
     * 监测权限
     */
    @Override
    protected boolean checkPermission() {
        return PermissionHelper.checkPermission(mActivity, getPermission2Check());
    }


    //监测权限返回结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionHelper.OnPermissionHandleOverListener() {
            @Override
            public void onHandleOver(boolean isOkExactly, Map<String, Integer> result) {
                //权限ok或者子类要求直接执行
                if (isOkExactly || handlePermissionResult(result))
                    invokeCommonMethod(mSaveBundle);
            }
        });
    }

    protected abstract String[] getPermission2Check();

    protected boolean handlePermissionResult(Map<String, Integer> resultNotOk) {
        return true;
    }

    protected abstract boolean isInitTitle();
}
