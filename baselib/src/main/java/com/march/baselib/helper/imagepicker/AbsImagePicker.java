package com.march.baselib.helper.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.march.baselib.helper.BitmapHelper;
import com.march.baselib.helper.Logger;
import com.march.baselib.helper.PathHelper;

import java.io.File;
import java.util.UUID;

/**
 * Project  : CommonLib </br>
 * Package  : com.march.baselib </br>
 * CreateAt : 16/8/15 </br> </br>
 * Describe : 选择图片工具</br>
 *
 * @author chendong </br>
 */
public abstract class AbsImagePicker {
    public static final int PICK_ALBUM = 0;
    public static final int PICK_CAPTURE = 1;
    protected static final int CHOOSE_PHOTO_FROM_CAMERA = 0x11;
    protected static final int CHOOSE_PHOTO_FROM_ALBUM = 0x12;
    protected static final int CHOOSE_PHOTO_FROM_SYSTEM_CROP = 0x13;
    protected Activity mActivity;
    protected String mSaveImageParentPath = PathHelper.TempPath;//获取图片或者剪切后存放目录
    protected File mCropImageFile;//裁剪之后的文件
    protected File mAlbumImageFile;//从相册返回的文件
    protected File mCaptureImageFile;//从拍照返回的文件
    private int mQuality = 100;
    private int xRatio = 1, yRatio = 1;
    private int width = 300, height = 300;
    protected boolean isCrop = true;
    private OnPickerOverListener mListener;


    public interface OnPickerOverListener {
        void onPickerOver(File file);
    }

    AbsImagePicker(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * 开始选择照片，之前使用PickerBuilder构建
     */
    public void pick() {
        //检测参数
        if (width / height != xRatio / yRatio) {
            throw new IllegalArgumentException(" width / height != xRatio / yRatio ");
        }
    }

    /**
     * 处理结果
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param intent      数据
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CHOOSE_PHOTO_FROM_SYSTEM_CROP && resultCode == Activity.RESULT_OK) {
            // 头像裁剪返回
            if (!mCropImageFile.exists()) {
                Logger.e("裁剪后文件不存在");
                return;
            }
            if (mQuality != 100) {
                Bitmap bmp = BitmapFactory.decodeFile(mCropImageFile.getAbsolutePath());
                File newFile = getFile("afterCompress", "jpg");
                BitmapHelper.compressImage(bmp, newFile, mQuality, true);
                publishResult(newFile);
            } else
                publishResult(mCropImageFile);
        }
    }

    /**
     * 暂时没使用
     */
    protected File syncPick() {
        //检测参数
        if (width / height != xRatio / yRatio) {
            throw new IllegalArgumentException(" width / height != xRatio / yRatio ");
        }
        if (isCrop)
            return mCropImageFile;
        else
            return getRealFile();
    }

    protected abstract File getRealFile();


    protected abstract Intent createIntent();


    protected File getFile(String sign, String suffix) {
        // 通过uuid生成照片唯一名字
        String mOutFileName = UUID.randomUUID().toString() + "_" + sign + "_image." + suffix;
        return new File(mSaveImageParentPath, mOutFileName);
    }

    protected void publishResult(File file) {
        if (mListener != null) {
            mListener.onPickerOver(file);
        }
    }

    /**
     * 启动裁剪图片
     *
     * @param uri  文件uri
     * @param size 输出大小
     */
    protected void startSystemCrop(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", xRatio);
        intent.putExtra("aspectY", yRatio);

        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);// 黑边
        intent.putExtra("noFaceDetection", true); // no face detection
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", true);
        // 剪切返回，头像存放的路径
        mCropImageFile = getFile("crop", "png");
        intent.putExtra("output", Uri.fromFile(mCropImageFile)); // 专入目标文件
        mActivity.startActivityForResult(intent, CHOOSE_PHOTO_FROM_SYSTEM_CROP);
    }

    void setRatio(int xRatio, int yRatio) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;
    }

    void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    void setQuality(int quality) {
        mQuality = quality;
    }

    void setListener(OnPickerOverListener mListener) {
        this.mListener = mListener;
    }

    void setIsCrop(boolean isCrop) {
        this.isCrop = isCrop;
    }
}
