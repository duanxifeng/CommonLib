package com.march.commonlib.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.march.commonlib.R;
import com.march.lib.adapter.common.AbsSectionHeader;
import com.march.lib.adapter.common.ISectionRule;
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
import java.util.List;

public class SectionRuleActivity extends BaseActivity {

    private List<Content> contents;
    private SlidingSelectLayout ssl;
    private SectionRvAdapter<ItemHeader, Content> adapter;
    private int limit = 30;
    private int offset = 0;
    private List<Content> selects;

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        RecyclerView mRv = getView(R.id.recyclerview);
        ssl = getView(R.id.ssl);
        mTitleBarView.setText(TitleBarView.POS_Center,"九宫格使用Rule适配/滑动选中");
        mRv.setLayoutManager(new GridLayoutManager(this, 3));
//        mRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
//        mRv.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        selects = new ArrayList<>();
        contents = new ArrayList<>();
        for (int i = offset; i < offset + limit; i++) {
            contents.add(new Content("this is new " + i, i));
        }
        offset += limit;
        adapter = new SectionRvAdapter<ItemHeader, Content>(
                this,
                contents,
                R.layout.item_header_header,
                R.layout.item_header_content) {
            @Override
            protected void onBindItemHeader(BaseViewHolder holder, ItemHeader data, int pos, int type) {
                holder.setText(R.id.info1, data.getItemHeaderTitle());
            }

            @Override
            protected void onBindContent(BaseViewHolder holder, Content data, int pos, int type) {
                ssl.markView(holder.getParentView(), pos, data);
                ViewGroup.LayoutParams layoutParams = holder.getParentView().getLayoutParams();
                layoutParams.height = (int) (getResources().getDisplayMetrics().widthPixels / 3.0f);
                holder.setText(R.id.tv, String.valueOf(data.contentIndex));

                if (selects.contains(data)) {
                    holder.getParentView().setBackgroundColor(Color.RED);
                } else {
                    holder.getParentView().setBackgroundColor(Color.GREEN);

                }
            }
        };

        adapter.addItemHeaderRule(new ISectionRule<ItemHeader, Content>() {
            @Override
            public ItemHeader buildItemHeader(int currentPos, Content preData, Content currentData, Content nextData) {
                return new ItemHeader("pre is " + getIndex(preData) + " current is " + getIndex(currentData) + " next is " + getIndex(nextData));
            }

            @Override
            public boolean isNeedItemHeader(int currentPos, Content preData, Content currentData, Content nextData) {
                Log.e("chendong", currentPos + "  " + getString(preData) + "  " + getString(currentData) + "  " + getString(nextData));
                return currentPos == 0 || currentData.contentIndex % 7 == 1;
            }
        });

        adapter.addLoadMoreModule(new LoadMoreModule(2, new OnLoadMoreListener() {
            @Override
            public void onLoadMore(LoadMoreModule mLoadMoreModule) {
                List<Content> tempList = new ArrayList<Content>();
                for (int i = offset; i < offset + limit; i++) {
                    tempList.add(new Content("this is new" + i, i));
                }
                offset += limit;
                adapter.appendSectionTailRangeData(tempList);
                mLoadMoreModule.finishLoad();
            }
        }));

        adapter.setItemListener(new SimpleItemListener<ItemModel>() {
            @Override
            public void onClick(int pos, BaseViewHolder holder, ItemModel data) {
                if (data.getRvType() == AbsAdapter.TYPE_ITEM_DEFAULT) {
                    Content content = (Content) data.get();
                    Toast.makeText(SectionRuleActivity.this, content.contentTitle, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mRv.setAdapter(adapter);

        ssl.setOnSlidingSelectListener(new SlidingSelectLayout.OnSlidingSelectListener<Content>() {
            @Override
            public void onSlidingSelect(int pos, View parentView, Content data) {
                if (selects.contains(data)) {
                    selects.remove(data);
                } else {
                    selects.add(data);
                }
                adapter.notifyItemChanged(pos);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_header_activity;
    }


    private String getString(Content content) {
        if (content == null)
            return "null";
        return content.toString();
    }

    private String getIndex(Content content) {
        if (content == null)
            return "null";
        return content.contentIndex + "";
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
        public static final int TYPE_CONTENT = 1;
        String contentTitle;
        int contentIndex;

        @Override
        public String toString() {
            return "Content{" +
                    "itemHeaderTitle='" + contentTitle + '\'' +
                    ", contentIndex=" + contentIndex +
                    '}';
        }

        public Content(String title, int index) {
            this.contentTitle = title;
            this.contentIndex = index;
        }
    }
}
