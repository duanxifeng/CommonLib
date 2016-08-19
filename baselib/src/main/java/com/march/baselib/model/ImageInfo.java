package com.march.baselib.model;

import android.support.annotation.NonNull;

/**
 * Project  : CommonLib <p>
 * Package  : com.march.baselib <p>
 * CreateAt : 16/8/15 <p>
 * Describe : 图片信息<p>
 *
 * @author chendong <p>
 */
public class ImageInfo implements Comparable<ImageInfo> {
    // 设置id为自增长的组件
    private Integer id;
    // 文件地址
    private String path;
    //0未选中,1选中未插入数据库,||(这边是已经插入数据库的可能状态)2选中插入数据库,3已经上传照片,4完全发布
    private int status;
    // 照片名字
    private String name;
    // 秒数
    private String date;
    private int fileId;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && path.equals(((ImageInfo) o).getPath());
    }

    @Override
    public int compareTo(@NonNull ImageInfo another) {
        long a = Long.parseLong(date);
        long b = Long.parseLong(another.getDate());
        if (b > a) {
            return 1;
        } else if (b < a) {
            return -1;
        } else {
            return 0;
        }
    }
}