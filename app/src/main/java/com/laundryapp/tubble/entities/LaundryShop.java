package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

import java.util.ArrayList;

public class LaundryShop extends SugarRecord {

    private String mName, mAddress, mSchedule, mContact;
    private long mLocationCoordinateX = 0, mLocationCoordinateY = 0;
    private float mTotalRating = 0;
    private int mRatingCount = 1;
    private float mRating = 0;

    public LaundryShop() {
    }

    public LaundryShop(String mName, String mAddress, String mSchedule, String mContact, float mRating) {
        this.mName = mName;
        this.mAddress = mAddress;
        this.mSchedule = mSchedule;
        this.mContact = mContact;
        this.mTotalRating = mRating;
        this.mRating = mRating;
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

    public void addRating(int rating) {
        mTotalRating += rating;
        mRatingCount++;
        mRating = mTotalRating / mRatingCount;
    }

    public float getRating() {
        return mRating;
    }

    public void setLocationCoordinates(long x, long y) {
        mLocationCoordinateX = x;
        mLocationCoordinateY = y;
    }

    public long[] getLocationCoordinates() {
        return new long[]{mLocationCoordinateX, mLocationCoordinateY};
    }
}
