package com.march.commonlib.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.march.commonlib.R;
import com.march.lib.adapter.common.AbsSectionHeader;
import com.march.lib.adapter.common.OnLoadMoreListener;
import com.march.lib.adapter.common.SimpleItemListener;
import com.march.lib.adapter.core.AbsAdapter;
import com.march.lib.adapter.core.BaseViewHolder;
import com.march.lib.adapter.core.SectionRvAdapter;
import com.march.lib.adapter.model.ItemModel;
import com.march.lib.adapter.module.LoadMoreModule;
import com.march.lib.core.activity.BaseActivity;
import com.march.lib.core.widget.TitleBarView;
import com.march.lib.view.SlidingSelectLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SectionAdapterTest extends BaseActivity {

    private int num;
    private SlidingSelectLayout ssl;
    private List<Content> list;
    private SectionRvAdapter<ItemHeader, Content> adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.item_header_activity;
    }

    @Override
    public void onInitDatas() {
        super.onInitDatas();
        list = new ArrayList<>();

    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        RecyclerView mRv = getView(R.id.recyclerview);
        ssl = getView(R.id.ssl);
        mTitleBarView.setText(TitleBarView.POS_Center,"九宫格显示适配");
        mRv.setLayoutManager(new GridLayoutManager(this, 3));
        final List<Content> contents = new ArrayList<>();

        final LinkedHashMap<ItemHeader, List<Content>> map = new LinkedHashMap<>();
        contents.clear();
        for (int i = 0; i < 7; i++) {
            contents.add(new Content("" + i));
        }
        map.put(new ItemHeader("title_1"), contents);

        adapter = new SectionRvAdapter<ItemHeader, Content>(this, map,
                R.layout.item_header_header,
                R.layout.item_header_content) {
            @Override
            protected void onBindItemHeader(BaseViewHolder holder, ItemHeader data, int pos, int type) {
                holder.setText(R.id.info1, data.getItemHeaderTitle());
            }

            @Override
            protected void onBindContent(BaseViewHolder holder, Content data, int pos, int type) {
                ViewGroup.LayoutParams layoutParams = holder.getParentView().getLayoutParams();
                layoutParams.height = (int) (getResources().getDisplayMetrics().widthPixels / 3.0f);
                ssl.markView(holder.getParentView(), pos,data);
                TextView tv = (TextView) holder.getView(R.id.tv);
                if (list.contains(data)) {
                    tv.setText("check" + data.contentTitle);
                    tv.setTextColor(Color.RED);
                } else {
                    tv.setText("un check" + data.contentTitle);
                    tv.setTextColor(Color.GREEN);
                }
            }
        };
        num = 11;
        adapter.addLoadMoreModule(new LoadMoreModule(2, new OnLoadMoreListener() {
            @Override
            public void onLoadMore(LoadMoreModule mLoadMoreModule) {
                Log.e("chendong", "触发");
                map.put(new ItemHeader("new title_" + num++), contents);
                map.put(new ItemHeader("new title_" + num++), contents);
                map.put(new ItemHeader("new title_" + num++), contents);
                adapter.updateDataAndItemHeader(map);
                mLoadMoreModule.finishLoad();
            }
        }));

        adapter.setItemListener(new SimpleItemListener<ItemModel>() {
            @Override
            public void onClick(int pos, BaseViewHolder holder, ItemModel data) {
                if (data.getRvType() == AbsAdapter.TYPE_ITEM_DEFAULT) {
                    Content content = (Content) data.get();
                    Toast.makeText(SectionAdapterTest.this, content.contentTitle, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mRv.setAdapter(adapter);

        ssl.setOnSlidingSelectListener(new SlidingSelectLayout.OnSlidingSelectListener<Content>() {
            @Override
            public void onSlidingSelect(int pos, View parentView, Content data) {
                Log.e("chendong",data.contentTitle);
                if (list.contains(data)) {
                    list.remove(data);
                } else {
                    list.add(data);
                }
                adapter.notifyItemChanged(pos);
            }
        });
    }



    @Override
    protected String[] getPermission2Check() {
        return new String[0];
    }

    @Override
    protected boolean isInitTitle() {
        return true;
    }


    class ItemHeader extends AbsSectionHeader {
        String itemHeaderTitle;

        public String getItemHeaderTitle() {
            return itemHeaderTitle;
        }

        public void setItemHeaderTitle(String itemHeaderTitle) {
            this.itemHeaderTitle = itemHeaderTitle;
        }

        public ItemHeader(String title) {
            this.itemHeaderTitle = title;
        }
    }

    static class Content {
        String contentTitle;

        public Content(String title) {
            this.contentTitle = title;
        }

    }
}
