package com.laundryapp.tubble;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.laundryapp.tubble.entities.User.Type;

public class Utility {

    private static final String USER_ID = "userId";
    private static final String USER_TYPE = "userType";
    private static final int CUSTOMER = 1;
    private static final int LAUNDRY_SHOP = 2;

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
