package com.march.commonlib;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.march.lib_base.activity.BaseActivity;
import com.march.lib_helper.helper.MultiFragmentHelper;
import com.march.lib_helper.inter.MultiFragmentOperator;

;

/**
 * com.march.commonlib
 * CommonLib
 * Created by chendong on 16/8/18.
 * Copyright © 2016年 chendong. All rights reserved.
 * Desc :
 */
public class TestMultiFragmentActivity2 extends BaseActivity implements MultiFragmentOperator {

    private MultiFragmentHelper multiFragmentHelper;

    @Override
    public void onStartWorks() {
        super.onStartWorks();
        multiFragmentHelper = new MultiFragmentHelper(this, this, mSaveBundle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        multiFragmentHelper.onSaveInstanceState(outState);
    }


    @Override
    protected String[] getPermission2Check() {
        return new String[0];
    }

    @Override
    protected boolean isInitTitle() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public boolean whenShowSameFragment(int showItem) {
        return false;
    }

    @Override
    public int getFragmentContainerId() {
        return 0;
    }

    @Override
    public Fragment makeFragment(int showItem) {
        return null;
    }

    @Override
    public void syncSelectState(int selectImage) {

    }
}
