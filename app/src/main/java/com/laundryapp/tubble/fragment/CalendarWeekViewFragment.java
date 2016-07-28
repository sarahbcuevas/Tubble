package com.laundryapp.tubble.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laundryapp.tubble.R;
import com.laundryapp.tubble.entities.BookingDetails;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarWeekViewFragment extends Fragment implements View.OnClickListener {
    public static final String CALENDAR_DAY = "calendarDay";
    private final String TAG = this.getClass().getName();
    private static TextView[] daysView;
    private String[] days;
    private long[] daysOfWeek;

    public CalendarWeekViewFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_day_layout, container, false);
        Bundle args = getArguments();
        daysOfWeek = args.getLongArray(CALENDAR_DAY);
        days = getDays(daysOfWeek);
        daysView = new TextView[7];
        daysView[0] = (TextView) view.findViewById(R.id.monday);
        daysView[1] = (TextView) view.findViewById(R.id.tuesday);
        daysView[2] = (TextView) view.findViewById(R.id.wednesday);
        daysView[3] = (TextView) view.findViewById(R.id.thursday);
        daysView[4] = (TextView) view.findViewById(R.id.friday);
        daysView[5] = (TextView) view.findViewById(R.id.saturday);
        daysView[6] = (TextView) view.findViewById(R.id.sunday);
        for (int i = 0; i < 7; i++) {
            daysView[i].setText(days[i]);
            daysView[i].setOnClickListener(this);
        }
        updateCalendar(false, -1);
        Log.d(TAG, "CalendarWeekViewFragment onCreateView");
        return view;
    }

    private String[] getDays(long[] daysOfWeek) {
        days = new String[daysOfWeek.length];
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < daysOfWeek.length; i++) {
            calendar.setTimeInMillis(daysOfWeek[i]);
            days[i] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        }
        return days;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.monday:
                updateCalendar(true, 0);
                break;
            case R.id.tuesday:
                updateCalendar(true, 1);
                break;
            case R.id.wednesday:
                updateCalendar(true, 2);
                break;
            case R.id.thursday:
                updateCalendar(true, 3);
                break;
            case R.id.friday:
                updateCalendar(true, 4);
                break;
            case R.id.saturday:
                updateCalendar(true, 5);
                break;
            case R.id.sunday:
                updateCalendar(true, 6);
                break;
            default:
                break;
        }
    }

    public void updateCalendar(boolean isSelectedFromCalendar, int position) {
        Calendar mCalendar = Calendar.getInstance();
//        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
//        int year = mCalendar.get(Calendar.YEAR);
//        int week = mCalendar.get(Calendar.DAY_OF_WEEK) - 2;
//        String monthString = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + Integer.toString(year);
//        week = week < 0 ? 6 : week;
//        int lastDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//        int[] actualDays = new int[7];
        for (int i = 0; i < 7; i++) {
            if ((isSelectedFromCalendar && (position == i)) || (!isSelectedFromCalendar && (Integer.parseInt(days[i]) == day))) {
                daysView[i].setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                daysView[i].setBackground(getResources().getDrawable(R.drawable.calendar_day_selected, null));
            } else {
                daysView[i].setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_day));
                daysView[i].setBackground(null);
            }
//            actualDays[i] = day + i - week;
//            Log.d("Sarah", "isSelectedFromCalendar: " + isSelectedFromCalendar);
//            Log.d("Sarah", "days[" + position + "]: " + (position >=0 ? days[position]: -1));
//            Log.d("Sarah", "actualDays[" + i + "]: " + actualDays[i]);
//            if ((actualDays[i] == day && !isSelectedFromCalendar) || (isSelectedFromCalendar && Integer.parseInt(days[position]) == actualDays[i])) {
//                daysView[i].setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
//                daysView[i].setBackground(getResources().getDrawable(R.drawable.calendar_day_selected, null));
//            } else {
//                daysView[i].setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_day));
//                daysView[i].setBackground(null);
//            }
//            if (actualDays[i] <= 0) {
//                mCalendar.set(year, month - 1, 1);
//                actualDays[i] += mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//                daysView[i].setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_day_disabled));
//            } else if (actualDays[i] > lastDay) {
//                actualDays[i] -= lastDay;
//                daysView[i].setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_day_disabled));
//            }
        }
//        SchedulerFragment.monthTextView.setText(monthString);
        if (position >= 0 && position < 7) {
            updateScheduleList(daysOfWeek[position]);
        }
    }

    public void updateScheduleList(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        SchedulerFragment.listAdapter.setDate(timeInMillis);
        List<BookingDetails> bookings = BookingDetails.listAll(BookingDetails.class);
        SchedulerFragment.allBookings.clear();
        for (BookingDetails booking: bookings) {
            calendar.setTimeInMillis(booking.getPickupDate());
            int pickupYear = calendar.get(Calendar.YEAR);
            int pickupMonth = calendar.get(Calendar.MONTH);
            int pickupDay = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.setTimeInMillis(booking.getReturnDate());
            int returnYear = calendar.get(Calendar.YEAR);
            int returnMonth = calendar.get(Calendar.MONTH);
            int returnDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (pickupYear == year && pickupMonth == month && pickupDay == day) {
                SchedulerFragment.allBookings.add(booking);
            } else if (returnYear == year && returnMonth == month && returnDay == day) {
                SchedulerFragment.allBookings.add(booking);
            }
        }
        Log.d(TAG, "Number of bookings: " + SchedulerFragment.allBookings.size());
        if (SchedulerFragment.allBookings.isEmpty()) {
            SchedulerFragment.noScheduleText.setVisibility(View.VISIBLE);
            SchedulerFragment.listView.setVisibility(View.GONE);
        } else {
            SchedulerFragment.noScheduleText.setVisibility(View.GONE);
            SchedulerFragment.listView.setVisibility(View.VISIBLE);
            SchedulerFragment.listAdapter = new ScheduleListAdapter(getContext(), SchedulerFragment.allBookings);
            SchedulerFragment.listView.setAdapter(SchedulerFragment.listAdapter);
            SchedulerFragment.listAdapter.notifyDataSetChanged();
        }
    }
}
