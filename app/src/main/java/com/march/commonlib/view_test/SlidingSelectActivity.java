package com.march.commonlib.view_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.march.commonlib.R;
import com.march.commonlib.adapter.SectionRuleActivity;
import com.march.lib.core.activity.BaseActivity;

public class SlidingSelectActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sliding_select;
    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        startActivity(new Intent(mContext, SectionRuleActivity.class));
        finish();
    }
}
