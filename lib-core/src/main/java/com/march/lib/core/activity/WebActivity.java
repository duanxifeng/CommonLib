package com.march.lib.core.activity;

import android.os.Bundle;
import android.view.View;

import com.march.lib.core.R;
import com.march.lib.core.widget.EasyWebView;

/**
 * Project  : CommonLib
 * Package  : com.march.lib.core.activity
 * CreateAt : 2016/12/1
 * Describe :
 *
 * @author chendong
 */

public class WebActivity extends BaseActivity {

    private EasyWebView easyWebView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected boolean isInitTitle() {
        return false;
    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        easyWebView = getView(R.id.easy_web_view);
    }

    @Override
    public void onStartWorks() {
        super.onStartWorks();
        easyWebView.loadUrl("http://www.runoob.com/");
    }

    @Override
    public void onBackPressed() {
        if (!easyWebView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
