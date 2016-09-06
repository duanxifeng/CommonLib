package com.march.commonlib;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.march.baselib.app.BaseApp;
import com.march.baselib.develop.DevelopLib;
import com.march.baselib.inter.HowLoadImg;

/**
 * com.march.commonlib
 * CommonLib
 * Created by chendong on 16/8/13.
 * Copyright © 2016年 chendong. All rights reserved.
 * Desc :
 */
public class MyApplication extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
        DevelopLib.initLib(this, new HowLoadImg() {
            @Override
            public void loadImg(ImageView iv, int w, int h, String path) {
                Glide.with(MyApplication.this).load(path).centerCrop().into(iv);
            }
        });
    }
}
