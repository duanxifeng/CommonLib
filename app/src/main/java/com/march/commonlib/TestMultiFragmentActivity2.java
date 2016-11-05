package com.march.commonlib;

import android.os.Bundle;
import android.support.v4.app.Fragment;;import com.march.lib.core.activity.BaseActivity;
import com.march.lib.core.common.FragmentHelper;

/**
 * com.march.commonlib
 * CommonLib
 * Created by chendong on 16/8/18.
 * Copyright © 2016年 chendong. All rights reserved.
 * Desc :
 */
public class TestMultiFragmentActivity2 extends BaseActivity  {

    private FragmentHelper multiFragmentHelper;

    @Override
    public void onStartWorks() {
        super.onStartWorks();
        multiFragmentHelper = new FragmentHelper(this, new FragmentHelper.SimpleFragmentOperator() {
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
        });
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
}
