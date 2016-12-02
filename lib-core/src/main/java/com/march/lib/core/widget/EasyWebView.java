package com.march.lib.core.widget;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.march.lib.core.R;
import com.march.lib.core.common.DimensionHelper;
import com.march.lib.core.common.Logger;

import java.util.Map;

/**
 * Project  : CommonLib
 * Package  : com.march.lib.core.widget
 * CreateAt : 2016/12/1
 * Describe :
 *
 * @author chendong
 */

public class EasyWebView extends FrameLayout {

    private float mToolbarSize;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLy;
    private WebView mWebView;
    private TextView mTitleTv;

    private View mToolLy;
    private MyScrollView mScrollView;
    private boolean isShowTitleEveryUpScroll = true;

    private ObjectAnimator showAnimator, hideAnimator;
    private boolean isShown = true;

    private Map<String, String> headers;

    private WebViewHandler mWebViewHandler;

    public void setWebViewHandler(WebViewHandler mWebViewHandler) {
        this.mWebViewHandler = mWebViewHandler;
    }

    public interface WebViewHandler {
        void onLoadUrl(WebView webView, String url);
    }

    public EasyWebView(Context context) {
        this(context, null);
    }

    public EasyWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.widget_easy_webview, this, true);
        mToolbarSize = DimensionHelper.dp2px(context, 50);
        initViews();
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void loadUrl(String url) {
        Logger.e("loadUrl");
        mSwipeRefreshLy.setRefreshing(false);
        mSwipeRefreshLy.setRefreshing(true);
        if (headers == null) {
            mWebView.loadUrl(url);
        } else {
            mWebView.loadUrl(url, headers);
        }
    }

    public WebView getWebView() {
        return mWebView;
    }

    private void initViews() {
        mProgressBar = getView(R.id.widget_web_progress);
        mSwipeRefreshLy = getView(R.id.widget_web_srl);
        mWebView = getView(R.id.widget_web_webview);
        mSwipeRefreshLy.setColorSchemeColors(Color.BLACK);
        mTitleTv = getView(R.id.widget_web_title);
        mToolLy = getView(R.id.widget_tool_ly);
        mScrollView = getView(R.id.widget_web_scroll);
        setWebSetting();

        mSwipeRefreshLy.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        mSwipeRefreshLy.setProgressViewOffset(true, DimensionHelper.dp2px(getContext(), 20), DimensionHelper.dp2px(getContext(), 80));

        mScrollView.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
                if (oldTop <= top) {
                    //往上
                    if (isShown && top >= mToolbarSize) {
                        hideTitleBar();
                    }
                } else {
                    //往下
                    if (isShown)
                        return;
                    if (isShowTitleEveryUpScroll) {
                        showTitleBar();
                    } else {
                        if (top <= mToolbarSize) {
                            showTitleBar();
                        }
                    }
                }
            }
        });
    }

    private void showTitleBar() {
        if (isShown)
            return;
        checkAnimatorState();

        if (showAnimator == null) {
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("alpha", 0f,
                    1f);
            PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("translationY", -mToolbarSize,
                    0);
            showAnimator = ObjectAnimator.ofPropertyValuesHolder(mToolLy, pvhY, pvhZ).setDuration(200);
            showAnimator.setInterpolator(new DecelerateInterpolator());
        }
        showAnimator.start();
        isShown = true;
    }

    private void hideTitleBar() {
        if (!isShown)
            return;
        checkAnimatorState();
        if (hideAnimator == null) {
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("alpha", 1f,
                    0f);
            PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("translationY", 0f,
                    -mToolbarSize);
            hideAnimator = ObjectAnimator.ofPropertyValuesHolder(mToolLy, pvhY, pvhZ).setDuration(200);
            hideAnimator.setInterpolator(new DecelerateInterpolator());
        }
        hideAnimator.start();
        isShown = false;
    }

    private void checkAnimatorState() {
        if (hideAnimator != null && hideAnimator.isRunning()) {
            hideAnimator.end();
        }
        if (showAnimator != null && showAnimator.isRunning()) {
            showAnimator.end();
        }
    }

    private void setWebSetting() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (mProgressBar.getVisibility() == View.GONE) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTitleTv.setText(title);
            }

        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showTitleBar();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mSwipeRefreshLy.isRefreshing()) {
                            Logger.e("onProgressChanged  ");
                            mSwipeRefreshLy.setRefreshing(false);
                        }
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        initWebViewSetting(mWebView);
    }

    public void initWebViewSetting(WebView mProgressWebView) {
        WebSettings mWebSettings = mProgressWebView.getSettings();

        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebSettings.setDisplayZoomControls(false);
        }

        //允许js交互,允许中文编码
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //DOM Storage
        mWebSettings.setDomStorageEnabled(true);
        //是否使用缓存
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setDefaultTextEncodingName("UTF-8");
        //是否显示缩放按钮，默认false
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setSupportZoom(true);
        //设置此属性，可任意比例缩放。大视图模式
        mWebSettings.setUseWideViewPort(true);
        //和setUseWideViewPort(true)一起解决网页自适应问题
        mWebSettings.setLoadWithOverviewMode(true);

        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mProgressWebView.setHapticFeedbackEnabled(false);
    }


    // 返回键
    public boolean onBackPressed() {
        if (mSwipeRefreshLy.isRefreshing()) {
            mSwipeRefreshLy.setRefreshing(false);
            mWebView.stopLoading();
            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(GONE);
            return true;
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    private <V extends View> V getView(int id) {
        return (V) findViewById(id);
    }
}
