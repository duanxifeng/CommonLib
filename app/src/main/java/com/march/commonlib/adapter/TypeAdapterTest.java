package com.march.commonlib.adapter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.march.commonlib.R;
import com.march.lib.adapter.common.ITypeAdapterModel;
import com.march.lib.adapter.core.BaseViewHolder;
import com.march.lib.adapter.core.TypeRvAdapter;
import com.march.lib.core.activity.BaseActivity;
import com.march.lib.core.widget.TitleBarView;

import java.util.ArrayList;
import java.util.List;

public class TypeAdapterTest extends BaseActivity {

    private List<TypeModel> typeModels;

    @Override
    protected int getLayoutId() {
        return R.layout.type_adapter_activity;
    }

    @Override
    public void onInitDatas() {
        super.onInitDatas();
        typeModels = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            typeModels.add(new TypeModel(i));
        }
    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        mTitleBarView.setText(TitleBarView.POS_Center,"多类型适配");
        RecyclerView mRv = getView(R.id.recyclerview);
        mRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        TypeRvAdapter<TypeModel> adapter = new TypeRvAdapter<TypeModel>(mContext, typeModels) {
            @Override
            public void onBindView(BaseViewHolder holder, TypeModel data, int pos, int type) {
                holder.setText(R.id.item_common_tv, "id相同可以不区分类型" + data.index);
                switch (type) {
                    case TypeModel.TYPE_OK:
                        holder.setText(R.id.item_ok_tv, "this is ok !");
                        break;
                    case TypeModel.TYPE_NO:
                        holder.setText(R.id.item_no_tv, "this is not ok !");
                        break;
                }
            }
        };
        adapter.addType(TypeModel.TYPE_OK, R.layout.type_adapter_ok)
                .addType(TypeModel.TYPE_NO, R.layout.type_adapter_no);
        mRv.setAdapter(adapter);
    }

    @Override
    protected String[] getPermission2Check() {
        return new String[0];
    }

    @Override
    protected boolean isInitTitle() {
        return true;
    }


    class TypeModel implements ITypeAdapterModel {

        public static final int TYPE_OK = 1;
        public static final int TYPE_NO = 2;
        int index;

        public TypeModel(int index) {
            this.index = index;
        }

        //当index是3的倍数时，为ok
        @Override
        public int getRvType() {
            return index % 3 == 0 ? TYPE_OK : TYPE_NO;
        }

    }
}
