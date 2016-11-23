package com.march.commonlib;

import android.app.Application;

import com.march.lib.support.helper.PathHelper;


/**
 * com.march.commonlib
 * CommonLib
 * Created by chendong on 16/8/13.
 * Copyright © 2016年 chendong. All rights reserved.
 * Desc :
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PathHelper.initPath(this, "TestPath");
    }
}
