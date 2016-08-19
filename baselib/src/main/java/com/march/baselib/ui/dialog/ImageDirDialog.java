package com.march.baselib.ui.dialog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.ImageView;

import com.march.baselib.DevelopLib;
import com.march.baselib.R;
import com.march.baselib.helper.DimensionHelper;
import com.march.baselib.inter.OnCommonListener;
import com.march.baselib.model.ImageDirInfo;
import com.march.baselib.model.ImageInfo;
import com.march.quickrvlibs.RvViewHolder;
import com.march.quickrvlibs.SimpleRvAdapter;
import com.march.quickrvlibs.inter.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Project  : CommonLib </br>
 * Package  : com.march.baselib </br>
 * CreateAt : 16/8/15 </br> </br>
 * Describe : 图像目录dialog </br>
 *
 * @author chendong </br>
 */
public class ImageDirDialog extends BaseDialog {

    private OnCommonListener<ImageDirInfo> listener;
    private RecyclerView mDirRv;
    private List<ImageDirInfo> mImageDirs;
    private SimpleRvAdapter<ImageDirInfo> mDirAdapter;

    public ImageDirDialog(Context context, Map<String, List<ImageInfo>> mImageMap) {
        super(context, R.style.no_dim_dialog);
        mImageDirs = new ArrayList<>();
        ImageDirInfo dir;
        for (String dirName : mImageMap.keySet()) {
            List<ImageInfo> imageInfos = mImageMap.get(dirName);
            dir = new ImageDirInfo(imageInfos.size(), dirName, imageInfos.get(0));
            mImageDirs.add(dir);
        }
        Collections.sort(mImageDirs);
        createAdapter();
    }

    public void setListener(OnCommonListener<ImageDirInfo> listener) {
        this.listener = listener;
    }

    private void createAdapter() {
        mDirAdapter = new SimpleRvAdapter<ImageDirInfo>(getContext(), mImageDirs, R.layout.select_image_dir_item) {
            @Override
            public void bindData4View(RvViewHolder holder, ImageDirInfo data, int pos) {
                ImageView iv = holder.getView(R.id.iv_dir_cover);
                int size = DimensionHelper.dp2px(80);
                DevelopLib.getLoadImg().loadImg(iv, size, size, data.getCoverInfo().getPath());
                holder.setText(R.id.tv_dir_name, data.getDirName() + "(" + data.getPicNum() + ")");
            }
        };
        mDirAdapter.setOnItemClickListener(new OnItemClickListener<RvViewHolder>() {
            @Override
            public void onItemClick(int pos, RvViewHolder holder) {
                if (listener != null) {
                    listener.onEvent(pos, mImageDirs.get(pos));
                }
                dismiss();
            }
        });
        mDirRv.setAdapter(mDirAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.select_image_dir_dialog;
    }

    @Override
    protected void initViews() {
        mDirRv = getView(R.id.rv_image_dir);
    }

    @Override
    protected void setWindowParams() {
        int mDialogHeight = (int) (DimensionHelper.getScreenHeight() * 0.725f);
        setWindowParams(MATCH, mDialogHeight, 1.0f, 0.8f, Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.dialog_anim_bottom_to_center);
    }

    @Override
    public void show() {
        mDirRv.scrollToPosition(0);
        super.show();
    }
}
