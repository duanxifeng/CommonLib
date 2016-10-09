package com.march.baselib.helper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Data;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.march.baselib.develop.DevelopLib;
import com.march.baselib.model.ContactInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project  : CommonLib
 * Package  : com.march.baselib
 * CreateAt : 16/8/15
 * Describe : 联系人操作
 *
 * @author chendong
 */
public class ContactHelper {
    private static String MyPhoneNumber;
    /**
     * 获取库Phone表字段,仅仅获取电话号码联系人等
     * 它所指向的其实是“content:// com.android.contacts/data/phones”。
     * 这个url对应着contacts表 和 raw_contacts表 以及 data表 所以说我们的数据都是从这三个表中获取的。
     */
    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID};

    /**
     * 联系人显示名称*
     */
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码*
     */
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 联系人姓名+号码的list*
     */
    private static ArrayList<ContactInfo> contactsList = new ArrayList<>();

    private static int REQUEST_PICK_CONTACT = 0x12;

    /**
     * 获取到联系人回调
     */
    public interface OnGetContactInfo {
        void onGetContactInfo(ContactInfo info);
    }

    /**
     * 获取联系人列表
     *
     * @param context context
     * @param isClear 刷新后获取，为true时，之前获取的缓存数据会被清空，为false时，如果有缓存优先返回
     * @return 联系人列表
     */
    public static List<ContactInfo> getContacts(Context context, boolean isClear) {
        if (contactsList != null && isClear)
            contactsList.clear();
        if (contactsList != null && contactsList.size() > 0)
            return contactsList;
        //得到手机中的联系人信息
        getPhoneContacts(context);
        //得到sim卡中联系人信息
        getSIMContacts(context);
        return contactsList;
    }

    /**
     * 打开通讯录选择联系人
     *
     * @param activity activity
     */
    public static void openContacts(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        activity.startActivityForResult(intent, REQUEST_PICK_CONTACT);
    }

    /**
     * 选择后返回数据处理
     *
     * @param context    context
     * @param reqCode    请求码
     * @param resultCode 返回码
     * @param data       数据
     * @param listener   监听
     */
    public static void onActivityResult(Context context, int reqCode, int resultCode, Intent data, OnGetContactInfo listener) {
        if (resultCode == Activity.RESULT_OK && reqCode == REQUEST_PICK_CONTACT) {
            Uri contactData = data.getData();
            ContactInfo info = getContactInfoById(context, contactData.getLastPathSegment());
            if (listener != null)
                listener.onGetContactInfo(info);
        }
    }

    /**
     * 获得用户自己的手机号码
     * Requires Permission:
     * {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
     *
     * @param context 上下文
     * @return 手机号码
     */
    public static String getMyPhoneNumber(Context context) {
        if (MyPhoneNumber == null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            MyPhoneNumber = telephonyManager.getLine1Number();
        }
        return MyPhoneNumber;
    }

    /**
     * 匹配正常的手机号和+86的手机号,有+86就去掉返回去掉后的手机号,不匹配返回null
     *
     * @param phoneNum 选择的手机号
     * @return 如果手机号不匹配, 返回null
     */
    public static String filter86PhoneNum(String phoneNum) {
        if (phoneNum != null) {
            phoneNum = phoneNum.replace(" ", "").replace("-", "");
            Pattern p1 = Pattern.compile("^((\\+{0,1}86){0,1})1[34578]{1}[0-9]{9}");
            Matcher m1 = p1.matcher(phoneNum);
            if (m1.matches()) {
                Pattern p2 = Pattern.compile("^((\\+{0,1}86){0,1})");
                Matcher m2 = p2.matcher(phoneNum);
                StringBuffer sb = new StringBuffer();
                while (m2.find()) {
                    m2.appendReplacement(sb, "");
                }
                m2.appendTail(sb);
                return sb.toString();
            }
        }
        return null;
    }

    /**
     * 从自己的应用跳转到系统的联系人选择界面获取的contactId
     * 再通过查询到对应的phone
     */
    private static ContactInfo getContactInfoById(Context context, String contactId) {
        //type:TYPE_MOBILE,TYPE_HOME,TYPE_WORK,这里由于都有可能是手机号码,所以不需要设置
        // int type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        ContactInfo model = null;
        String[] whereArgs = new String[]{String.valueOf(contactId)};
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone._ID + " = ?",
                whereArgs,
                null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    String phoneNumber = filter86PhoneNum(cursor.getString(cursor.getColumnIndexOrThrow(Phone.NUMBER)));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(Phone.DISPLAY_NAME));
                    model = new ContactInfo(name, phoneNumber);
                }
            } finally {
                cursor.close();
            }
        }
        return model;
    }


    /**
     * 得到手机通讯录联系人信息
     *
     * @param context context
     */
    private static void getPhoneContacts(Context context) {
        ContentResolver resolver = context.getContentResolver();
        //query查询，得到结果的游标
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue;
                }
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                ContactInfo cb = new ContactInfo(contactName, phoneNumber);
                contactsList.add(cb);
            }
            phoneCursor.close();
        }
    }

    /**
     * 得到手机SIM卡联系人人信息
     *
     * @param context context
     */
    private static void getSIMContacts(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue;
                }
                // 得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                ContactInfo cb = new ContactInfo(contactName, phoneNumber);
                contactsList.add(cb);
            }
            phoneCursor.close();
        }
    }

    /**
     * 添加一个联系人数据
     *
     * @param context     context
     * @param name        name
     * @param phoneNumber phone
     * @return 返回true表示添加成功，false表示失败
     */

    public static boolean insert(Context context, String name, String phoneNumber) {
        //根据号码找数据，如果存在则不添加
        if (getNameByPhoneNumber(context,phoneNumber) == null) {
            //插入raw_contacts表，并获取_id属性
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            long contact_id = ContentUris.parseId(resolver.insert(uri, values));
            //插入data表
            uri = Uri.parse("content://com.android.contacts/data");
            //添加姓名
            values.put("raw_contact_id", contact_id);
            values.put(Data.MIMETYPE, "vnd.android.cursor.item/name");
            values.put("data2", name);
            resolver.insert(uri, values);
            values.clear();
            //添加手机号码
            values.put("raw_contact_id", contact_id);
            values.put(Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
            values.put("data2", "2");    //2表示手机
            values.put("data1", phoneNumber);
            resolver.insert(uri, values);
            values.clear();
            return true;
        } else {
            return false;
        }
    }

    //

    /**
     * 删除单个数据，会直接删除是这个名字的人的所有信息
     *
     * @param context context
     * @param name    用户的姓名
     * @return 是否删除成功
     */
    public static boolean delete(Context context, String name) {
        try {
            //根据姓名求id
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            ContentResolver resolver = context.getContentResolver();
            //查询到name=“name”的集合
            Cursor cursor = resolver.query(uri, new String[]{Data._ID},
                    "display_name=?", new String[]{name}, null);
            assert cursor != null;
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                //根据id删除data中的相应数据
                resolver.delete(uri, "display_name=?", new String[]{name});
                uri = Uri.parse("content://com.android.contacts/data");
                resolver.delete(uri, "raw_contact_id=?", new String[]{id + ""});
                return true;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    /**
     * 修改联系人数据
     *@param context context
     * @param name        name
     * @param phoneNumber phone
     * @return 是否成功
     */
    public static boolean update(Context context,String name, String phoneNumber) {
        try {
            //根据姓名求id,再根据id删除
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, new String[]{Data._ID}, "display_name=?", new String[]{name}, null);
            assert cursor != null;
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                Uri mUri = Uri.parse("content://com.android.contacts/data");//对data表的所有数据操作
                ContentResolver mResolver = context.getContentResolver();
                ContentValues values = new ContentValues();
                values.put("data1", phoneNumber);
                mResolver.update(mUri, values, "mimetype=? and raw_contact_id=?", new String[]{"vnd.android.cursor.item/phone_v2", id + ""});
                return true;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据电话号码查询姓名
     *@param context context
     * @param phoneNumber phone
     * @return 返回这个电话的主人名，如果没有则返回null
     */
    public static String getNameByPhoneNumber(Context context,String phoneNumber) {
        String name = null;
        //uri=  content://com.android.contacts/data/phones/filter/#
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phoneNumber);
        ContentResolver resolver = context.getContentResolver();
        //从raw_contact表中返回display_name
        Cursor cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);

        assert cursor != null;
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }
}
