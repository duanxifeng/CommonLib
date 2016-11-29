package com.march.commonlib.view_test;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.march.commonlib.R;
import com.march.lib.core.activity.BaseActivity;
import com.march.lib.core.widget.TitleBarView;
import com.march.lib.view.LockView;

public class LockViewActivity extends BaseActivity {

    private LockView lockview;

    @Override
    protected boolean isInitTitle() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_lock_activity;
    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        mTitleBarView.setText(TitleBarView.POS_Center,"九宫格解锁");
        lockview = getView(R.id.lockview);
        lockview.setListener(new LockView.OnLockFinishListener() {
            @Override
            public boolean onFinish(LockView lockView, String passWd, int passWsLength) {
                if (passWsLength < 5) {
                    Toast.makeText(mContext, "最少4个点", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (!passWd.equals("012345")) {
                    Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    Toast.makeText(mContext, "密码正确", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });
    }
}
