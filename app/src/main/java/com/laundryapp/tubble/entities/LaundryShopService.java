package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

public class LaundryShopService extends SugarRecord {

    private long mLaundryShopId;
    private long mLaundryServiceId;
    private float mPrice;

    public LaundryShopService() {
    }

    public LaundryShopService(long laundryshop_id, long laundryservice_id, float mPrice) {
        this.mLaundryShopId = laundryshop_id;
        this.mLaundryServiceId = laundryservice_id;
        this.mPrice = mPrice;
    }

    public long getLaundryShopId() {
        return mLaundryShopId;
    }

    public long getLaundryServiceId() {
        return mLaundryServiceId;
    }

    public float getPrice() {
        return mPrice;
    }
}
