package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

public class User extends SugarRecord {
    private String mFullName;
    private String mMobileNumber;
    private String mEmailAddress;
    private String mAddress;
    private String mPassword;
    private String mUserPhoto;

    public enum Type { CUSTOMER, LAUNDRY_SHOP }

    public static final String USER_ID = "user_id";

    public User() {
    }

    public User(String mFullName, String mMobileNumber, String mEmailAddress, String mAddress, String mPassword, String mUserPhoto) {
        this.mFullName = mFullName;
        this.mMobileNumber = mMobileNumber;
        this.mEmailAddress = mEmailAddress;
        this.mAddress = mAddress;
        this.mPassword = mPassword;
        this.mUserPhoto = mUserPhoto;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getUserPhoto() { return mUserPhoto; }

    public void setUserPhoto (String mUserPhoto) {
        this.mUserPhoto = mUserPhoto;
    }
}
