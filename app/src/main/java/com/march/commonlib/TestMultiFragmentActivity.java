package com.march.commonlib;

import android.support.v4.app.Fragment;

import com.march.lib.core.activity.MultiFragmentActivity;


/**
 * com.march.commonlib
 * CommonLib
 * Created by chendong on 16/8/18.
 * Copyright © 2016年 chendong. All rights reserved.
 * Desc :
 */
public class TestMultiFragmentActivity extends MultiFragmentActivity {
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected boolean whenShowSameFragment(int showItem) {
        return false;
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @Override
    protected Fragment makeFragment(int showItem) {
        return null;
    }

    @Override
    protected void syncSelectState(int selectImage) {

    }

    @Override
    protected String[] getPermission2Check() {
        return new String[0];
    }

    @Override
    protected boolean isInitTitle() {
        return false;
    }
}
