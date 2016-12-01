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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.march.commonlib.R;
import com.march.lib.adapter.common.SimpleItemListener;
import com.march.lib.adapter.core.BaseViewHolder;
import com.march.lib.adapter.core.SimpleRvAdapter;
import com.march.lib.core.activity.BaseActivity;
import com.march.lib.core.common.DimensionHelper;
import com.march.lib.core.common.Toaster;
import com.march.lib.core.dialog.BaseDialog;
import com.march.lib.support.helper.ImageHelper;
import com.march.lib.support.model.ImageInfo;
import com.march.lib.view.SlidingSelectLayout;

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

    public static final String KEY_SELECT_IMAGE_RESULT = "KEY_SELECT_IMAGE_RESULT";
    public static final int RESULT_CODE_SELECT_IMAGE = 0x123;
    private static int REQ_CODE = 0x12;
    private static String KEY_LIMIT = "KEY_LIMIT";
    public static int NO_LIMIT = -1;


    private int size;
    private int mSelectImageMaxNum = 0;

    private TextView mDirTv;
    private RecyclerView mImageRv;
    private TextView mEnsureTv;
    private SlidingSelectLayout mSlidingSelectLy;
    private TextView mDateTv;

    private SimpleDateFormat simpleDateFormat;
    private GridLayoutManager mLayoutManager;
    private ObjectAnimator mAlphaAnimator;
    // 扫描文件的任务
    private AsyncTask<Void, Void, Void> scanImageTask;
    private SimpleRvAdapter<ImageInfo> mImageAdapter;
    // 目录 － 目录下的图片列表
    private Map<String, List<ImageInfo>> mImagesMap;
    // 当前的图片列表
    private List<ImageInfo> mCurrentImages;
    // 被选中的图片列表
    private ArrayList<ImageInfo> mSelectImages;
    private ImageDirDialog mDirDialog;
    private PreviewDialog mPreviewDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.select_image_activity;
    }


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
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_LIMIT, limit);
        Intent intent = buildIntent(from, SelectImageActivity.class, bundle);
        from.startActivityForResult(intent, REQ_CODE);
    }

    /**
     * 从返回数据中获取选中的图片
     *
     * @param intent onActivityResult(Intent data)
     * @return ArrayList<ImageInfo>
     */
    public static ArrayList<ImageInfo> getSelectImage(Intent intent) {
        ArrayList<ImageInfo> rst = intent.getParcelableArrayListExtra(KEY_SELECT_IMAGE_RESULT);
        return rst;
    }


    @Override
    public void onInitIntent(Bundle intent) {
        super.onInitIntent(intent);
        mSelectImageMaxNum = intent.getInt(KEY_LIMIT, NO_LIMIT);
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
    public void onInitDatas() {
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
            mImageAdapter.notifyDataSetChanged(mCurrentImages, true);
            return;
        }
        mImageAdapter = new SimpleRvAdapter<ImageInfo>(mContext, mCurrentImages, R.layout.select_image_item_rv) {

            @Override
            public void onBindView(final BaseViewHolder holder, final ImageInfo data, int pos, int type) {
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
        mImageAdapter.setItemListener(new SimpleItemListener<ImageInfo>() {
            @Override
            public void onClick(int pos, BaseViewHolder holder, ImageInfo data) {
                showPreviewDialog(data.getPath());
            }
        });
        mImageRv.setAdapter(mImageAdapter);
    }


    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        mEnsureTv = getView(R.id.tv_ensure);
        mEnsureTv.setText("完成  " + mSelectImages.size() + "/" + mSelectImageMaxNum);
        mSlidingSelectLy = getView(R.id.sliding);
        mImageRv = getView(R.id.rv_select_image);
        mLayoutManager = (GridLayoutManager) mImageRv.getLayoutManager();
        mDateTv = getView(R.id.tv_time_image);
        mTitleBarView.setText("返回", ImageHelper.ALL_IMAGE_KEY, "预览");
        mTitleBarView.setLeftBackListener(mActivity);
        mAlphaAnimator = ObjectAnimator
                .ofFloat(mDateTv, "alpha", 1.0f, 0f)
                .setDuration(1500);
    }

    @Override
    public void onInitEvents() {
        super.onInitEvents();
        mDirTv = getView(R.id.tv_select_image_dir);
        mDirTv.setOnClickListener(this);

        mEnsureTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(KEY_SELECT_IMAGE_RESULT, mSelectImages);
                setResult(RESULT_CODE_SELECT_IMAGE, intent);
                finish();
            }
        });

        mImageRv.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                // 获取第一个位置
                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItemPosition < 0)
                    return;
                String date = mCurrentImages.get(firstVisibleItemPosition).getDate();
                mDateTv.setText(simpleDateFormat.format(new Date(Long.parseLong(date) * 1000)));
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
                notifyCheckOrNotCheck((BaseViewHolder) mImageRv.findViewHolderForAdapterPosition(pos), data);
            }
        });
    }

    /**
     * 修改选中和不选中的状态
     *
     * @param holder
     */
    private void notifyCheckOrNotCheck(BaseViewHolder holder, ImageInfo imageInfo) {
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
        BaseViewHolder holder;
        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
            //尽量提前推出，只对第一屏有效
            if (findSelectCount >= mSelectImages.size()) {
                break;
            }
            holder = (BaseViewHolder) mImageRv.findViewHolderForAdapterPosition(i);
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
    protected void onDestroy() {
        super.onDestroy();
        if (mImagesMap != null)
            mImagesMap.clear();
        if (mCurrentImages != null)
            mCurrentImages.clear();
        if (mSelectImages != null)
            mSelectImages.clear();
        mImagesMap = null;
        mCurrentImages = null;
        mSelectImages = null;
        if (scanImageTask != null)
            scanImageTask.cancel(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_select_image_dir) {
            showDirDialog();
        }
    }


    private void showPreviewDialog(String path) {
        if (mPreviewDialog != null) {
            mPreviewDialog.show(path);
            return;
        }

        mPreviewDialog = new PreviewDialog(mContext);
        mPreviewDialog.setCancelable(true);
        mPreviewDialog.setCanceledOnTouchOutside(true);
        mPreviewDialog.show(path);
    }

    private void showDirDialog() {
        if (checkDialog2Show(mDirDialog))
            return;
        mDirDialog = new ImageDirDialog(mActivity, mImagesMap);
        mDirDialog.setListener(new OnImageDirClickListener() {
            @Override
            public void onClickDir(int pos, ImageDirInfo data) {
                mCurrentImages = mImagesMap.get(data.getDirName());
                mTitleBarView.setText(mTitleBarView.POS_Center, data.getDirName());
                mDirTv.setText(data.getDirName());
                mImageAdapter.notifyDataSetChanged(mCurrentImages, true);
            }
        });
        mDirDialog.show();
    }

    public interface OnImageDirClickListener {
        void onClickDir(int pos, ImageDirInfo dir);
    }

    public class PreviewDialog extends BaseDialog {

        private ImageView mPreviewIv;
        private int width, height;

        public PreviewDialog(Context context) {
            super(context);
        }

        @Override
        protected void initViews() {
            width = (int) (DimensionHelper.getScreenWidth(mContext) * 0.8f);
            height = (int) (width * (4.0f / 3));
            mPreviewIv = (ImageView) findViewById(R.id.iv_preview);
            assert mPreviewIv != null;
            ViewGroup.LayoutParams layoutParams = mPreviewIv.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            mPreviewIv.setLayoutParams(layoutParams);
            mPreviewIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }


        @Override
        protected int getLayoutId() {
            return R.layout.select_image_preview_dialog;
        }

        @Override
        protected void setWindowParams() {
            buildDefaultParams();
        }

        public void show(String path) {
            loadImg(mPreviewIv, width, height, path);
            super.show();
        }
    }

    /**
     * 目录选择弹窗
     */
    public class ImageDirDialog extends BaseDialog {
        private OnImageDirClickListener listener;
        private RecyclerView mDirRv;
        private List<ImageDirInfo> mImageDirs;
        private SimpleRvAdapter<ImageDirInfo> mDirAdapter;
        private int lastCheckPos = 0;

        public ImageDirDialog(Context context, Map<String, List<ImageInfo>> mImageMap) {
            super(context);
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

        public void setListener(OnImageDirClickListener listener) {
            this.listener = listener;
        }

        private void createAdapter() {
            mDirAdapter = new SimpleRvAdapter<ImageDirInfo>(getContext(), mImageDirs, R.layout.select_image_dir_item) {
                @Override
                public void onBindView(BaseViewHolder holder, ImageDirInfo data, int pos, int type) {
                    ImageView iv = (ImageView) holder.getView(R.id.iv_dir_cover);
                    int size = DimensionHelper.dp2px(mContext, 80);
                    loadImg(iv, size, size, data.getCoverInfo().getPath());
                    holder.setText(R.id.tv_dir_name, data.getDirName());
                    holder.setText(R.id.tv_dir_img_num, data.getPicNum() + "");
                    holder.getView(R.id.iv_dir_sign).setVisibility(pos == lastCheckPos ? View.VISIBLE : View.INVISIBLE);
                }
            };
            mDirAdapter.setItemListener(new SimpleItemListener<ImageDirInfo>() {
                @Override
                public void onClick(int pos, BaseViewHolder holder, ImageDirInfo data) {
                    if (listener != null) {
                        listener.onClickDir(pos, mImageDirs.get(pos));
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
            buildDefaultParams(MATCH, mDialogHeight, 1.0f, 0.8f, Gravity.BOTTOM);
            setBottomToCenterAnimation();
        }

        @Override
        public void show() {
            mDirRv.scrollToPosition(0);
            super.show();
        }
    }

}
