package com.march.baselib.inter;

import android.widget.ImageView;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/16
 * Describe : 给类库定义加载图片的方式
 *
 * @author chendong
 */
public interface HowLoadImg {
    void loadImg(ImageView iv, int w, int h, String path);
}
