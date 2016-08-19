package com.march.baselib.inter;

import android.widget.ImageView;

/**
 * Project  : CommonLib <p>
 * Package  : com.march.baselib <p>
 * CreateAt : 16/8/16 <p>
 * Describe : 给类库定义加载图片的方式<p>
 *
 * @author chendong <p>
 */
public interface HowLoadImg {
    void loadImg(ImageView iv, int w, int h, String path);
}
