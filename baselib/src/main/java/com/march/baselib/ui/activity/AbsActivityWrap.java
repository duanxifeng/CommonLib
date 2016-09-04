package com.march.baselib.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.march.baselib.helper.PermissionHelper;
import com.march.baselib.helper.imagepicker.AbsImagePicker;
import com.march.baselib.module.TitleModule;

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
 * Describe :
 * 标准基类实现类，实现部分功能
 * 添加共有的title bar
 * 监测权限
 * 关联图片相册、相机选择裁剪
 *
 * @author chendong
 */
abstract class AbsActivityWrap extends AbsActivity {

    /**
     * title bar
     */
    protected TitleModule mTitleModule;

    private AbsImagePicker mPicker;


    @Override
    protected View getLayoutView() {
        ViewGroup rootView = null;
        if (isInitTitle()) {
            mTitleModule = new TitleModule(mActivity);
            rootView = mTitleModule.getBarView();
            getLayoutInflater().inflate(getLayoutId(), rootView, true);
        }
        return rootView;
    }

    /**
     * 关联图片获取
     * @param picker 图片选择弃
     */
    public void setPicker(AbsImagePicker picker) {
        this.mPicker = picker;
    }

    /**
     * 监测权限
     */
    @Override
    protected boolean checkPermission() {
        return PermissionHelper.checkPermission(mActivity, getPermission2Check());
    }

    //启动activity返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPicker != null) {
            mPicker.onActivityResult(requestCode, resultCode, data);
        }
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
