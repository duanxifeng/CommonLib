package com.march.commonlib;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

//import com.march.lib.core.activity.BaseActivity;;
import com.march.commonlib.adapter.AdapterMainActivity;
import com.march.lib.core.activity.BaseActivity;
import com.march.lib.support.helper.ImagePicker;

import java.io.File;

public class MainActivity extends BaseActivity {

    private ImageView iv;

    @Override
    public void onInitDatas() {
        super.onInitDatas();
    }

    @Override
    public void onInitIntent(Bundle intent) {
        super.onInitIntent(intent);
        iv = getView(R.id.iv);
//        SelectImageActivity.selectImages(mActivity, 60);
        startActivity(new Intent(this, AdapterMainActivity.class));
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
        return false;
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

    public void clickBtn(View view) {
//        if (view.getId() == R.id.capture) {
//            new PickerBuilder(AbsImagePicker.PICK_CAPTURE, mActivity)
//                    .listener(listener)
//                    .build()
//                    .pick();
//        } else {
//            new PickerBuilder(AbsImagePicker.PICK_ALBUM, this)
//                    .listener(listener)
//                    .build()
//                    .pick();
//        }
    }
}
