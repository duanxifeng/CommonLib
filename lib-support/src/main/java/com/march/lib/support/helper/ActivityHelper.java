package com.march.lib.support.helper;

import android.app.Activity;

import com.march.lib.support.R;

/**
 * Project  : Reaper
 * Package  : com.march.reaper.helper
 * CreateAt : 2016/11/30
 * Describe :
 *
 * @author chendong
 */

public class ActivityHelper {


    public static void translateStart(Activity activity) {
        activity.overridePendingTransition(R.anim.translate_in, R.anim.ano_anim);
    }

    public static void translateFinish(Activity activity) {
        activity.overridePendingTransition(R.anim.ano_anim, R.anim.translate_out);
    }

    public static void fadeStart(Activity activity) {
        activity.overridePendingTransition(R.anim.fade_in,R.anim.ano_anim);
    }

    public static void fadeFinish(Activity activity) {
        activity.overridePendingTransition(R.anim.ano_anim, R.anim.fade_out);
    }
}
