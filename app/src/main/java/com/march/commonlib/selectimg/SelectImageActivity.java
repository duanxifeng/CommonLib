package com.march.commonlib.selectimg;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.march.baselib.helper.DimensionHelper;
import com.march.baselib.helper.ImageHelper;
import com.march.baselib.helper.Toaster;
import com.march.baselib.inter.OnCommonListener;
import com.march.baselib.model.ImageInfo;
import com.march.baselib.module.TitleModule;
import com.march.baselib.ui.activity.BaseActivity;
import com.march.baselib.ui.dialog.BaseDialog;
import com.march.commonlib.R;
import com.march.quickrvlibs.RvViewHolder;
import com.march.quickrvlibs.SimpleRvAdapter;
import com.march.quickrvlibs.inter.OnClickListener;
import com.march.slidingselect.SlidingSelectLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private TextView mEnsureTv;
    private SlidingSelectLayout mSlidingSelectLy;
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
    private AsyncTask<Void, Void, Void> scanImageTask;
    private TextView mDirTv;

    protected void loadImg(ImageView iv, int w, int h, String path) {
        Glide.with(mContext).load(path).centerCrop().thumbnail(0.1f).into(iv);
    }

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
            Toaster.get().show(mContext, "您没有允许读取存储卡，不能继续操作");
            return false;
        }
        return true;
    }

    @Override
    protected void onInitDatas() {
        super.onInitDatas();
        simpleDateFormat = new SimpleDateFormat("yyyy年M月d日 HH:mm:ss", Locale.CHINA);
        mSelectImages = new ArrayList<>();
        startScanImageTask();
    }


    //扫描图片文件
    private void startScanImageTask() {
        scanImageTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                size = (int) (DimensionHelper.getScreenWidth(mContext) / 3.0f);
                mImagesMap = ImageHelper.formatImages4EachDir(ImageHelper.getImagesByMediaStore(mContext));
                mCurrentImages = mImagesMap.get(ImageHelper.ALL_IMAGE_KEY);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                createOrUpdateAdapter();
            }
        };
        scanImageTask.execute();
    }

    // 创建和更新adapter
    private void createOrUpdateAdapter() {
        if (mImageAdapter != null) {
            mImageAdapter.updateData(mCurrentImages);
            return;
        }
        mImageAdapter = new SimpleRvAdapter<ImageInfo>(mContext, mCurrentImages, R.layout.select_image_item_rv) {
            @Override
            public void onBindView(final RvViewHolder holder, final ImageInfo data, final int pos, int type) {
                mSlidingSelectLy.markView(holder.getParentView(), pos, data);
                View coverView = holder.getView(R.id.cover_select_image);
                TextView tv = (TextView) holder.getView(R.id.tv_select_image);
                tv.setTag(R.id.data_image_info, data);
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
                loadImg(iv, size, size, data.getPath());

                View parentView = holder.getParentView();
                parentView.getLayoutParams().width = size;
                parentView.getLayoutParams().height = size;

                View.OnClickListener lis = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyCheckOrNotCheck(holder, data);
                    }
                };
                holder.setClickLis(R.id.view_click_cover, lis);
                holder.setClickLis(R.id.tv_select_image, lis);
            }
        };
        mImageAdapter.setOnChildClickListener(new OnClickListener<ImageInfo>() {
            @Override
            public void onItemClick(int pos, RvViewHolder holder, ImageInfo data) {
                Toaster.get().show(mContext, "点击预览");
            }
        });
        mImageRv.setAdapter(mImageAdapter);
    }

    @Override
    protected void onInitViews(Bundle save) {
        super.onInitViews(save);
        mEnsureTv = getView(R.id.tv_ensure);
        mEnsureTv.setText("完成  " + mSelectImages.size() + "/" + mSelectImageMaxNum);
        mSlidingSelectLy = getView(R.id.sliding);
        mImageRv = getView(R.id.rv_select_image);
        mLayoutManager = (GridLayoutManager) mImageRv.getLayoutManager();
        mDateTv = getView(R.id.tv_time_image);
        mTitleModule.setText("返回", ImageHelper.ALL_IMAGE_KEY, "预览");
        mAlphaAnimator = ObjectAnimator
                .ofFloat(mDateTv, "alpha", 1.0f, 0f)
                .setDuration(1500);
    }

    @Override
    protected void onInitEvents() {
        super.onInitEvents();
        mDirTv = getView(R.id.tv_select_image_dir);
        mDirTv.setOnClickListener(this);

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

        mImageRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        mSlidingSelectLy.setOnSlidingSelectListener(new SlidingSelectLayout.OnSlidingSelectListener<ImageInfo>() {
            @Override
            public void onSlidingSelect(int pos, View parentView, ImageInfo data) {
                notifyCheckOrNotCheck((RvViewHolder) mImageRv.findViewHolderForAdapterPosition(pos), data);
            }
        });
    }

    /**
     * 修改选中和不选中的状态
     *
     * @param holder
     */
    private void notifyCheckOrNotCheck(RvViewHolder holder, ImageInfo imageInfo) {
        TextView tv = (TextView) holder.getView(R.id.tv_select_image);
        View coverView = holder.getView(R.id.cover_select_image);
        if (tv.isSelected()) {
            mSelectImages.remove(imageInfo);
            tv.setSelected(false);
            coverView.setVisibility(View.GONE);
            tv.setText("");
        } else {
            if (mSelectImageMaxNum != NO_LIMIT && mSelectImages.size() >= mSelectImageMaxNum) {
                Toaster.get().show(mContext, "最多只能选择" + mSelectImageMaxNum + "张");
                return;
            }
            mSelectImages.add(imageInfo);
            tv.setSelected(true);
            coverView.setVisibility(View.VISIBLE);
            tv.setText(String.valueOf(mSelectImages.indexOf(imageInfo) + 1));
        }
        mEnsureTv.setText("完成  " + mSelectImages.size() + "/" + mSelectImageMaxNum);
        changeNumberDisplayByHolder();
    }

    // 更新其他的item更新数字显示
    private void changeNumberDisplayByHolder() {
        int findSelectCount = 0;
        ImageInfo temp;
        TextView tv;
        RvViewHolder holder;
        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
//            Logger.e("发现 " +findSelectCount + " 被选中","总共 " + mSelectImages.size() + "  个被选中");
            //尽量提前推出，只对第一屏有效
            if (findSelectCount >= mSelectImages.size()) {
                break;
            }
            holder = (RvViewHolder) mImageRv.findViewHolderForAdapterPosition(i);
            tv = (TextView) holder.getView(R.id.tv_select_image);
            if (tv.isSelected()) {
                temp = (ImageInfo) tv.getTag(R.id.data_image_info);
                if (temp != null && mSelectImages.contains(temp)) {
                    tv.setText(String.valueOf(mSelectImages.indexOf(temp) + 1));
                    findSelectCount++;
                }
            }
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
        scanImageTask.cancel(true);
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
                mDirTv.setText(data.getDirName());
                mImageAdapter.updateData(mCurrentImages);
            }
        });
        mDirDialog.show();
    }

    // 目录选择弹窗
    public class ImageDirDialog extends BaseDialog {

        private OnCommonListener<ImageDirInfo> listener;
        private RecyclerView mDirRv;
        private List<ImageDirInfo> mImageDirs;
        private SimpleRvAdapter<ImageDirInfo> mDirAdapter;
        private int lastCheckPos = 0;

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
                public void onBindView(RvViewHolder holder, ImageDirInfo data, int pos, int type) {
                    ImageView iv = (ImageView) holder.getView(R.id.iv_dir_cover);
                    int size = DimensionHelper.dp2px(mContext, 80);
                    loadImg(iv, size, size, data.getCoverInfo().getPath());
                    holder.setText(R.id.tv_dir_name, data.getDirName());
                    holder.setText(R.id.tv_dir_img_num, data.getPicNum() + "");
                    holder.getView(R.id.iv_dir_sign).setVisibility(pos == lastCheckPos?View.VISIBLE:View.INVISIBLE);
                }
            };
            mDirAdapter.setOnChildClickListener(new com.march.quickrvlibs.inter.OnClickListener<ImageDirInfo>() {
                @Override
                public void onItemClick(int pos, RvViewHolder holder, ImageDirInfo data) {
                    if (listener != null) {
                        listener.onEvent(pos, mImageDirs.get(pos));
                    }
                    int prePos = lastCheckPos;
                    lastCheckPos = pos;
                    mDirAdapter.notifyItemChanged(prePos);
                    mDirAdapter.notifyItemChanged(lastCheckPos);
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
            int mDialogHeight = (int) (DimensionHelper.getScreenHeight(mContext) * 0.725f);
            setWindowParams(MATCH, mDialogHeight, 1.0f, 0.8f, Gravity.BOTTOM);
            getWindow().setWindowAnimations(R.style.dialog_anim_bottom_to_center);
        }

        @Override
        public void show() {
            mDirRv.scrollToPosition(0);
            super.show();
        }
    }

}
