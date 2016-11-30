package com.march.lib.adapter.core;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.march.lib.adapter.common.AbsSectionHeader;
import com.march.lib.adapter.common.ISectionRule;
import com.march.lib.adapter.helper.CommonHelper;
import com.march.lib.adapter.helper.EasyConverter;
import com.march.lib.adapter.model.ItemModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter
 * CreateAt : 16/8/31
 * Describe : header + section的adapter,可以实现九宫格的效果
 *
 * @author chendong
 */
public abstract class SectionRvAdapter<IH extends AbsSectionHeader, ID> extends TypeRvAdapter<ItemModel> {

    private ISectionRule<IH, ID> sectionRule;
    private List<ID> mOriginDatas;
    private int headerLayoutId;
    private int contentLayoutId;


    private void clearDataIfNotNull() {
        if (this.dateSets == null)
            this.dateSets = new ArrayList<>();
        else
            this.dateSets.clear();
    }


    //使用自动匹配插入header，同时制定header 和 content的layout
    public SectionRvAdapter(Context context,
                            List<ID> originDatas,
                            int headerLayoutId, int contentLayoutId) {
        super(context);
        this.mOriginDatas = originDatas;
        this.headerLayoutId = headerLayoutId;
        this.contentLayoutId = contentLayoutId;
        addTypeInternal(AbsAdapter.TYPE_ITEM_DEFAULT, contentLayoutId);
        addTypeInternal(AbsAdapter.TYPE_ITEM_HEADER, headerLayoutId);
    }

    //使用自动匹配插入header,header布局固定，可以添加多种type 的content
    public SectionRvAdapter(Context context, List<ID> originDatas,
                            int headerLayoutId) {
        super(context);
        this.mOriginDatas = originDatas;
        this.headerLayoutId = headerLayoutId;
        addTypeInternal(AbsAdapter.TYPE_ITEM_HEADER, headerLayoutId);
    }

    //使用Map的方式构造，数据在外面已经构建好，制定header和content的资源
    public SectionRvAdapter(Context context,
                            LinkedHashMap<IH, List<ID>> originDatas,
                            int headerLayoutId, int contentLayoutId) {
        super(context);
        this.headerLayoutId = headerLayoutId;
        this.contentLayoutId = contentLayoutId;
        clearDataIfNotNull();
        this.dateSets.addAll(secondaryPackData(originDatas));
        addTypeInternal(AbsAdapter.TYPE_ITEM_DEFAULT, contentLayoutId);
        addTypeInternal(AbsAdapter.TYPE_ITEM_HEADER, headerLayoutId);
    }

    //使用Map的方式构造，数据在外面已经构建好，制定header,可以多种content
    public SectionRvAdapter(Context context,
                            LinkedHashMap<IH, List<ID>> originDatas,
                            int headerLayoutId) {
        super(context);
        this.headerLayoutId = headerLayoutId;
        clearDataIfNotNull();
        this.dateSets.addAll(secondaryPackData(originDatas));
        addTypeInternal(AbsAdapter.TYPE_ITEM_HEADER, headerLayoutId);
    }

    /**
     * 添加header生成规则
     *
     * @param sectionRule 规则
     */
    public void addItemHeaderRule(ISectionRule<IH, ID> sectionRule) {
        this.sectionRule = sectionRule;
        clearDataIfNotNull();
        this.dateSets.addAll(secondaryPackData(mOriginDatas, false));
    }

    /**
     * 使用map添加数据时，打包数据
     *
     * @param data 数据
     * @return 用来适配的数据
     */
    private List<ItemModel> secondaryPackData(Map<IH, List<ID>> data) {
        List<ItemModel> tempList = new ArrayList<>();
        for (IH ih : data.keySet()) {
            tempList.add(new ItemModel<>(ih));
            tempList.addAll(EasyConverter.convert(data.get(ih), TYPE_ITEM_DEFAULT));
        }
        return tempList;
    }

    /**
     * 使用规则生成header时打包数据
     *
     * @param data     数据
     * @param isAppend 是不是连接数据
     * @return 用来适配数据
     */
    private List<ItemModel> secondaryPackData(List<ID> data, boolean isAppend) {
        if (sectionRule == null)
            throw new IllegalStateException("please insert item header rule first");

        List<ItemModel> tempList = new ArrayList<>();
        ID preData;
        ID nextData;
        ID tempData;
        IH itemHeader;
        for (int i = 0; i < data.size(); i++) {
            if (isAppend && i == 0)
                continue;
            tempData = data.get(i);
            preData = i == 0 ? null : data.get(i - 1);
            nextData = i == data.size() - 1 ? null : data.get(i + 1);
            //如果需要在当前数据之前插入header,就在插入当前数据之前构建一个header
            int middle = isAppend ? mOriginDatas.size() + i : i;
            if (sectionRule.isNeedItemHeader(middle, preData, tempData, nextData)) {
                itemHeader = sectionRule.buildItemHeader(middle, preData, tempData, nextData);
                if (itemHeader != null)
                    tempList.add(new ItemModel<>(itemHeader));
            }
            tempList.add(new ItemModel<>(tempData, AbsAdapter.TYPE_ITEM_DEFAULT));
        }
        return tempList;
    }


    @Override
    public void notifyDataSetChanged(List<ItemModel> data, boolean isUpdate) {
        throw new IllegalArgumentException("please use updateDataAndItemHeader() to update show");
    }

    /**
     * 更新数据，使用规则生成header时
     *
     * @param data 新数据
     */
    public void updateDataAndItemHeader(List<ID> data) {
        mOriginDatas.clear();
        mOriginDatas.addAll(data);
        this.dateSets.clear();
        this.dateSets.addAll(secondaryPackData(data, false));
        notifyDataSetChanged();
    }

    /**
     * 更新数据，使用map初始化使用
     *
     * @param map 数据
     */
    public void updateDataAndItemHeader(Map<IH, List<ID>> map) {
        this.dateSets.clear();
        this.dateSets.addAll(secondaryPackData(map));
        notifyDataSetChanged();
    }


    /**
     * 追加在后面的数据
     *
     * @param data 数据
     */
    public void appendSectionTailRangeData(List<ID> data) {
        if (data == null || data.size() <= 0)
            return;

        int startPos = this.dateSets.size() + 1;

        List<ItemModel> tempList = new ArrayList<>();
        IH itemHeader;
        ID preOne = mOriginDatas.get(mOriginDatas.size() - 1);
        ID currentOne = data.get(0);
        ID nextOne = null;
        if (data.size() > 1)
            nextOne = data.get(1);
        int middle = mOriginDatas.size();
        // 拼接时如果需要加header,就先加一个header再打包数据
        if (sectionRule.isNeedItemHeader(middle, preOne, currentOne, nextOne)) {
            itemHeader = sectionRule.buildItemHeader(middle, preOne, currentOne, nextOne);
            if (itemHeader != null)
                tempList.add(new ItemModel<>(itemHeader));
        }
        tempList.add(new ItemModel<>(currentOne, AbsAdapter.TYPE_ITEM_DEFAULT));
        tempList.addAll(secondaryPackData(data, true));
        mOriginDatas.addAll(data);
        this.dateSets.addAll(tempList);
        notifyItemRangeInserted(startPos, this.dateSets.size() - startPos - 1);
    }

    public List<ID> getOriginDatas() {
        return mOriginDatas;
    }

    @Override
    public void onBindView(BaseViewHolder holder, ItemModel data, int pos, int type) {
        if (type == AbsAdapter.TYPE_ITEM_HEADER) {
            onBindItemHeader(holder, (IH) data.get(), pos, type);
        } else {
            onBindContent(holder, (ID) (data.get()), pos, type);
        }
    }

    protected abstract void onBindItemHeader(BaseViewHolder holder, IH data, int pos, int type);

    protected abstract void onBindContent(BaseViewHolder holder, ID data, int pos, int type);

    @Override
    public boolean isFullSpan4GridLayout(int viewType) {
        return viewType == AbsAdapter.TYPE_ITEM_HEADER;
    }
}
