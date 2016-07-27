package com.laundryapp.tubble.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public static final String TIME_PICKER = "timePicker";
    OnTimeSetListener listener;
    String tag;

    public TimePickerFragment() {
    }

    public void setOnTimeSetListener(OnTimeSetListener listener, String tag) {
        this.listener = listener;
        this.tag = tag;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        listener.onTimeSet(tag, hourOfDay, minute);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, false);
    }

    public interface OnTimeSetListener {
        void onTimeSet(String tag, int hourOfDay, int minute);
    }

    static public class Time {
        int hour;
        int minute;

        public void setTime(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }
    }
}