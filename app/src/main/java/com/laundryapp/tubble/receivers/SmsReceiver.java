package com.laundryapp.tubble.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.laundryapp.tubble.Utility;
import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.User;
import com.laundryapp.tubble.fragment.LaundryRequestFragment;
import com.laundryapp.tubble.fragment.StatusFragment;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();
    final SmsManager smsManager = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] obj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < obj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) obj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    byte[] data = currentMessage.getUserData();
                    String message = new String(data);
                    Log.d(TAG, "Phone number: " + phoneNumber);
                    Log.d(TAG, "Message:\n" + message);
                    parseMessage(context, message);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void parseMessage(Context context, String message) {
        if (message.startsWith("user")) {
            message = message.substring("user{".length(), message.length() - 1);
            String[] subStr = message.split(Utility.DELIMETER);
            List<User> users = User.find(User.class, "m_Mobile_Number = ?", subStr[1]);
            if (users.isEmpty()) {
                User user = new User(subStr[0], subStr[1], subStr[2], subStr[3], null, null);
                user.save();
            }
        } else if (message.startsWith("laundry")) {
            message = message.substring("laundry{".length(), message.length() - 1);
            String[] subStr = message.split(Utility.DELIMETER);
            User user = User.find(User.class, "m_Mobile_Number = ?", subStr[0]).get(0);
            BookingDetails.Mode mode = (subStr[1].equals("1") ? BookingDetails.Mode.PICKUP : BookingDetails.Mode.DELIVERY);
            BookingDetails.Type type = (subStr[2].equals("1") ? BookingDetails.Type.COMMERCIAL : BookingDetails.Type.PERSONAL);
            BookingDetails booking = new BookingDetails(mode, type, Long.parseLong(subStr[4]), Utility.getUserId(context), user.getId(), user.getAddress(), subStr[5], Long.parseLong(subStr[6]), Long.parseLong(subStr[7]), Integer.parseInt(subStr[8]), Float.parseFloat(subStr[9]));
            booking.save();
            LaundryRequestFragment.updateBookingsList(context);
        } else if (message.startsWith("status")) {
            message = message.substring("status{".length(), message.length() - 1);
            String[] subStr = message.split(Utility.DELIMETER);
            BookingDetails.Status status = null;
            if (subStr[0].equals(BookingDetails.Status.ACCEPTED.name())) {
                status = BookingDetails.Status.ACCEPTED;
            } else if (subStr[0].equals(BookingDetails.Status.REJECTED.name())) {
                status = BookingDetails.Status.REJECTED;
            } else if (subStr[0].equals(BookingDetails.Status.COMPLETED.name())) {
                status = BookingDetails.Status.COMPLETED;
            }

            BookingDetails.Mode mode = (subStr[1].equals("1") ? BookingDetails.Mode.PICKUP : BookingDetails.Mode.DELIVERY);
            BookingDetails.Type type = (subStr[2].equals("1") ? BookingDetails.Type.COMMERCIAL : BookingDetails.Type.PERSONAL);
            String notes = subStr[5];

            Log.d(TAG, "Mode: " + mode);
            Log.d(TAG, "Type: " + type);
            Log.d(TAG, "Laundry shop id: " + subStr[3]);
            Log.d(TAG, "Service id: " + subStr[4]);
            Log.d(TAG, "Notes: " + subStr[5]);
            Log.d(TAG, "Pickup date: " + subStr[6]);
            Log.d(TAG, "Return date: " + subStr[7]);
            Log.d(TAG, "No. of clothes: " + subStr[8]);
            Log.d(TAG, "Estimated kilo: " + subStr[9]);

            List<BookingDetails> booking;
            if (notes.equals("") || notes.equals(" ")) {
                Log.d(TAG, "Notes is empty.");
                booking = BookingDetails.find(BookingDetails.class, "m_Mode = ? and m_Type = ? and m_Laundry_Shop_Id = ? and m_Service_Id = ? " +
                                "and m_Pickup_Date = ? and m_Return_Date = ? and m_No_Of_Clothes = ? and m_Estimated_Kilo = ?", mode.toString(), type.toString(),
                        subStr[3], subStr[4], subStr[6], subStr[7], subStr[8], subStr[9]);
            } else {
                Log.d(TAG, "Notes exists.");
                booking = BookingDetails.find(BookingDetails.class, "m_Mode = ? and m_Type = ? and m_Laundry_Shop_Id = ? and m_Service_Id = ? " +
                                "and m_Notes = ? and m_Pickup_Date = ? and m_Return_Date = ? and m_No_Of_Clothes = ? and m_Estimated_Kilo = ?", mode.toString(), type.toString(),
                        subStr[3], subStr[4], subStr[5], subStr[6], subStr[7], subStr[8], subStr[9]);
            }

            Log.d(TAG, "booking found: " + booking.size());
            if (!booking.isEmpty()) {
                booking.get(0).setStatus(status);
                booking.get(0).save();
                StatusFragment.updateLaundryList();
            }
        }
    }
}