package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

public class LaundryService extends SugarRecord {

    public enum Service {WASH_DRY_FOLD, WASH_DRY_PRESS, WASH_DRY, DRY_CLEAN}

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
