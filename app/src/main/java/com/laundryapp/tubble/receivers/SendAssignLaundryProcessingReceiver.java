package com.laundryapp.tubble.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.LaundryAssignment;
import com.laundryapp.tubble.entities.LaundryShopStaff;
import com.laundryapp.tubble.fragment.StatusFragment;

public class SendAssignLaundryProcessingReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();
    private static BookingDetails mDetails = null;
    private static LaundryShopStaff mAssignee = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                if (mDetails != null && mAssignee != null) {
                    LaundryAssignment assignment = new LaundryAssignment(mAssignee.getId(), mDetails.getId());
                    assignment.save();
                    mDetails.setStatus(BookingDetails.Status.PROCESSING);
                    mDetails.save();
                    StatusFragment.updateLaundrySchedule(mDetails.getId());
                    Toast.makeText(context, "Laundry assignment has been sent.", Toast.LENGTH_SHORT).show();
                }
                mDetails = null;
                mAssignee = null;
                Log.d(TAG, "Laundry assignment has been sent.");
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "Error sending laundry assignment: No service available", Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Sending laundry assignment failed. Please try again later.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.d(TAG, "Laundry assignment was not sent.");
                break;
        }
    }

    public static void setAssignee(BookingDetails details, LaundryShopStaff assignee) {
        mDetails = details;
        mAssignee = assignee;
    }
}
