package com.march.lib.support.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Project  : CommonLib
 * Package  : com.march.lib.support.model
 * CreateAt : 2016/10/31
 * Describe :
 *
 * @author chendong
 */

public class ContactInfo implements Parcelable {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.contactName);
        dest.writeString(this.phoneNumber);
    }

    protected ContactInfo(Parcel in) {
        this.contactName = in.readString();
        this.phoneNumber = in.readString();
    }

    public static final Creator<ContactInfo> CREATOR = new Creator<ContactInfo>() {
        @Override
        public ContactInfo createFromParcel(Parcel source) {
            return new ContactInfo(source);
        }

        @Override
        public ContactInfo[] newArray(int size) {
            return new ContactInfo[size];
        }
    };
}