package com.march.commonlib.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import com.march.lib_helper.helper.FileHelper;

import java.io.File;

/**
 * com.march.baselib.helper.imagepicker
 * CommonLib
 * Created by chendong on 16/8/15.
 * Copyright © 2016年 chendong. All rights reserved.
 * Desc :从相册选择
 */
class AlbumPicker extends AbsImagePicker {

    public AlbumPicker(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public File getRealFile() {
        return mAlbumImageFile;
    }

    @Override
    public void pick() {
        super.pick();
        mActivity.startActivityForResult(createIntent(), CHOOSE_PHOTO_FROM_ALBUM);
    }

    @Override
    protected Intent createIntent() {
        // 调用系统相册
        Intent intent = new Intent(Intent.ACTION_PICK);// 系统相册action
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        return intent;
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(context, requestCode, resultCode, intent);
        if (requestCode == CHOOSE_PHOTO_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
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
                FileHelper.copyTo(new File(path), mAlbumImageFile, true);
                if (!isCrop)
                    publishResult(mAlbumImageFile);
                else
                    startSystemCrop(Uri.fromFile(mAlbumImageFile), 300);
            }
        }
    }
}
