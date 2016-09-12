package com.laundryapp.tubble.entities;

import android.util.Log;

import com.laundryapp.tubble.fragment.DatePickerFragment.Date;
import com.laundryapp.tubble.fragment.TimePickerFragment.Time;
import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BookingDetails extends SugarRecord {

    public enum Mode {PICKUP, DELIVERY}

    public enum Type {PERSONAL, COMMERCIAL}

    public enum Status {NEW, ACCEPTED, COMPLETED, REJECTED}

    private Mode mMode;
    private Type mType;
    private Status mStatus;
    private long mLaundryShopId, mServiceId, mUserId;
    private String mLocation, mNotes;
    private long mPickupDate, mReturnDate, mDateCreated;
//    private Time mPickupTime, mReturnTime, mTimeCreated;
    private int mNoOfClothes;
    private float mEstimatedKilo;
    public static final String BOOKING_URI = "booking_uri";

    public BookingDetails() {
    }

    public BookingDetails(Mode mMode, Type mType, long mServiceId, long mLaundryShopId, long mUserId, String mLocation, String mNotes, long mPickupDate, long mReturnDate, int mNoOfClothes, float mEstimatedKilo) {
        this.mMode = mMode;
        this.mType = mType;
        this.mServiceId = mServiceId;
        this.mLaundryShopId = mLaundryShopId;
        this.mUserId = mUserId;
        this.mLocation = mLocation;
        this.mNotes = mNotes;
        this.mPickupDate = mPickupDate;
        this.mReturnDate = mReturnDate;
//        this.mPickupTime = mPickupTime;
//        this.mReturnTime = mReturnTime;
        this.mNoOfClothes = mNoOfClothes;
        this.mEstimatedKilo = mEstimatedKilo;
        this.mStatus = Status.NEW;
        this.mDateCreated = System.currentTimeMillis();
//        this.mDateCreated = mDateCreated;
//        this.mTimeCreated = mTimeCreated;
//        Log.d("BookingDetails", "Pickup: " + this.mPickupDate +"\n" +
//        "Return: " + this.mReturnDate + "\n" +
//        "Created: " + this.mDateCreated);
//        setDateTimeCreated();
    }

//    private void setDateTimeCreated() {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//        Date date = new Date();
//        date.setDate(year, month, day);
//        this.mDateCreated = date;
//        Time time = new Time();
//        time.setTime(hour, minute);
//    }

    public LaundryShop getLaundryShop() {
        LaundryShop laundryShop = LaundryShop.findById(LaundryShop.class, mLaundryShopId);
        return laundryShop;
    }

    public long getDateCreatedInMillis() {
        return mDateCreated;
    }

    public String getDateTimeCreated() {
//        Log.d("BookingDetails", "Date Created: " + mDateCreated);
//        Log.d("BookingDetails", "Pickup Date: " + mPickupDate);
//        Log.d("BookingDetails", "Return Date: " + mReturnDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mDateCreated);
//        calendar.set(mDateCreated.getYear(), mDateCreated.getMonth(), mDateCreated.getDate(), mTimeCreated.getHour(), mTimeCreated.getMinute());
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
        return format.format(calendar.getTime());
//        return "";
    }

    public String getTypeName() {
        return mType == Type.COMMERCIAL ? "Commercial" : "Personal";
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
        LaundryShopService service = LaundryShopService.findById(LaundryShopService.class, mServiceId);
        return mEstimatedKilo * service.getPrice();
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
}
