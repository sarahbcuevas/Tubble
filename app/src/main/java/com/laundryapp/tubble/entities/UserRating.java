package com.laundryapp.tubble.entities;

import com.orm.SugarRecord;

public class UserRating extends SugarRecord {

    private long mScheduleId;
    private float mRating;
    private String mComments;

    public UserRating() {
    }

    public UserRating(long mScheduleId, float mRating, String mComments) {
        this.mScheduleId = mScheduleId;
        this.mRating = mRating;
        this.mComments = mComments;
    }

    public long getScheduleId() {
        return mScheduleId;
    }

    public float getRating() {
        return mRating;
    }

    public String getComments() {
        return mComments;
    }
}
