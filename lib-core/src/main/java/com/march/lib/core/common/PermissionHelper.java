package com.march.lib.core.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/17
 * Describe : Android M 权限控制
 *
 * @author chendong
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PermissionHelper {

    private static final int REQ_PERMISSION_CODE = 0x12;

    /**
     * 全部的dangerous permission
     */
    public static final String PER_GROUP_CONTACTS = Manifest.permission_group.CONTACTS;
    public static final String PER_WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS;
    public static final String PER_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PER_READ_CONTACTS = Manifest.permission.READ_CONTACTS;

    public static final String PER_GROUP_PHONE = Manifest.permission_group.PHONE;
    public static final String PER_READ_CALL_LOG = Manifest.permission.READ_CALL_LOG;
    public static final String PER_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PER_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PER_WRITE_CALL_LOG = Manifest.permission.WRITE_CALL_LOG;
    public static final String PER_USE_SIP = Manifest.permission.USE_SIP;
    public static final String PER_PROCESS_OUTGOING_CALLS = Manifest.permission.PROCESS_OUTGOING_CALLS;
    public static final String PER_ADD_VOICEMAIL = Manifest.permission.ADD_VOICEMAIL;

    public static final String PER_GROUP_CALENDAR = Manifest.permission_group.CALENDAR;
    public static final String PER_READ_CALENDAR = Manifest.permission.READ_CALENDAR;
    public static final String PER_WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR;

    public static final String PER_GROUP_CAMERA = Manifest.permission_group.CAMERA;
    public static final String PER_CAMERA = Manifest.permission.CAMERA;

    public static final String PER_GROUP_SENSORS = Manifest.permission_group.SENSORS;
    public static final String PER_BODY_SENSORS = Manifest.permission.BODY_SENSORS;

    public static final String PER_GROUP_LOCATION = Manifest.permission_group.LOCATION;
    public static final String PER_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PER_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    public static final String PER_GROUP_STORAGE = Manifest.permission_group.STORAGE;
    public static final String PER_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PER_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public static final String PER_GROUP_MICROPHONE = Manifest.permission_group.MICROPHONE;
    public static final String PER_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;

    public static final String PER_GROUP_SMS = Manifest.permission_group.SMS;
    public static final String PER_READ_SMS = Manifest.permission.READ_SMS;
    public static final String PER_RECEIVE_WAP_PUSH = Manifest.permission.RECEIVE_WAP_PUSH;
    public static final String PER_RECEIVE_MMS = Manifest.permission.RECEIVE_MMS;
    public static final String PER_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
    public static final String PER_SEND_SMS = Manifest.permission.SEND_SMS;


    /**
     * 打开权限管理界面
     *
     * @param context 上下文
     */
    public static void openPermissionPage(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }


    /**
     * 动态申请权限
     *
     * @param context     上下文
     * @param permissions 需要申请的权限
     * @return 是不是需要申请
     */
    public static boolean checkPermission(Activity context, String[] permissions) {
        //6.0以上
        if (isOverMarshmallow()) {
            //没有权限需要申请时,直接返回true,相当于权限全部通过
            if (permissions == null || permissions.length <= 0)
                return true;

            //过滤没有申请到的权限
            List<String> noOkPermissions = new ArrayList<>();
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                    noOkPermissions.add(permission);
//                    if (whenClickNotRemind(context, permission)) {
//                        return false;
//                    }
                }
            }

            //没有没申请的权限，相当于权限全部通过
            if (noOkPermissions.size() <= 0)
                return true;
            //6.0以上需要申请权限
            for (String noOkPermission : noOkPermissions) {
                Logger.e(" noOkPermission " + noOkPermission);
            }
            context.requestPermissions(noOkPermissions.toArray(new String[noOkPermissions.size()]), REQ_PERMISSION_CODE);
            return false;
        }

        //6.0以下安装时已经全部申请
        return true;
    }


    private static boolean whenClickNotRemind(Activity context, String permission) {
        // 点了拒绝之后不再提示,返回false,否则返回true
        // 网上说，只要点击了拒绝，小米就会一直是false，单独适配
        boolean clickNotRemind = !context.shouldShowRequestPermissionRationale(permission);

        if (Build.MANUFACTURER.toLowerCase().contains("xiaomi")) {

        }

        if (clickNotRemind) {
            Logger.e("点击不再提示 ");
            openPermissionPage(context);
            return true;
        } else {
            Logger.e("没有点击不再提示 ");
            return false;
        }
    }

    /**
     * 处理权限申请的结果，返回结构化的数据
     *
     * @param requestCode  请求码
     * @param permissions  被请求的权限
     * @param grantResults 请求结果
     * @param listener     监听
     */
    public static void onRequestPermissionsResult(int requestCode,
                                                  @NonNull String[] permissions,
                                                  @NonNull int[] grantResults,
                                                  OnPermissionHandleOverListener listener) {
        if (requestCode != REQ_PERMISSION_CODE)
            return;

        Map<String, Integer> result = new ArrayMap<>();
        boolean isHavePermissionNotOk = false;
        for (int i = 0; i < Math.min(permissions.length, grantResults.length); i++) {
            result.put(permissions[i], grantResults[i]);
            //有权限没有同意
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isHavePermissionNotOk = true;
            }
        }
        //如果权限全部同意，继续执行
        if (listener != null)
            listener.onHandleOver(!isHavePermissionNotOk, result);
    }


    public interface OnPermissionHandleOverListener {
        void onHandleOver(boolean isOkExactly, Map<String, Integer> result);
    }


    static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
