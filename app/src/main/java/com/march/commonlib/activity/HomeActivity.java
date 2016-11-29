package com.march.commonlib.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.march.commonlib.R;
import com.march.commonlib.adapter.AdapterMainActivity;
import com.march.commonlib.model.TestDemo;
import com.march.commonlib.selectimg.SelectImageActivity;
import com.march.commonlib.view_test.LeProgressViewTestActivity;
import com.march.commonlib.view_test.LockViewActivity;
import com.march.commonlib.view_test.SlidingSelectActivity;
import com.march.lib.adapter.common.SimpleItemListener;
import com.march.lib.adapter.core.BaseViewHolder;
import com.march.lib.adapter.core.SimpleRvAdapter;
import com.march.lib.core.activity.BaseActivity;
import com.march.lib.core.widget.TitleBarView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {


    private RecyclerView rv;

    private List<TestDemo> datas;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void onInitDatas() {
        super.onInitDatas();
        datas = new ArrayList<>();
        datas.add(new TestDemo("Adapter-快速适配器", AdapterMainActivity.class));
        datas.add(new TestDemo("Demo-仿QQ微信相册", SelectImageActivity.class));
        datas.add(new TestDemo("Demo-系统相册选择照片裁剪", SysSelectImageAndCropActivity.class));
        datas.add(new TestDemo("View-乐视进度条", LeProgressViewTestActivity.class));
        datas.add(new TestDemo("View-九宫格解锁", LockViewActivity.class));
        datas.add(new TestDemo("View-滑动选中", SlidingSelectActivity.class));
    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        mTitleBarView.setText(TitleBarView.POS_Center, "Demo列表");
        rv = getView(R.id.rv);

        SimpleRvAdapter<TestDemo> adapter = new SimpleRvAdapter<TestDemo>(mContext, datas, R.layout.item_home) {
            @Override
            public void onBindView(BaseViewHolder holder, TestDemo data, int pos, int type) {
                holder.setText(R.id.item_tv, data.desc);
            }
        };

        adapter.setItemListener(new SimpleItemListener<TestDemo>() {
            @Override
            public void onClick(int pos, BaseViewHolder holder, TestDemo data) {
                super.onClick(pos, holder, data);
                if (data.cls.equals(SelectImageActivity.class)) {
                    SelectImageActivity.selectImages(mActivity, 60);
                } else {
                    startActivity(new Intent(mContext, data.cls));
                }
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(adapter);
    }

    @Override
    protected String[] getPermission2Check() {
        return new String[0];
    }

    @Override
    protected boolean isInitTitle() {
        return true;
    }
}
