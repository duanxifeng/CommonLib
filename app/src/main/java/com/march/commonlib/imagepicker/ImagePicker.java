package com.march.commonlib.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.march.lib.core.common.Logger;
import com.march.lib.support.helper.BitmapHelper;
import com.march.lib.support.helper.FileHelper;
import com.march.lib.support.helper.PathHelper;

import java.io.File;
import java.util.UUID;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : 选择照片的基类,全都放到一个类里面，也是可行的，但是代码不够清晰,不建议使用
 *
 * @author chendong
 */
@Deprecated
public class ImagePicker {

    private static final int PICK_ALBUM = 0;
    public static final int PICK_CAPTURE = 1;
    private static final int CHOOSE_PHOTO_FROM_CAMERA = 0x11;
    private static final int CHOOSE_PHOTO_FROM_ALBUM = 0x12;
    private static final int CHOOSE_PHOTO_FROM_SYSTEM_CROP = 0x13;
    private int type;
    protected Activity mActivity;
    private String mSaveImageParentPath = PathHelper.TEMP_PATH;//获取图片或者剪切后存放目录
    private File mCropImageFile;//裁剪之后的文件
    private File mAlbumImageFile;//从相册返回的文件
    private File mCaptureImageFile;//从拍照返回的文件
    private int mQuality = 100;
    private int xRatio = 1, yRatio = 1;
    private int width = 300, height = 300;
    private boolean isCrop = true;
    private OnPickerOverListener mListener;

    public ImagePicker(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void pick() {
        //检测参数
        if (width / height != xRatio / yRatio) {
            throw new IllegalArgumentException(" width / height != xRatio / yRatio ");
        }
        if (type == PICK_ALBUM) {
            mActivity.startActivityForResult(createIntent(), CHOOSE_PHOTO_FROM_ALBUM);
        } else {
            mCaptureImageFile = getFile("capture", "jpg");
            mActivity.startActivityForResult(createIntent(), CHOOSE_PHOTO_FROM_CAMERA);
        }
    }

    protected File getRealFile() {
        if (type == PICK_ALBUM) {
            return mAlbumImageFile;
        } else {
            return mCaptureImageFile;
        }
    }

    private Intent createIntent() {
        if (type == PICK_ALBUM) {
            // 调用系统相册
            Intent intent = new Intent(Intent.ACTION_PICK);// 系统相册action
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            return intent;
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCaptureImageFile));
            return intent;
        }
    }

    public void onActivityResult(Context context,int requestCode, int resultCode, Intent intent) {
        if (requestCode == CHOOSE_PHOTO_FROM_SYSTEM_CROP && resultCode == Activity.RESULT_OK) {
            // 头像裁剪返回
            if (!mCropImageFile.exists()) {
                Logger.e("裁剪后文件不存在");
                return;
            }
            if (mQuality != 100) {
                Bitmap bmp = BitmapFactory.decodeFile(mCropImageFile.getAbsolutePath());
                File newFile = getFile("afterCompress", "jpg");
                BitmapHelper.compressImage(context,bmp, newFile, mQuality, true);
                publishResult(newFile);
            } else
                publishResult(mCropImageFile);
        } else if (requestCode == CHOOSE_PHOTO_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            // 相册返回,存放在path路径的文件中
            String path = null;
            if (intent == null || intent.getData() == null)
                return;
            // 获得相册中图片的路径
            if ("file".equals(intent.getData().getScheme())) {
                path = intent.getData().getPath();
            } else if ("content".equals(intent.getData().getScheme())) {
                Cursor cursor = mActivity.getContentResolver().query(intent.getData(), null, null, null, null);
                assert cursor != null;
                if (cursor.moveToFirst())
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }
            // 如果路径存在，就复制文件到temp文件夹中
            if (path != null && path.length() > 0) {
                mAlbumImageFile = getFile("album", "jpg");
                FileHelper.copyTo(new File(path), mAlbumImageFile,true);
                if (!isCrop)
                    publishResult(mAlbumImageFile);
                else
                    startSystemCrop(Uri.fromFile(mAlbumImageFile), 300);
            }
        } else if (requestCode == CHOOSE_PHOTO_FROM_CAMERA && resultCode == Activity.RESULT_OK) {
            // 拍照返回,如果先前传入的文件路径下有文件，就通过回调返回
            if (mCaptureImageFile != null && mCaptureImageFile.exists()) {
                if (!isCrop)
                    publishResult(mCaptureImageFile);
                else
                    startSystemCrop(Uri.fromFile(mCaptureImageFile), 300);
            }
        }
    }


    public ImagePicker pickType(int type) {
        this.type = type;
        return this;
    }


    public ImagePicker setRatio(int xRatio, int yRatio) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        return this;
    }

    public ImagePicker setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ImagePicker setQuality(int quality) {
        mQuality = quality;
        return this;
    }

    public ImagePicker setListener(OnPickerOverListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public ImagePicker setIsCrop(boolean isCrop) {
        this.isCrop = isCrop;
        return this;
    }

    public interface OnPickerOverListener {
        void onPickerOver(File file);
    }

    private File getFile(String sign, String suffix) {
        // 通过uuid生成照片唯一名字
        String mOutFileName = UUID.randomUUID().toString() + "_" + sign + "_image." + suffix;
        return new File(mSaveImageParentPath, mOutFileName);
    }

    private void publishResult(File file) {
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
    private void startSystemCrop(Uri uri, int size) {
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
}
