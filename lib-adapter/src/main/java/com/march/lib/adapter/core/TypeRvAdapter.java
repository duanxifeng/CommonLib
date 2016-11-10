package com.march.lib.adapter.core;

import android.content.Context;
import android.util.SparseArray;

import com.march.lib.adapter.common.IAdapterModel;
import com.march.lib.adapter.helper.CommonHelper;

import java.util.List;

/**
 * Created by march on 16/6/8.
 * RecyclerView多类型快速适配
 */
public abstract class TypeRvAdapter<D extends IAdapterModel>
        extends BaseRvAdapter<D> {

    private class TypeConfig {
        int type;
        int resId;

        TypeConfig(int type, int resId) {
            this.type = type;
            this.resId = resId;
        }
    }

    protected SparseArray<TypeConfig> Res4Type;

    public TypeRvAdapter(Context context) {
        super(context);
    }

    /**
     * 多类型适配,需要调用addType()方法配置参数
     *
     * @param context context
     * @param data    数据源
     */
    public TypeRvAdapter(Context context, List<D> data) {
        super(context, data);
    }

    public TypeRvAdapter(Context context, D[] data) {
        super(context, data);
    }

    /**
     * @param type  数据的类型(如果有n种类型,那么type的值需要是0 ~ n-1)
     * @param resId 该类型对应的资源文件的id
     * @return QuickTypeAdapter
     */
    public TypeRvAdapter<D> addType(int type, int resId) {
        CommonHelper.checkTypeValid(type);

        if (this.Res4Type == null)
            this.Res4Type = new SparseArray<>();
        this.Res4Type.put(type, new TypeConfig(type, resId));
        return this;
    }

    protected TypeRvAdapter<D> addTypeInternal(int type, int resId){
        if (this.Res4Type == null)
            this.Res4Type = new SparseArray<>();
        this.Res4Type.put(type, new TypeConfig(type, resId));
        return this;
    }

    @Override
    protected int getOriginItemType(int pos) {
        return this.dateSets.get(pos).getRvType();
    }

    @Override
    protected int getLayout4Type(int viewType) {
        return Res4Type.get(viewType).resId;
    }
}
