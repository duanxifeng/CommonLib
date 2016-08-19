package com.march.baselib.develop;

import android.content.Context;

import com.march.baselib.helper.PathHelper;
import com.march.baselib.inter.HowLoadImg;


/**
 * Project  : CommonLib <p>
 * Package  : com.march.baselib <p>
 * CreateAt : 16/8/15 <p>
 * Describe : 类库处理类 <p>
 *
 * @author chendong <p>
 */
public class DevelopLib {

    private static HowLoadImg mLoadImg;
    private static Context mContext;

    /**
     * 初始化
     *
     * @param mCtx    上下文
     * @param loadImg 图片加载工具
     */
    public static void initLib(Context mCtx, HowLoadImg loadImg) {
        mContext = mCtx;
        mLoadImg = loadImg;
        PathHelper.initPath();
    }

    public static Context getCtx() {
        return mContext;
    }

    public static HowLoadImg getLoadImg() {
        return mLoadImg;
    }

}