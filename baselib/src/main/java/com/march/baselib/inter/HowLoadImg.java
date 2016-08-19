package com.march.baselib.inter;

import android.widget.ImageView;

/**
 * Project  : CommonLib </br>
 * Package  : com.march.baselib </br>
 * CreateAt : 16/8/16 </br> </br>
 * Describe : 给类库定义加载图片的方式</br>
 *
 * @author chendong </br>
 */
public interface HowLoadImg {
    void loadImg(ImageView iv, int w, int h, String path);
}
