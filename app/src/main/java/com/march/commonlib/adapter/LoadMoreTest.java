package com.march.commonlib.adapter;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.march.commonlib.R;
import com.march.lib.adapter.common.OnLoadMoreListener;
import com.march.lib.adapter.core.BaseViewHolder;
import com.march.lib.adapter.core.SimpleRvAdapter;
import com.march.lib.adapter.module.LoadMoreModule;
import com.march.lib.core.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class LoadMoreTest extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.load_more_activity;
    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        getSupportActionBar().setTitle("加载更多模块");
        RecyclerView mRv = getView(R.id.recyclerview);
//        mRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRv.setLayoutManager(new GridLayoutManager(mContext, 2));

        final List<LoadMoreModel> datas = new ArrayList<>();
        for (int i = 0; i < 19; i++) {
            datas.add(new LoadMoreModel("this is " + i));
        }

        final SimpleRvAdapter<LoadMoreModel> adapter = new SimpleRvAdapter<LoadMoreModel>(mContext, datas, R.layout.load_more_item) {
            @Override
            public void onBindView(BaseViewHolder holder, LoadMoreModel data, int pos, int type) {
                holder.setText(R.id.info, data.desc);
            }
        };

        LoadMoreModule loadMoreM = new LoadMoreModule(4, new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final LoadMoreModule mLoadMoreModule) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<LoadMoreModel> tempData = new ArrayList<LoadMoreModel>();
                        for (int i = 0; i < 9; i++) {
                            tempData.add(new LoadMoreModel("new is " + i));
                        }
                        adapter.appendTailRangeData(tempData, false);
                        mLoadMoreModule.finishLoad();
                    }
                }, 1500);
            }
        });
        adapter.addLoadMoreModule(loadMoreM);
        mRv.setAdapter(adapter);
    }

    @Override
    protected String[] getPermission2Check() {
        return new String[0];
    }

    @Override
    protected boolean isInitTitle() {
        return false;
    }

    class LoadMoreModel {
        String desc;

        public LoadMoreModel(String desc) {
            this.desc = desc;
        }
    }
}
