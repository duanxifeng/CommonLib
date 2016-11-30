package com.march.commonlib.view_test;


import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.march.commonlib.R;
import com.march.lib.core.activity.BaseActivity;
import com.march.lib.view.LeProgressView;

public class LeProgressViewTestActivity extends BaseActivity {

    TextView mProgressTv;
    LeProgressView mLpv;
    SeekBar mProgressSeekbar;


    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        mProgressTv = getView(R.id.tv);
        mLpv = getView(R.id.lpv);
        mProgressSeekbar = getView(R.id.seekbar);
        mProgressSeekbar.setMax(100);
    }

    @Override
    public void onInitEvents() {
        super.onInitEvents();
        mProgressSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mProgressTv.setText("进度：" + i);
                mLpv.prepareLoading(i * 1f / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_le_progress_activity;
    }

    @Override
    protected String[] getPermission2Check() {
        return new String[0];
    }

    @Override
    protected boolean isInitTitle() {
        return false;
    }

    public void loading(View view) {
        if (mLpv.getLoadPercent() < 1f) {
            Toast.makeText(mContext, "没有加载完", Toast.LENGTH_SHORT).show();
        } else {
            view.setEnabled(false);
            mProgressSeekbar.setEnabled(false);
            mLpv.startLoading();
        }
    }
}
