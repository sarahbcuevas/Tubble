package com.laundryapp.tubble.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laundryapp.tubble.R;

import java.lang.reflect.Field;
import java.util.Calendar;

public class CalendarWeekViewFragment extends Fragment implements View.OnClickListener {
    private final String TAG = this.getClass().getName();
    private TextView[] daysView;
    private static int selectedPosition = -1, currentDatePosition = -1;
    private int mPosition;
    private long[] daysOfWeek;
    private String[] daysNumber;
    private View fragmentView;

    public CalendarWeekViewFragment() {

    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public static int getSelectedDayPosition() {
        return selectedPosition;
    }

    public static int getCurrentDatePosition() { return currentDatePosition; }

    public static void setSelectedDayPosition(int position) {
        selectedPosition = position;
    }

    public long[] getDaysOfWeek(int position) {
        long[] days = new long[7];
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.DATE, (position - 5_000) * 7);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, -1);
        }
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        for (int i = 0; i < 7; i++) {
            days[i] = calendar.getTimeInMillis();
            calendar.add(Calendar.DATE, 1);
        }
        return days;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.calendar_day_layout, container, false);
        updateCalendarContent();
        return fragmentView;
    }

    private String[] getDayNumbers(long[] daysOfWeek) {
        String[] days = new String[daysOfWeek.length];
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
                selectedPosition = 0;
                updateCalendarSelectedDay();
                break;
            case R.id.tuesday:
                selectedPosition = 1;
                updateCalendarSelectedDay();
                break;
            case R.id.wednesday:
                selectedPosition = 2;
                updateCalendarSelectedDay();
                break;
            case R.id.thursday:
                selectedPosition = 3;
                updateCalendarSelectedDay();
                break;
            case R.id.friday:
                selectedPosition = 4;
                updateCalendarSelectedDay();
                break;
            case R.id.saturday:
                selectedPosition = 5;
                updateCalendarSelectedDay();
                break;
            case R.id.sunday:
                selectedPosition = 6;
                updateCalendarSelectedDay();
                break;
            default:
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser  && fragmentView != null) {
            updateCalendarContent();
        }
    }

    public void updateCalendarContent() {
        try {
            daysOfWeek = getDaysOfWeek(mPosition);
            daysNumber = getDayNumbers(daysOfWeek);
            daysView = new TextView[7];
            daysView[0] = (TextView) fragmentView.findViewById(R.id.monday);
            daysView[1] = (TextView) fragmentView.findViewById(R.id.tuesday);
            daysView[2] = (TextView) fragmentView.findViewById(R.id.wednesday);
            daysView[3] = (TextView) fragmentView.findViewById(R.id.thursday);
            daysView[4] = (TextView) fragmentView.findViewById(R.id.friday);
            daysView[5] = (TextView) fragmentView.findViewById(R.id.saturday);
            daysView[6] = (TextView) fragmentView.findViewById(R.id.sunday);
            for (int i = 0; i < 7; i++) {
                daysView[i].setText(daysNumber[i]);
                daysView[i].setOnClickListener(this);
            }
            if (selectedPosition == -1) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                for (int i = 0; i < 7; i++) {
                    if(Integer.parseInt(daysNumber[i]) == day) {
                        currentDatePosition = i;
                        selectedPosition = i;
                    }
                }
            }
            updateCalendarSelectedDay();
        } catch (NullPointerException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    public void updateCalendarSelectedDay() {
        try {
            for (int i = 0; i < 7; i++) {
                if (selectedPosition == i) {
                    daysView[i].setTextColor(ContextCompat.getColor(this.getActivity(), android.R.color.white));
                    daysView[i].setBackground(getResources().getDrawable(R.drawable.calendar_day_selected, null));
                } else {
                    try {
                        daysView[i].setTextColor(ContextCompat.getColor(this.getActivity(), R.color.calendar_day));
                        daysView[i].setBackground(null);
                    } catch (NullPointerException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
            if (0 <= selectedPosition && selectedPosition < 7) {
                SchedulerFragment.updateScheduleList(daysOfWeek[selectedPosition]);
            }
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException ex1) {
            Log.e(TAG, ex1.getMessage(), ex1);
        } catch (IllegalAccessException ex2) {
            Log.e(TAG, ex2.getMessage(), ex2);
        }
    }
}
