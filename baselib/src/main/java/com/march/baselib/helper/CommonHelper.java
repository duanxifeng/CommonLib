package com.march.baselib.helper;

import android.os.Environment;

import java.io.Closeable;
import java.io.IOException;

/**
 * Project  : CommonLib </br>
 * Package  : com.march.baselib </br>
 * CreateAt : 16/8/15 </br> </br>
 * Describe : 一些小的辅助方法，由于比较碎片，不单独提取到帮助类中</br>
 *
 * @author chendong </br>
 */
public class CommonHelper {

    /**
     * sd卡是否可用
     * @return 可用？
     */
    public static boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 关闭读写流
     * @param closeable 读写流
     * @return 关闭成功
     */
    public static boolean closeStream(Closeable... closeable) {
        for (Closeable close : closeable) {
            if (close != null)
                try {
                    close.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }
}
