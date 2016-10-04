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

import com.laundryapp.tubble.MainActivity;
import com.laundryapp.tubble.R;
import com.laundryapp.tubble.Utility;
import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarWeekViewFragment extends Fragment implements View.OnClickListener {
    public static final String CALENDAR_DAY = "calendarDay";
    private final String TAG = this.getClass().getName();
    private static TextView[] daysView;
    private String[] days;
    private long[] daysOfWeek;
    private static int selectedPosition = -1;

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
        updateCalendar(false);
        Log.d(TAG, "CalendarWeekViewFragment onCreateView");
        return view;
    }

    private String[] getDays(long[] daysOfWeek) {
        days = new String[daysOfWeek.length];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        for (int i = 0; i < daysOfWeek.length; i++) {
            calendar.setTimeInMillis(daysOfWeek[i]);
            days[i] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            Log.d("Sarah", "Date: " + dateFormat.format(daysOfWeek[i]));
        }
        return days;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.monday:
                selectedPosition = 0;
                updateCalendar(true);
                break;
            case R.id.tuesday:
                selectedPosition = 1;
                updateCalendar(true);
                break;
            case R.id.wednesday:
                selectedPosition = 2;
                updateCalendar(true);
                break;
            case R.id.thursday:
                selectedPosition = 3;
                updateCalendar(true);
                break;
            case R.id.friday:
                selectedPosition = 4;
                updateCalendar(true);
                break;
            case R.id.saturday:
                selectedPosition = 5;
                updateCalendar(true);
                break;
            case R.id.sunday:
                selectedPosition = 6;
                updateCalendar(true);
                break;
            default:
                break;
        }
    }

    public void updateCalendar(boolean isSelectedFromCalendar) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        try {
            for (int i = 0; i < 7; i++) {
                if ((isSelectedFromCalendar && (selectedPosition == i)) || (!isSelectedFromCalendar && (Integer.parseInt(days[i]) == day))) {
                    daysView[i].setTextColor(ContextCompat.getColor(this.getActivity(), android.R.color.white));
                    daysView[i].setBackground(getResources().getDrawable(R.drawable.calendar_day_selected, null));
                    Log.d("Sarah", "updatecalendar " + daysView[i].getText());
                } else {
                    try {
                        daysView[i].setTextColor(ContextCompat.getColor(this.getActivity(), R.color.calendar_day));
                        daysView[i].setBackground(null);
                    } catch (NullPointerException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            mCalendar.setTimeInMillis(daysOfWeek[4]);
            int year = mCalendar.get(Calendar.YEAR);
            SchedulerFragment.monthTextView.setText(mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + Integer.toString(year));
            if (selectedPosition >= 0 && selectedPosition < 7) {
                updateScheduleList(daysOfWeek[selectedPosition]);
            }
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void updateScheduleList(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        SchedulerFragment.listAdapter.setDate(timeInMillis);
//        List<BookingDetails> bookings = BookingDetails.listAll(BookingDetails.class);
//        List<BookingDetails> bookings = BookingDetails.find(BookingDetails.class, "m_User_Id = ?", Long.toString(Utility.getUserId(getContext())));
        List<BookingDetails> bookings = null;
        String user_id = Long.toString(Utility.getUserId(getContext()));
        if (Utility.getUserType(getContext()) == User.Type.CUSTOMER) {
            bookings = BookingDetails.find(BookingDetails.class, "m_User_Id = ?", user_id);
        } else if (Utility.getUserType(getContext()) == User.Type.LAUNDRY_SHOP) {
            bookings = BookingDetails.find(BookingDetails.class, "m_Laundry_Shop_Id = ?", user_id);
        }
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
