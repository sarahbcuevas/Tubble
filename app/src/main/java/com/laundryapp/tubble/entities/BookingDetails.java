package com.laundryapp.tubble.entities;

import android.util.Log;

import com.laundryapp.tubble.fragment.DatePickerFragment.Date;
import com.laundryapp.tubble.fragment.TimePickerFragment.Time;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BookingDetails extends SugarRecord {

    public enum Mode {PICKUP, DELIVERY}

    public enum Type {PERSONAL, COMMERCIAL}

    public enum Status {NEW, ACCEPTED, PROCESSING, COMPLETED, REJECTED}

    private Mode mMode;
    private Type mType;
    private Status mStatus;
    private long mLaundryShopId, mServiceId, mUserId;
    private String mLocation, mNotes;
    private long mPickupDate, mReturnDate, mDateCreated;
    private int mNoOfClothes;
    private float mEstimatedKilo, mFee;
    public static final String BOOKING_URI = "booking_uri";

    public BookingDetails() {
    }

    public BookingDetails(long mDateCreated, Mode mMode, Type mType, long mServiceId, long mLaundryShopId, long mUserId, String mLocation, String mNotes, long mPickupDate, long mReturnDate, int mNoOfClothes, float mEstimatedKilo, float mFee) {
        this.mMode = mMode;
        this.mType = mType;
        this.mServiceId = mServiceId;
        this.mLaundryShopId = mLaundryShopId;
        this.mUserId = mUserId;
        this.mLocation = mLocation;
        this.mNotes = mNotes;
        this.mPickupDate = mPickupDate;
        this.mReturnDate = mReturnDate;
        this.mNoOfClothes = mNoOfClothes;
        this.mEstimatedKilo = mEstimatedKilo;
        this.mFee = mFee;
        this.mStatus = Status.NEW;
        this.mDateCreated = mDateCreated;
//        this.mDateCreated = System.currentTimeMillis();
    }

    public LaundryShop getLaundryShop() {
        LaundryShop laundryShop = LaundryShop.findById(LaundryShop.class, mLaundryShopId);
        return laundryShop;
    }

    public long getDateCreatedInMillis() {
        return mDateCreated;
    }

    public String getDateTimeCreated() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mDateCreated);
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
        return format.format(calendar.getTime());
    }

    public Type getType() {
        return mType;
    }

    public String getTypeName() {
        return mType == Type.COMMERCIAL ? "Commercial" : "Personal";
    }

    public long getLaundryServiceId() {
        return mServiceId;
    }

    public String getLaundryServiceName() {
        LaundryShopService service = LaundryShopService.findById(LaundryShopService.class, mServiceId);
        LaundryService serv = LaundryService.findById(LaundryService.class, service.getLaundryServiceId());

        return serv.getLabel();
    }

    public Mode getMode() {
        return mMode;
    }

    public String getModeName() {
        return mMode == Mode.PICKUP ? "Pick-Up": "Delivery";
    }

    public float getFee() {
//        LaundryShopService service = LaundryShopService.findById(LaundryShopService.class, mServiceId);
//        return mEstimatedKilo * service.getPrice();
        return mFee;
    }

    public String getLocation() {
        return mLocation;
    }

    public long getPickupDate() {
        return mPickupDate;
    }

    public long getReturnDate() {
        return mReturnDate;
    }

    public Status getStatus() { return mStatus; }

    public void setStatus(Status status) {
        this.mStatus = status;
    }

    public String getCustomerName() {
        User user = User.findById(User.class, mUserId);
        if (user != null) {
            return user.getFullName();
        }
        return null;
    }

    public long getUserId() { return mUserId; }

    public int getNoOfClothes() { return mNoOfClothes; }

    public float getEstimatedKilo() { return mEstimatedKilo; }

    public String getNotes() { return mNotes; }

    public void setFee(float fee) {
        this.mFee = fee;
    }

    public void setNoOfClothes(int noOfClothes) {
        this.mNoOfClothes = noOfClothes;
    }

    public void setEstimatedKilo(float estimatedKilo) {
        this.mEstimatedKilo = estimatedKilo;
    }
}
