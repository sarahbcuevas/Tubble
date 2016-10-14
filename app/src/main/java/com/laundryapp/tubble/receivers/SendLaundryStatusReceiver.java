package com.laundryapp.tubble.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.laundryapp.tubble.entities.BookingDetails;

public class SendLaundryStatusReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();
    private static BookingDetails details = null;
    private static BookingDetails.Status status = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                if (details != null && status != null) {
                    details.setStatus(status);
                    details.save();
                    Toast.makeText(context, "Laundry status updated.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Laundry status updated.");
                }
                details = null;
                status = null;
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "Error updating laundry status: No service available", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error updating laundry status: No service available");
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Updating laundry status failed. Please try again later.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Updating laundry status failed. Please try again later.");
                break;
            default:
                Toast.makeText(context, "Failed to change laundry request status. Please try again later.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Failed to change laundry request status. Please try again later.");
                break;
        }
    }

    public static void setBookingDetailsWaitingResponse(BookingDetails booking, BookingDetails.Status stat) {
        details = booking;
        status = stat;
    }
}
