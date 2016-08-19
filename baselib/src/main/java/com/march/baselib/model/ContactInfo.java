package com.march.baselib.model;

/**
 * Project  : CommonLib </br>
 * Package  : com.march.baselib </br>
 * CreateAt : 16/8/15 </br> </br>
 * Describe : 联系人信息</br>
 *
 * @author chendong </br>
 */
public class ContactInfo {
    private String contactName;
    private String phoneNumber;

    public ContactInfo(String contactName, String phoneNumber) {
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}