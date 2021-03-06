package com.march.commonlib.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;

import com.march.lib.core.activity.BaseActivity;;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : 构建选择照片工具
 *
 * @author chendong
 */
public class PickerBuilder {

    private AbsImagePicker mPicker;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AbsImagePicker.PICK_ALBUM, AbsImagePicker.PICK_CAPTURE})
    public @interface PickType {

    }

    /**
     * 构造方法，如果继承自BaseActivity可以
     *
     * @param type     相册／相机
     * @param activity BaseActivity
     */
    public PickerBuilder(@PickType int type, Activity activity) {
        if(!(activity instanceof BaseActivity))
            throw new IllegalArgumentException("activity must extends BaseActivity");
        switch (type) {
            case AbsImagePicker.PICK_ALBUM:
                mPicker = new AlbumPicker(activity);
                break;
            case AbsImagePicker.PICK_CAPTURE:
                mPicker = new CapturePicker(activity);
                break;
        }
    }

    /**
     * 裁剪后的图片质量
     *
     * @param quality 质量
     * @return PickerBuilder
     */
    public PickerBuilder quality(int quality) {
        mPicker.setQuality(quality);
        return this;
    }

    /**
     * 裁剪比例
     *
     * @param xRatio x比例
     * @param yRatio y比例
     * @return PickerBuilder
     */
    public PickerBuilder ratio(int xRatio, int yRatio) {
        mPicker.setRatio(xRatio, yRatio);
        return this;
    }

    /**
     * 裁剪的打小
     *
     * @param width  宽度
     * @param height 高度
     * @return PickerBuilder
     */
    public PickerBuilder size(int width, int height) {
        mPicker.setSize(width, height);
        return this;
    }

    /**
     * 是否裁剪
     *
     * @param isCrop 是否裁剪
     * @return PickerBuilder
     */
    public PickerBuilder isCrop(boolean isCrop) {
        mPicker.setIsCrop(isCrop);
        return this;
    }

    /**
     * 裁剪或选择后监听
     *
     * @param listener 监听
     * @return PickerBuilder
     */
    public PickerBuilder listener(AbsImagePicker.OnPickerOverListener listener) {
        mPicker.setListener(listener);
        return this;
    }

    /**
     * 构建
     *
     * @return AbsImagePicker
     */
    public AbsImagePicker build() {
        return mPicker;
    }

    /**
     * 处理返回数据
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        数据
     */
    public void onActivityResult(Context context,int requestCode, int resultCode, Intent data) {
        mPicker.onActivityResult(context,requestCode, resultCode, data);
    }
}
