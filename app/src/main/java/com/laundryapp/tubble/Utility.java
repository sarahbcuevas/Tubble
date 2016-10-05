package com.laundryapp.tubble;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.User;
import com.laundryapp.tubble.entities.User.Type;
import com.laundryapp.tubble.fragment.SchedulerFragment;
import com.laundryapp.tubble.fragment.StatusFragment;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Utility {

    public static final String DELIMETER = ";";
    private static final String TAG = "Utility";
    private static final String USER_ID = "userId";
    private static final String USER_TYPE = "userType";
    private static final String DELIVERED = "sms_delivered";
    private static String SENT = "sms_sent";
    private static final int CUSTOMER = 1;
    private static final int LAUNDRY_SHOP = 2;

    public static void sendLaundryRequestThruSms(Context context, final BookingDetails details) {
        String message = "";
        String userInfo = "";
        String mode = Integer.toString(details.getMode() == BookingDetails.Mode.PICKUP ? 1 : 2);
        String type = Integer.toString(details.getType() == BookingDetails.Type.COMMERCIAL ? 1 : 2);
        String laundryShopId = Long.toString(details.getLaundryShop().getId());
        String serviceId = Long.toString(details.getLaundryServiceId());
        User user = User.findById(User.class, details.getUserId());
        String userFullname = user.getFullName();
        String userEmail = user.getEmailAddress();
        String userMobile = user.getMobileNumber();
        String location = details.getLocation();
        String notes = details.getNotes();
        String pickupDate = Long.toString(details.getPickupDate());
        String returnDate = Long.toString(details.getReturnDate());
        String noOfClothes = Integer.toString(details.getNoOfClothes());
        String estimatedKilo = Float.toString(details.getEstimatedKilo());
        message = "laundry{" +
                userMobile + DELIMETER +
                mode + DELIMETER +
                type + DELIMETER +
                laundryShopId + DELIMETER +
                serviceId + DELIMETER +
                notes + DELIMETER +
                pickupDate + DELIMETER +
                returnDate + DELIMETER +
                noOfClothes + DELIMETER +
                estimatedKilo + "}";
        Log.d(TAG, "Message before sending: " + message);

        userInfo = "user{" +
                userFullname + DELIMETER +
                userMobile + DELIMETER +
                userEmail + DELIMETER +
                location + "}";

        LaundryShop laundryShop = details.getLaundryShop();
//        String phoneNo = laundryShop.getContact();
        String phoneNo = "09989976459";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            short port = 6734;
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            details.save();
                            SchedulerFragment.updateScheduleListAndCalendar();
                            StatusFragment.updateLaundryList();
                            Log.d(TAG, "Laundry request has been sent.");
                            break;
                        default:
                            Log.d(TAG, "Laundry request was not sent.");
                            break;
                    }
                }
            }, new IntentFilter(SENT));
            smsManager.sendDataMessage(phoneNo, null, port, userInfo.getBytes(), sentIntent, null);
            byte[] b = message.getBytes();
            smsManager.sendDataMessage(phoneNo, null, port, b, sentIntent, null);
            Log.d(TAG, "Size of message: " + b.length);
            Log.d(TAG, "Send laundry request START...");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void logout(Context context) {
        setUserId(context, -1);
        setUserType(context, null);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void setUserId(Context context, long id) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(USER_ID, id);
        editor.commit();
    }

    public static long getUserId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getLong(USER_ID, -1);
    }

    public static void setUserType(Context context, Type type) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        int userType = -1;
        if (Type.CUSTOMER == type) {
            userType = CUSTOMER;
        } else if (Type.LAUNDRY_SHOP == type) {
            userType = LAUNDRY_SHOP;
        }
        editor.putInt(USER_TYPE, userType);
        editor.commit();
    }

    @Nullable
    public static Type getUserType(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int userType = sharedPref.getInt(USER_TYPE, -1);
        if (CUSTOMER == userType) {
            return Type.CUSTOMER;
        } else if (LAUNDRY_SHOP == userType) {
            return Type.LAUNDRY_SHOP;
        }
        return null;
    }

//    private static void sendMultipartDataMessage(final int index, Context context, final String phoneNumber, final short port, final ArrayList<String> messages) {
//        SmsManager smsManager = SmsManager.getDefault();
//        String intentTag = SENT + index;
//        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(intentTag), 0);
//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                int[] i = {index};
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Log.d(TAG, "Part " + (i[0] + 1) + " of " + messages.size() + " has been sent.");
//                        if (i[0] + 1 < messages.size()) {
//                            sendMultipartDataMessage(index + 1, context, phoneNumber, port, messages);
//                        }
//                        break;
//                    default:
//                        Toast.makeText(context, "There is a problem sending laundry request. Please try again later.", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(intentTag));
//        byte[] b = messages.get(index).getBytes();
//        smsManager.sendDataMessage(phoneNumber, null, port, b, sentIntent, null);
//        Log.d(TAG, "Sending part " + (index + 1) + "...");
//        Log.d(TAG, "Message: " + messages.get(index));
//        Log.d(TAG, "Message size: " + messages.get(index).getBytes().length);
//    }

    public static String getTimeDifference(long startDate, long endDate) {
        String differenceStr = "";
        long difference = endDate - startDate;

        if (difference <= 0) {
            return "0 hrs 0 mins";
        }

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = difference / daysInMilli;
        difference = difference % daysInMilli;

        long elapsedHours = difference / hoursInMilli;
        difference = difference % hoursInMilli;

        long elapsedMinutes = difference / minutesInMilli;
        difference = difference % minutesInMilli;

        if (elapsedDays > 0) {
            differenceStr += elapsedDays + " days ";
        }
        differenceStr += elapsedHours + " hrs " + elapsedMinutes + " mins";

        return differenceStr;
    }
}
