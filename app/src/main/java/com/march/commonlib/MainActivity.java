package com.march.commonlib;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.march.baselib.helper.AnnotationHelper;
import com.march.baselib.helper.Logger;
import com.march.baselib.helper.imagepicker.AbsImagePicker;
import com.march.baselib.helper.imagepicker.ImagePicker;
import com.march.baselib.helper.imagepicker.PickerBuilder;
import com.march.baselib.ui.activity.SelectImageActivity;
import com.march.baselib.ui.activity.BaseActivity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ImageView iv;

    @Override
    protected void onInitDatas() {
        super.onInitDatas();
    }

    @Override
    protected void onInitViews(Bundle save) {
        super.onInitViews(save);
        iv = getView(R.id.iv);
        SelectImageActivity.selectImages(mActivity, 60);
    }

    @TestAnnotation(testCode = 100, value = {"a", "b"})
    public void test() {
        Logger.e("test");
    }

    @Override
    protected void onInitEvents() {
        super.onInitEvents();
    }

    @Override
    protected String[] getPermission2Check() {
        return new String[0];
    }

    @Override
    protected void onStartWorks() {
        super.onStartWorks();
//        SelectImageActivity.selectImages(mActivity, 5);

        List<Method> methods = AnnotationHelper.getMethods(MainActivity.class, TestAnnotation.class);
        for (Method m : methods) {
            Logger.e(m.toString());
            TestAnnotation annotation = m.getAnnotation(TestAnnotation.class);
            Logger.e(annotation.testCode());
            for (String str : annotation.value()) {
                Logger.e(str);
            }
        }
        AnnotationHelper.executeMethod(this, methods.get(0));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isInitTitle() {
        return false;
    }

    private AbsImagePicker.OnPickerOverListener listener = new AbsImagePicker.OnPickerOverListener() {
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

    public void clickBtn(View view) {
        if (view.getId() == R.id.capture) {
            new PickerBuilder(AbsImagePicker.PICK_CAPTURE, mActivity)
                    .listener(listener)
                    .build()
                    .pick();
        } else {
            new PickerBuilder(AbsImagePicker.PICK_ALBUM, mActivity)
                    .listener(listener)
                    .build()
                    .pick();
        }
    }
}
