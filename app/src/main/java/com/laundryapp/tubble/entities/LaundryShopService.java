package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

public class LaundryShopService extends SugarRecord {

    private long laundryshop_id;
    private long laundryservice_id;
    private float mPrice;

    public LaundryShopService() {
    }

    public LaundryShopService(long laundryshop_id, long laundryservice_id, float mPrice) {
        this.laundryshop_id = laundryshop_id;
        this.laundryservice_id = laundryservice_id;
        this.mPrice = mPrice;
    }

    public long getLaundryShopId() {
        return laundryshop_id;
    }

    public long getLaundryServiceId() {
        return laundryservice_id;
    }

    public float getPrice() {
        return mPrice;
    }
}
