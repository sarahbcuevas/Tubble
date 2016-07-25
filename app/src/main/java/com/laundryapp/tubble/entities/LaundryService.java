package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

public class LaundryService extends SugarRecord {

    public static final int WASH_DRY_FOLD = 0;
    public static final int WASH_DRY_PRESS = 1;
    public static final int COMFORTER_CLEANING = 2;
    public static final int HOUSEHOLD_ITEM_CLEANING = 3;
    public static final int DRY_CLEANING = 4;

    private String mLabel;

    public LaundryService() {
    }

    public LaundryService(String mLabel) {
        this.mLabel = mLabel;
    }

    public String getLabel() {
        return mLabel;
    }
}
