package com.march.baselib.helper.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * com.march.baselib.helper.imagepicker
 * CommonLib
 * Created by chendong on 16/8/15.
 * Copyright © 2016年 chendong. All rights reserved.
 * Desc :使用相机拍照
 */
class CapturePicker extends AbsImagePicker {
    public CapturePicker(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public File getRealFile() {
        return mCaptureImageFile;
    }

    @Override
    public void pick() {
        super.pick();
        mCaptureImageFile = getFile("capture", "jpg");
        mActivity.startActivityForResult(createIntent(), CHOOSE_PHOTO_FROM_CAMERA);
    }


    @Override
    protected Intent createIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCaptureImageFile));
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // 设置图片
        if (requestCode == CHOOSE_PHOTO_FROM_CAMERA && resultCode == Activity.RESULT_OK) {
            // 拍照返回,如果先前传入的文件路径下有文件，就通过回调返回
            if (mCaptureImageFile != null && mCaptureImageFile.exists()) {
                if (!isCrop)
                    publishResult(mCaptureImageFile);
                else
                    startSystemCrop(Uri.fromFile(mCaptureImageFile), 300);
            }
        }
    }
}
