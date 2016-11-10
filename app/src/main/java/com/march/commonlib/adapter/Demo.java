package com.march.commonlib.adapter;


import com.march.lib.adapter.common.ITypeAdapterModel;

/**
 * QuickRv     com.march.quickrv
 * Created by 陈栋 on 16/3/15.
 * 功能:
 */
public class Demo implements ITypeAdapterModel {

    private boolean isSingleType = false;
    @Override
    public int getRvType() {
        if(isSingleType)
            return 0;
        return index % 3 == 0 ? 0 : 1;
    }

    String title;
    int index;

    public Demo(int index, String title) {
        this.index = index;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Demo{" +
                "isSingleType=" + isSingleType +
                ", itemHeaderTitle='" + title + '\'' +
                ", contentIndex=" + index +
                '}';
    }
}
