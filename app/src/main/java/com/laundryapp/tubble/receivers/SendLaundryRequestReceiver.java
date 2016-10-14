package com.laundryapp.tubble.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.fragment.SchedulerFragment;
import com.laundryapp.tubble.fragment.StatusFragment;

public class SendLaundryRequestReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();
    private static BookingDetails details = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                if (details != null) {
                    details.save();
                    SchedulerFragment.updateScheduleListAndCalendar();
                    StatusFragment.updateLaundryList();
                    Toast.makeText(context, "Laundry request has been sent.", Toast.LENGTH_SHORT).show();
                }
                details = null;
                Log.d(TAG, "Laundry request has been sent.");
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "Error sending laundry request: No service available", Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Sending laundry request failed. Please try again later.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.d(TAG, "Laundry request was not sent.");
                break;
        }
    }

    public static void setBookingDetailsWaitingResponse(BookingDetails booking) {
        details = booking;
    }
}
