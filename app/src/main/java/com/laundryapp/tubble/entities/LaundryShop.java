package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

import java.util.ArrayList;

public class LaundryShop extends SugarRecord {

    private String mName, mAddress, mSchedule, mContact, mLogin, mPassword;
    private double mLocationCoordinateX = 0, mLocationCoordinateY = 0;
    private float mTotalRating = 0;
    private int mRatingCount = 1;
    private float mRating = 0;

    public LaundryShop() {
    }

    public LaundryShop(String mName, String mAddress, String mSchedule, String mContact, float mRating, String mLogin, String mPassword) {
        this.mName = mName;
        this.mAddress = mAddress;
        this.mSchedule = mSchedule;
        this.mContact = mContact;
        this.mTotalRating = mRating;
        this.mRating = mRating;
        this.mLogin = mLogin;
        this.mPassword = mPassword;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getSchedule() {
        return mSchedule;
    }

    public String getContact() {
        return mContact;
    }

    public String getLogin() { return mLogin; }

    public void addRating(float rating) {
        mTotalRating += rating;
        mRatingCount++;
        mRating = mTotalRating / mRatingCount;
    }

    public float getRating() {
        return mRating;
    }

    public void setLocationCoordinates(double x, double y) {
        mLocationCoordinateX = x;
        mLocationCoordinateY = y;
    }

    public double[] getLocationCoordinates() {
        return new double[]{mLocationCoordinateX, mLocationCoordinateY};
    }

    public String getPassword() {
        return mPassword;
    }
}
