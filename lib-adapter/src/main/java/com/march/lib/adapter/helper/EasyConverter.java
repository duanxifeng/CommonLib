package com.march.lib.adapter.helper;

import com.march.lib.adapter.model.ItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * QuickAdapter     com.march.adapterlibs.helper
 * Created by 陈栋 on 16/4/8.
 * 功能:
 */
public class EasyConverter {

    /**
     * 将Integer,String等类型的list转换为可识别类型
     *
     * @param list 需要转换的list
     * @param <T>  范型
     * @return 列表
     */
    public static <T> List<ItemModel> convert(List<T> list) {
        List<ItemModel> itemModels = new ArrayList<>();
        for (T t : list) {
            itemModels.add(new ItemModel<>(t));
        }
        return itemModels;
    }

    /**
     * @param list 需要转换的list
     * @param type 数据自定义type
     * @param <T>  范型
     * @return 列表
     */
    public static <T> List<ItemModel> convert(List<T> list, int type) {
        List<ItemModel> itemModels = new ArrayList<>();
        for (T t : list) {
            itemModels.add(new ItemModel<>(t, type));
        }
        return itemModels;
    }


    /**
     * 将数组转换为可识别类型的list
     *
     * @param list 需要转换的list
     * @param <T>  范型
     * @return 列表
     */
    public static <T> List<ItemModel> convert(T[] list) {
        List<ItemModel> itemModels = new ArrayList<>();
        for (T t : list) {
            itemModels.add(new ItemModel<>(t));
        }
        return itemModels;
    }
}
