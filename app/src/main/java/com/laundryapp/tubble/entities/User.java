package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

public class User extends SugarRecord {
    private String mUniqueId;
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

    public User(String mFullName, String mMobileNumber, String mEmailAddress, String mAddress, String mPassword, String mUserPhoto, String mUniqueId) {
        this.mFullName = mFullName;
        this.mMobileNumber = mMobileNumber;
        this.mEmailAddress = mEmailAddress;
        this.mAddress = mAddress;
        this.mPassword = mPassword;
        this.mUserPhoto = mUserPhoto;
        this.mUniqueId = mUniqueId;
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

    public String getPassword() {
        return mPassword;
    }

    public String getUniqueId() {
        return mUniqueId;
    }

    public void setUserPhoto (String mUserPhoto) {
        this.mUserPhoto = mUserPhoto;
    }

    public void setFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public void setMobileNumber(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
    }

    public void setEmailAddress(String mEmailAddress) {
        this.mEmailAddress = mEmailAddress;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public void setUniqueId(String mUniqueId) {
        this.mUniqueId = mUniqueId;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }
}
