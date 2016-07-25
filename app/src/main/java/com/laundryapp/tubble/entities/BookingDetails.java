package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

import java.util.Date;

public class BookingDetails extends SugarRecord {

    public enum Mode {PICKUP, DELIVERY}

    public enum Type {PERSONAL, COMMERCIAL}

    public enum Status {NEW, ACCEPTED, REJECTED}

    private Mode mMode;
    private Type mType;
    private Status mStatus;
    private long mLaundryShopId, mServiceId, mUserId;
    private String mLocation, mNotes;
    private Date mPickupDateTime, mReturnDateTime, mDateCreated;
    private int mNoOfClothes;
    private float mEstimatedKilo;

    public BookingDetails() {
    }

    public BookingDetails(Mode mMode, Type mType, long mServiceId, long mLaundryShopId, long mUserId, String mLocation, String mNotes, Date mPickupDateTime, Date mReturnDateTime, int mNoOfClothes, float mEstimatedKilo) {
        this.mMode = mMode;
        this.mType = mType;
        this.mServiceId = mServiceId;
        this.mLaundryShopId = mLaundryShopId;
        this.mUserId = mUserId;
        this.mLocation = mLocation;
        this.mNotes = mNotes;
        this.mPickupDateTime = mPickupDateTime;
        this.mReturnDateTime = mReturnDateTime;
        this.mNoOfClothes = mNoOfClothes;
        this.mEstimatedKilo = mEstimatedKilo;
        this.mStatus = Status.NEW;
        // mDateCreated
    }
}
