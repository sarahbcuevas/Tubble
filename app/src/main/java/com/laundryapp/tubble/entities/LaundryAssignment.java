package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

public class LaundryAssignment extends SugarRecord{

    private long mLaundryShopStaffId;
    private long mBookingDetailsId;

    public LaundryAssignment() {

    }

    public LaundryAssignment(long mLaundryShopStaffId, long mBookingDetailsId) {
        this.mLaundryShopStaffId = mLaundryShopStaffId;
        this.mBookingDetailsId = mBookingDetailsId;
    }

    public long getLaundryShopStaffId() {
        return mLaundryShopStaffId;
    }

    public long getBookingDetailsId() {
        return mBookingDetailsId;
    }

    public String getLaundryStaffName() {
        LaundryShopStaff staff = LaundryShopStaff.findById(LaundryShopStaff.class, mLaundryShopStaffId);
        return staff.getName();
    }
}
