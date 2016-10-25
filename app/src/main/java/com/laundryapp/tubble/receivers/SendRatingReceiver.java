package com.laundryapp.tubble.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.UserRating;
import com.laundryapp.tubble.fragment.StatusFragment;

public class SendRatingReceiver extends BroadcastReceiver {

    private final String TAG  = this.getClass().getName();
    private static long mDetailsId = -1;
    private static float mRating = -1;
    private static String mComments = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                if (mDetailsId != -1 && mRating != -1 && mComments != null) {
                    UserRating userRating = new UserRating(mDetailsId, mRating, mComments);
                    long id = userRating.save();
                    StatusFragment.onRatingSent(id);
                    Toast.makeText(context, "Rating added.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Rating added.");
                }
                mDetailsId = -1;
                mRating = -1;
                mComments = null;
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "Error sending rating and comments/suggestions: No service available", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error sending rating and comments/suggestions: No service available");
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Sending rating and comments/suggestions failed. Please try again later.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Sending rating and comments/suggestions failed. Please try again later.");
                break;
            default:
                Toast.makeText(context, "Failed to send rating and comments/suggestions. Please try again later.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Failed to send rating and comments/suggestions. Please try again later.");
                break;
        }
    }

    public static void setBookingDetailsWaitingResponse(long detailsId, float rating, String comments) {
        mDetailsId = detailsId;
        mRating = rating;
        mComments = comments;
    }
}
