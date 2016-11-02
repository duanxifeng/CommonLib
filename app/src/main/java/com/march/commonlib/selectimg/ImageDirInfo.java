package com.march.commonlib.selectimg;


import android.support.annotation.NonNull;

import com.march.lib_helper.model.ImageInfo;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : 图像目录信息
 *
 * @author chendong
 */
public class ImageDirInfo implements Comparable<ImageDirInfo> {
    private int picNum;
    private String dirName;
    private ImageInfo coverInfo;

    public ImageDirInfo(int picNum, String dirName, ImageInfo coverInfo) {
        this.picNum = picNum;
        this.dirName = dirName;
        this.coverInfo = coverInfo;
    }

    public int getPicNum() {
        return picNum;
    }

    public void setPicNum(int picNum) {
        this.picNum = picNum;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public ImageInfo getCoverInfo() {
        return coverInfo;
    }

    public void setCoverInfo(ImageInfo coverInfo) {
        this.coverInfo = coverInfo;
    }

    @Override
    public int compareTo(@NonNull ImageDirInfo another) {
        return another.picNum - picNum;
    }
}
