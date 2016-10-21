package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

public class LaundryShopStaff extends SugarRecord{

    private String mName;
    private long mLaundryShopId;

    public LaundryShopStaff() {

    }

    public LaundryShopStaff(String mName, long mLaundryShopId) {
        this.mName = mName;
        this.mLaundryShopId = mLaundryShopId;
    }

    public String getName() {
        return mName;
    }

    public long getLaundryShopId() {
        return mLaundryShopId;
    }
}
