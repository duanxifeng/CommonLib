package com.march.commonlib.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.march.commonlib.R;
import com.march.commonlib.imagepicker.AbsImagePicker;
import com.march.commonlib.imagepicker.PickerBuilder;
import com.march.lib.core.activity.BaseActivity;
import com.march.lib.core.widget.TitleBarView;
import com.march.lib.support.helper.ImagePicker;

import java.io.File;

public class SysSelectImageAndCropActivity extends BaseActivity {

    private ImageView iv;

    @Override
    public void onInitIntent(Bundle intent) {
        super.onInitIntent(intent);
        iv = getView(R.id.iv);
    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        mTitleBarView.setText(TitleBarView.POS_Center,"系统照片选择裁剪");
    }

    @Override
    protected String[] getPermission2Check() {
        return new String[0];
    }

    @Override
    public void onStartWorks() {
        super.onStartWorks();
//        SelectImageActivity.selectImages(mActivity, 5);
//        List<Method> methods = AnnotationHelper.getMethods(MainActivity.class, TestAnnotation.class);
//        for (Method m : methods) {
//            Logger.e(m.toString());
//            TestAnnotation annotation = m.getAnnotation(TestAnnotation.class);
//            Logger.e(annotation.testCode());
//            for (String str : annotation.value()) {
//                Logger.e(str);
//            }
//        }
//        AnnotationHelper.executeMethod(this, methods.get(0));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isInitTitle() {
        return true;
    }

    private ImagePicker.OnPickerOverListener listener = new ImagePicker.OnPickerOverListener() {
        @Override
        public void onPickerOver(File file) {
            iv.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }
    };
    private ImagePicker.OnPickerOverListener listener2 = new ImagePicker.OnPickerOverListener() {
        @Override
        public void onPickerOver(File file) {
            iv.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }
    };

    private AbsImagePicker.OnPickerOverListener listener3 = new AbsImagePicker.OnPickerOverListener() {
        @Override
        public void onPickerOver(File file) {
            iv.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }
    };

    private AbsImagePicker picker;

    public void clickBtn(View view) {
        if (view.getId() == R.id.capture) {
            picker = new PickerBuilder(AbsImagePicker.PICK_CAPTURE, mActivity)
                    .listener(listener3)
                    .build();
            picker.pick();
        } else {
            picker = new PickerBuilder(AbsImagePicker.PICK_ALBUM, mActivity)
                    .listener(listener3)
                    .build();
            picker.pick();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        picker.onActivityResult(mContext, requestCode, resultCode, data);
    }
}
