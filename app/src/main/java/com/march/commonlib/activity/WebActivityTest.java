package com.march.commonlib.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.march.commonlib.R;
import com.march.lib.core.activity.BaseActivity;
import com.march.lib.core.common.Logger;
import com.march.lib.core.widget.SimpleWebView;

/**
 * Project  : CommonLib
 * Package  : com.march.commonlib.activity
 * CreateAt : 2016/12/2
 * Describe :
 *
 * @author chendong
 */

public class WebActivityTest extends BaseActivity {
    private SimpleWebView simpleWebView;

    @Override
    protected int getLayoutId() {
        return R.layout.web_test_activity;
    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        simpleWebView = getView(R.id.webview);
        simpleWebView.loadUrl("http://www.runoob.com/");
//        webView.loadUrl("http://chendongmarch.github.io/");
//        webView.loadUrl("http://192.168.31.132:4000/");
        simpleWebView.getWebView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Logger.e("long click");
                return false;
            }
        });
        simpleWebView.getTitleLeftTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        simpleWebView.getTitleRightTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(WebActivityTest.this, "点击了更多", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected boolean isInitTitle() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!simpleWebView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
