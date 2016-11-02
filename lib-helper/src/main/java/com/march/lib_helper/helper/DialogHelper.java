package com.march.lib_helper.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib.helper
 * CreateAt : 16/9/2
 * Describe : 显示Dialog
 *
 * @author chendong
 */
public class DialogHelper {

    public static void showDialog(Context context) {
        new AlertDialog.Builder(context)
                .setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }
}
