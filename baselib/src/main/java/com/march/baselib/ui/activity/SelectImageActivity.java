package com.march.baselib.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.march.baselib.develop.DevelopLib;
import com.march.baselib.R;
import com.march.baselib.helper.DimensionHelper;
import com.march.baselib.helper.ImageHelper;
import com.march.baselib.helper.Toaster;
import com.march.baselib.inter.OnCommonListener;
import com.march.baselib.model.ImageDirInfo;
import com.march.baselib.model.ImageInfo;
import com.march.baselib.module.TitleModule;
import com.march.baselib.ui.dialog.ImageDirDialog;
import com.march.baselib.widget.SlidingCheckLayout;
import com.march.quickrvlibs.RvViewHolder;
import com.march.quickrvlibs.SimpleRvAdapter;
import com.march.quickrvlibs.inter.OnItemClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : 选择照片的activity
 *
 * @author chendong
 */
public class SelectImageActivity extends BaseActivity implements View.OnClickListener {

    private static int REQ_CODE = 0x12;
    private static String KEY_LIMIT = "KEY_LIMIT";
    /**
     * 不限制选择数量
     */
    public static int NO_LIMIT = -1;

    private int size;

    private RecyclerView mImageRv;
    private SlidingCheckLayout mSlidingCheckLy;
    private SimpleRvAdapter<ImageInfo> mImageAdapter;
    //目录 － 目录下的图片列表
    private Map<String, List<ImageInfo>> mImagesMap;
    //当前的图片列表
    private List<ImageInfo> mCurrentImages;
    //被选中的图片列表
    private List<ImageInfo> mSelectImages;
    private TextView mDateTv;
    private SimpleDateFormat simpleDateFormat;
    private GridLayoutManager mLayoutManager;
    private ObjectAnimator mAlphaAnimator;
    private ImageDirDialog mDirDialog;
    private int mSelectImageMaxNum = 0;

    /**
     * 启动界面选择图片
     *
     * @param from  从哪个activity进入
     * @param limit 最多选择多少张，不限制时 NO_LIMIT
     */
    public static void selectImages(Activity from, int limit) {
        Intent intent = new Intent(from, SelectImageActivity.class);
        intent.putExtra(KEY_LIMIT, limit);
        from.startActivityForResult(intent, REQ_CODE);
    }

    @Override
    protected void onInitIntent(Intent intent) {
        super.onInitIntent(intent);
        mSelectImageMaxNum = intent.getIntExtra(KEY_LIMIT, NO_LIMIT);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected String[] getPermission2Check() {
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    @Override
    protected boolean handlePermissionResult(Map<String, Integer> resultNoOk) {
        if (resultNoOk.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toaster.get().show("您没有允许读取存储卡，不能继续操作");
            return false;
        }
        return true;
    }

    @Override
    protected void onInitDatas() {
        super.onInitDatas();
        simpleDateFormat = new SimpleDateFormat("yyyy年M月d日 HH:mm:ss", Locale.CHINA);
        mSelectImages = new ArrayList<>();
        Task.callInBackground(new Callable<Map<String, List<ImageInfo>>>() {
            @Override
            public Map<String, List<ImageInfo>> call() throws Exception {
                size = (int) (DimensionHelper.getScreenWidth() / 3.0f);
                return ImageHelper.formatImages4EachDir(ImageHelper.getImagesByMediaStore(mActivity));
            }
        }).onSuccess(new Continuation<Map<String, List<ImageInfo>>, Boolean>() {
            @Override
            public Boolean then(Task<Map<String, List<ImageInfo>>> task) throws Exception {
                mImagesMap = task.getResult();
                mCurrentImages = mImagesMap.get(ImageHelper.ALL_IMAGE_KEY);
                createOrUpdateAdapter();
                return true;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }


    private void createOrUpdateAdapter() {
        if (mImageAdapter != null) {
            mImageAdapter.updateData(mCurrentImages);
            return;
        }
        mImageAdapter = new SimpleRvAdapter<ImageInfo>(mContext, mCurrentImages, R.layout.select_image_item_rv) {
            @Override
            public void onBindView(final RvViewHolder holder, ImageInfo data, final int pos, int type) {
                View coverView = holder.getView(R.id.cover_select_image);
                TextView tv = (TextView) holder.getView(R.id.tv_select_image);
                if (mSelectImages.contains(data)) {
                    coverView.setVisibility(View.VISIBLE);
                    tv.setSelected(true);
                    tv.setText(String.valueOf(mSelectImages.indexOf(data) + 1));
                } else {
                    coverView.setVisibility(View.GONE);
                    tv.setSelected(false);
                    tv.setText("");
                }

                ImageView iv = (ImageView) holder.getView(R.id.iv_select_image);
                DevelopLib.getLoadImg().loadImg(iv, size, size, data.getPath());

                View parentView = holder.getParentView();
                parentView.getLayoutParams().width = size;
                parentView.getLayoutParams().height = size;

                View.OnClickListener lis = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyCheckOrNotCheck(holder, pos);
                    }
                };
                holder.setClickLis(R.id.view_click_cover, lis);
                holder.setClickLis(R.id.tv_select_image, lis);
            }

        };
        mImageAdapter.setOnItemClickListener(new OnItemClickListener<ImageInfo>() {
            @Override
            public void onItemClick(int pos, RvViewHolder holder, ImageInfo data) {
                Toaster.get().show("打开预览");
            }
        });
        mImageRv.setAdapter(mImageAdapter);
    }

    @Override
    protected void onInitViews(Bundle save) {
        super.onInitViews(save);
        mSlidingCheckLy = getView(R.id.sliding);
        mImageRv = getView(R.id.rv_select_image);
        mLayoutManager = (GridLayoutManager) mImageRv.getLayoutManager();
        mDateTv = getView(R.id.tv_time_image);
        mTitleModule.setText("返回", ImageHelper.ALL_IMAGE_KEY, "确定");
        mAlphaAnimator = ObjectAnimator
                .ofFloat(mDateTv, "alpha", 1.0f, 0f)
                .setDuration(1500);
    }

    @Override
    protected void onInitEvents() {
        super.onInitEvents();
        getView(R.id.tv_select_image_dir).setOnClickListener(this);

        mImageRv.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItemPosition < 0)
                    return;
                mDateTv.setText(simpleDateFormat.format(new Date(Long.parseLong(mCurrentImages.get(firstVisibleItemPosition).getDate()) * 1000)));
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });

        mSlidingCheckLy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!mAlphaAnimator.isRunning())
                        mAlphaAnimator.start();
                } else {
                    if (mAlphaAnimator.isRunning())
                        mAlphaAnimator.cancel();
                    mDateTv.setAlpha(1);
                }
            }
        });

        mSlidingCheckLy.setOnSlidingCheckListener(new SlidingCheckLayout.OnSlidingCheckListener() {
            @Override
            public void onSlidingCheck(int pos) {
                RvViewHolder holder = (RvViewHolder) mImageRv.findViewHolderForAdapterPosition(pos);
                notifyCheckOrNotCheck(holder, pos);
            }
        });
    }

    /**
     * 修改选中和不选中的状态
     *
     * @param holder
     * @param pos
     */
    private void notifyCheckOrNotCheck(RvViewHolder holder, int pos) {
        ImageInfo imageInfo = mCurrentImages.get(pos);
        TextView tv = (TextView) holder.getView(R.id.tv_select_image);
        View coverView = holder.getView(R.id.cover_select_image);
        if (tv.isSelected()) {
            mSelectImages.remove(imageInfo);
            tv.setSelected(false);
            coverView.setVisibility(View.GONE);
            tv.setText("");
        } else {
            if (mSelectImageMaxNum != NO_LIMIT && mSelectImages.size() >= mSelectImageMaxNum) {
                Toaster.get().show("最多只能选择" + mSelectImageMaxNum + "张");
                return;
            }
            mSelectImages.add(imageInfo);
            tv.setSelected(true);
            coverView.setVisibility(View.VISIBLE);
            tv.setText(String.valueOf(mSelectImages.indexOf(imageInfo) + 1));
        }
    }

    @Override
    protected boolean isInitTitle() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.select_image_activity;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImagesMap.clear();
        mCurrentImages.clear();
        mSelectImages.clear();
        mImagesMap = null;
        mCurrentImages = null;
        mSelectImages = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_select_image_dir) {
            showDirDialog();
        }
    }

    private void showDirDialog() {
        if (checkDialog2Show(mDirDialog))
            return;
        mDirDialog = new ImageDirDialog(mActivity, mImagesMap);
        mDirDialog.setListener(new OnCommonListener<ImageDirInfo>() {
            @Override
            public void onEvent(int pos, ImageDirInfo data) {
                mCurrentImages = mImagesMap.get(data.getDirName());
                mTitleModule.setText(TitleModule.POS_Center, data.getDirName());
                mImageAdapter.updateData(mCurrentImages);
            }
        });
        mDirDialog.show();
    }
}
