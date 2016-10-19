package com.laundryapp.tubble.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.fragment.StatusFragment;

public class EditLaundryDetailsReceiver extends BroadcastReceiver{

    private final String TAG = this.getClass().getName();
    private static BookingDetails details = null;
    private static float mFee = -1, mEstimatedKilo = -1;
    private static int mNoOfClothes = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                if (details != null && mFee != -1 && mEstimatedKilo != -1 && mNoOfClothes != -1) {
                    details.setFee(mFee);
                    details.setEstimatedKilo(mEstimatedKilo);
                    details.setNoOfClothes(mNoOfClothes);
                    details.save();
                    StatusFragment.updateLaundryScheduleDetailsLayout(details.getId());
                    Toast.makeText(context, "Laundry details updated.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Laundry details updated.");
                }
                details = null;
                mFee = -1;
                mEstimatedKilo = -1;
                mNoOfClothes = -1;
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "Error updating laundry details: No service available", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error updating laundry details: No service available");
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Updating laundry details failed. Please try again later.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Updating laundry details failed. Please try again later.");
                break;
            default:
                Toast.makeText(context, "Failed to change laundry request details. Please try again later.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Failed to change laundry request details. Please try again later.");
                break;
        }
    }

    public static void setBookingDetailsWaitingResponse(BookingDetails booking, float fee, int noOfClothes, float estimatedKilo) {
        details = booking;
        mFee = fee;
        mEstimatedKilo = estimatedKilo;
        mNoOfClothes = noOfClothes;
    }
}
