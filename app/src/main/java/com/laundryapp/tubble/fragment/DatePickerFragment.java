package com.laundryapp.tubble.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static final String DATE_PICKER = "datePicker";
    OnDateSetListener listener;
    String tag;

    public DatePickerFragment() {
    }

    public void setOnDateSetListener(OnDateSetListener listener, String tag) {
        this.listener = listener;
        this.tag = tag;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
        listener.onDateSet(tag, year, month, date);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public interface OnDateSetListener {
        void onDateSet(String tag, int year, int month, int date);
    }

    static public class Date {
        int year;
        int month;
        int date;

        public void setDate(int year, int month, int date) {
            this.year = year;
            this.month = month;
            this.date = date;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDate() {
            return date;
        }
    }
}
