package com.laundryapp.tubble;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.User;
import com.laundryapp.tubble.entities.User.Type;
import com.laundryapp.tubble.receivers.EditLaundryDetailsReceiver;
import com.laundryapp.tubble.receivers.SendLaundryRequestReceiver;
import com.laundryapp.tubble.receivers.SendLaundryStatusReceiver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Utility {

    public static final String DELIMETER = ";";
    private static final String TAG = "Utility";
    private static final String USER_ID = "userId";
    private static final String USER_TYPE = "userType";
    private static final String DELIVERED = "sms_delivered";
    private static String SENT = "com.laundryapp.tubble.SMS_SENT";
    private static String STATUS_SENT = "com.laundryapp.tubble.STATUS_SENT";
    private static String EDIT_DETAILS_SENT = "com.laundryapp.tubble.EDIT_DETAILS_SENT";
    private static final int CUSTOMER = 1;
    private static final int LAUNDRY_SHOP = 2;
    private static final short PORT = 6734;
    public final static int CAPTURE_IMAGE_RESULT = 2;

    public static String takePhotoUsingCamera(Activity activity) {
        String imageDecode = null;
        if (activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile(activity);
                    imageDecode = photoFile.getPath();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(activity, "com.example.android.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    activity.startActivityForResult(takePictureIntent, CAPTURE_IMAGE_RESULT);
                }
            }
        } else {
            Toast.makeText(activity, "No camera detected.", Toast.LENGTH_SHORT).show();
        }

        return imageDecode;
    }

    public static File createImageFile(Activity activity) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    public static void scaleImage(CircleImageView mUserPhoto, String imageDecode) {
        int targetW = mUserPhoto.getWidth();
        int targetH = mUserPhoto.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageDecode, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imageDecode, bmOptions);
        mUserPhoto.setImageBitmap(bitmap);
        mUserPhoto.setBorderWidth(20);
    }

    public static void savePicToGallery(Activity activity, String imageDecode) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imageDecode);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    public static void sendLaundryStatusThruSms(Context context, final BookingDetails details, final BookingDetails.Status laundryStatus) {
        String status = laundryStatus.name();
        String mode = Integer.toString(details.getMode() == BookingDetails.Mode.PICKUP ? 1 : 2);
        String type = Integer.toString(details.getType() == BookingDetails.Type.COMMERCIAL ? 1 : 2);
        String laundryShopId = Long.toString(Utility.getUserId(context));
        String serviceId = Long.toString(details.getLaundryServiceId());
        String notes = details.getNotes().equals("") ? " " : details.getNotes();
        String pickupDate = Long.toString(details.getPickupDate());
        String returnDate = Long.toString(details.getReturnDate());
        String noOfClothes = Integer.toString(details.getNoOfClothes());
        String estimatedKilo = Float.toString(details.getEstimatedKilo());
        String fee = Float.toString(details.getFee());
        String message = "status{" +
                status + DELIMETER +
                mode + DELIMETER +
                type + DELIMETER +
                laundryShopId + DELIMETER +
                serviceId + DELIMETER +
                notes + DELIMETER +
                pickupDate + DELIMETER +
                returnDate + DELIMETER +
                noOfClothes + DELIMETER +
                estimatedKilo + DELIMETER +
                fee + "}";

        Log.d(TAG, "Message: " + message);
        User user = User.findById(User.class, details.getUserId());
        String phoneNo = user.getMobileNumber();
//        String phoneNo = "09989976459"; // personal number
//        String phoneNo = "09391157355";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(STATUS_SENT), 0);
            SendLaundryStatusReceiver.setBookingDetailsWaitingResponse(details, laundryStatus);
            smsManager.sendDataMessage(phoneNo, null, PORT, message.getBytes(), sentIntent, null);
            Log.d(TAG, "Sending laundry status update...");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void sendEditDetailsThruSms(Context context, final BookingDetails details, float tempFee, int tempNoOfClothes, float tempEstimatedKilo) {
        // To do: Implement sending of edited laundry schedule details
        String newFee = Float.toString(tempFee);
        String newNoOfClothes = Integer.toString(tempNoOfClothes);
        String newEstimatedKilo = Float.toString(tempEstimatedKilo);

        String mode = Integer.toString(details.getMode() == BookingDetails.Mode.PICKUP ? 1 : 2);
        String type = Integer.toString(details.getType() == BookingDetails.Type.COMMERCIAL ? 1 : 2);
        String laundryShopId = Long.toString(Utility.getUserId(context));
        String serviceId = Long.toString(details.getLaundryServiceId());
        String notes = details.getNotes().equals("") ? " " : details.getNotes();
        String pickupDate = Long.toString(details.getPickupDate());
        String returnDate = Long.toString(details.getReturnDate());
        String noOfClothes = Integer.toString(details.getNoOfClothes());
        String estimatedKilo = Float.toString(details.getEstimatedKilo());
        String fee = Float.toString(details.getFee());
        String message = "edit{" +
                newFee + DELIMETER +
                newNoOfClothes + DELIMETER +
                newEstimatedKilo + DELIMETER +
                mode + DELIMETER +
                type + DELIMETER +
                laundryShopId + DELIMETER +
                serviceId + DELIMETER +
                notes + DELIMETER +
                pickupDate + DELIMETER +
                returnDate + DELIMETER +
                noOfClothes + DELIMETER +
                estimatedKilo + DELIMETER +
                fee + "}";

        Log.d(TAG, "Message: " + message);
        User user = User.findById(User.class, details.getUserId());
        String phoneNo = user.getMobileNumber();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(EDIT_DETAILS_SENT), 0);
            EditLaundryDetailsReceiver.setBookingDetailsWaitingResponse(details, tempFee, tempNoOfClothes, tempEstimatedKilo);
            smsManager.sendDataMessage(phoneNo, null, PORT, message.getBytes(), sentIntent, null);
            Log.d(TAG, "Sending laundry details update...");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

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
        String estimatedFee = Float.toString(details.getFee());
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
                estimatedKilo + DELIMETER +
                estimatedFee + "}";
        Log.d(TAG, "Message before sending: " + message);

        userInfo = "user{" +
                userFullname + DELIMETER +
                userMobile + DELIMETER +
                userEmail + DELIMETER +
                location + "}";

        LaundryShop laundryShop = details.getLaundryShop();
//        String phoneNo = laundryShop.getContact();
//        String phoneNo = "09989976459";   // personal number
//        String phoneNo = "09063931566";   // lyssa
        String phoneNo = "09391157355";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
            SendLaundryRequestReceiver.setBookingDetailsWaitingResponse(details);
            smsManager.sendDataMessage(phoneNo, null, PORT, userInfo.getBytes(), sentIntent, null);
            byte[] b = message.getBytes();
            smsManager.sendDataMessage(phoneNo, null, PORT, b, sentIntent, null);
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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    public static String getTimeDifference(Context context, long startDate, long endDate) {
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

        Resources res = context.getResources();

        if (elapsedDays > 0) {
            differenceStr += elapsedDays + " " + res.getQuantityString(R.plurals.plural_day, (int) elapsedDays) + " ";
        }
        differenceStr += elapsedHours + " " + res.getQuantityString(R.plurals.plural_hour, (int) elapsedHours) + " " +
                elapsedMinutes + " " + res.getQuantityString(R.plurals.plural_minute, (int) elapsedMinutes);

        return differenceStr;
    }
}
