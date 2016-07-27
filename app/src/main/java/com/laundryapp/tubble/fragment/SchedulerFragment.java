package com.laundryapp.tubble.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.laundryapp.tubble.R;
import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.LaundryService;
import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.LaundryShopService;
import com.laundryapp.tubble.entities.User;
import com.laundryapp.tubble.fragment.DatePickerFragment.Date;
import com.laundryapp.tubble.fragment.TimePickerFragment.Time;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SchedulerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SchedulerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SchedulerFragment extends Fragment implements View.OnClickListener, TimePickerFragment.OnTimeSetListener, DatePickerFragment.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "SchedulerFragment";
    private static final String PICKUP_TIME_TAG = "pickupTimePicker";
    private static final String PICKUP_DATE_TAG = "pickupDatePicker";
    private static final String RETURN_TIME_TAG = "returnTimePicker";
    private static final String RETURN_DATE_TAG = "returnDatePicker";
    private Calendar mCalendar;
    private View fragmentView;
    protected static TextView monthTextView;
    protected static TextView noScheduleText;
    private FrameLayout leftButton, rightButton;
    private LinearLayout modeLayout, typeLayout, locationLayout, shopLayout, serviceLayout, summaryLayout;
    private Switch modeToggle, typeToggle;
    private LinearLayout modeToggleLayout, typeToggleLayout, locationToggleLayout, summaryToggleLayout, confirmButton;
    private TextView modeText, typeText, locationText, shopText, serviceText, summaryText;
    //    private TextView[] days;
    protected static ListView listView;
    protected static List<BookingDetails> allBookings;
    protected static ScheduleListAdapter listAdapter;
    private ImageButton bookButton;
    private LinearLayout schedulerLayout, bookingLayout;
    private CheckBox locationCheckbox;
    private EditText locationEdittext, notesEdittext, noOfClothesEdittext, estimatedKiloEdittext;
    private Button pickupDateButton, pickupTimeButton, returnDateButton, returnTimeButton;
    private Dialog shopDialog, serviceDialog, messageDialog;
    private long laundryShop_id = -1, laundryShopService_id = -1;
    List<LaundryShop> shops;
    List<LaundryShopService> services = new ArrayList<LaundryShopService>();
    private Date pickupDate, returnDate;
    private Time pickupTime, returnTime;
    private ArrayAdapter<String> serviceAdapter, shopAdapter;
    private static CalendarWeekViewAdapter calendarAdapter;
    static ViewPager calendarPager;
    private static FragmentManager fm;

    private OnFragmentInteractionListener mListener;

    public SchedulerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SchedulerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SchedulerFragment newInstance(String param1, String param2) {
        SchedulerFragment fragment = new SchedulerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_scheduler, container, false);

        schedulerLayout = (LinearLayout) fragmentView.findViewById(R.id.scheduler);
        schedulerLayout.setVisibility(View.VISIBLE);
        bookingLayout = (LinearLayout) fragmentView.findViewById(R.id.booking);
        bookingLayout.setVisibility(View.GONE);
        modeLayout = (LinearLayout) fragmentView.findViewById(R.id.mode);
        typeLayout = (LinearLayout) fragmentView.findViewById(R.id.type);
        locationLayout = (LinearLayout) fragmentView.findViewById(R.id.location);
        shopLayout = (LinearLayout) fragmentView.findViewById(R.id.shop);
        serviceLayout = (LinearLayout) fragmentView.findViewById(R.id.service);
        summaryLayout = (LinearLayout) fragmentView.findViewById(R.id.summary);
        modeText = (TextView) fragmentView.findViewById(R.id.mode_text);
        typeText = (TextView) fragmentView.findViewById(R.id.type_text);
        locationText = (TextView) fragmentView.findViewById(R.id.location_text);
        shopText = (TextView) fragmentView.findViewById(R.id.shop_text);
        serviceText = (TextView) fragmentView.findViewById(R.id.service_text);
        summaryText = (TextView) fragmentView.findViewById(R.id.summary_text);
        modeToggle = (Switch) fragmentView.findViewById(R.id.mode_toggle);
        typeToggle = (Switch) fragmentView.findViewById(R.id.type_toggle);
        modeToggleLayout = (LinearLayout) fragmentView.findViewById(R.id.mode_toggle_layout);
        typeToggleLayout = (LinearLayout) fragmentView.findViewById(R.id.type_toggle_layout);
        locationToggleLayout = (LinearLayout) fragmentView.findViewById(R.id.location_toggle_layout);
        summaryToggleLayout = (LinearLayout) fragmentView.findViewById(R.id.summary_toggle_layout);
        confirmButton = (LinearLayout) fragmentView.findViewById(R.id.confirm_button);
        monthTextView = (TextView) fragmentView.findViewById(R.id.month_text);
        noScheduleText = (TextView) fragmentView.findViewById(R.id.no_schedule_text);
        leftButton = (FrameLayout) fragmentView.findViewById(R.id.left_button);
        rightButton = (FrameLayout) fragmentView.findViewById(R.id.right_button);
        locationCheckbox = (CheckBox) fragmentView.findViewById(R.id.location_checkbox);
        locationEdittext = (EditText) fragmentView.findViewById(R.id.location_edittext);
        notesEdittext = (EditText) fragmentView.findViewById(R.id.notes);
        noOfClothesEdittext = (EditText) fragmentView.findViewById(R.id.no_of_clothes);
        estimatedKiloEdittext = (EditText) fragmentView.findViewById(R.id.estimated_kilo);
        pickupDateButton = (Button) fragmentView.findViewById(R.id.pickup_date_button);
        pickupTimeButton = (Button) fragmentView.findViewById(R.id.pickup_time_button);
        returnDateButton = (Button) fragmentView.findViewById(R.id.return_date_button);
        returnTimeButton = (Button) fragmentView.findViewById(R.id.return_time_button);
//        days = new TextView[7];
//        days[0] = (TextView) fragmentView.findViewById(R.id.monday);
//        days[1] = (TextView) fragmentView.findViewById(R.id.tuesday);
//        days[2] = (TextView) fragmentView.findViewById(R.id.wednesday);
//        days[3] = (TextView) fragmentView.findViewById(R.id.thursday);
//        days[4] = (TextView) fragmentView.findViewById(R.id.friday);
//        days[5] = (TextView) fragmentView.findViewById(R.id.saturday);
//        days[6] = (TextView) fragmentView.findViewById(R.id.sunday);
        listView = (ListView) fragmentView.findViewById(R.id.schedule_list);
        allBookings = new ArrayList<BookingDetails>();
        listAdapter = new ScheduleListAdapter(getContext(), allBookings);
        listView.setAdapter(listAdapter);
        bookButton = (ImageButton) fragmentView.findViewById(R.id.book_button);
        fm = getFragmentManager();
        calendarAdapter = new CalendarWeekViewAdapter(fm);
        calendarPager = (ViewPager) fragmentView.findViewById(R.id.calendar_pager);
        calendarPager.setAdapter(calendarAdapter);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        modeLayout.setOnClickListener(this);
        typeLayout.setOnClickListener(this);
        locationLayout.setOnClickListener(this);
        shopLayout.setOnClickListener(this);
        serviceLayout.setOnClickListener(this);
        summaryLayout.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        pickupDateButton.setOnClickListener(this);
        pickupTimeButton.setOnClickListener(this);
        returnDateButton.setOnClickListener(this);
        returnTimeButton.setOnClickListener(this);

        updateDateTimeString();

//        for (TextView day : days) {
//            day.setOnClickListener(this);
//        }
        bookButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    schedulerLayout.setVisibility(View.GONE);
                    bookingLayout.setVisibility(View.VISIBLE);
                    updateDateTimeString();
                    updateBookingSelectedItem(R.id.mode);
                    return true;
                }
                return false;
            }
        });

        locationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String str = "";
                if (isChecked) {
                    User user = User.listAll(User.class).get(0);
                    str = user.getAddress();
                }
                locationEdittext.setText(str);
            }
        });

        mCalendar = Calendar.getInstance();
        mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
//        updateCalendar(false, -1);
        updateCalendarAdapter();
        updateLaundryShops();
        updateMessageDialog(R.string.select_laundry_shop);
        return fragmentView;
    }

    public static void updateCalendarAdapter() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        int year = calendar.get(Calendar.YEAR);
        String monthString = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + Integer.toString(year);

        long[] days = new long[7];
        for (int i = 0; i < 7; i++) {
            days[i] = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendarAdapter = new CalendarWeekViewAdapter(fm);
        calendarAdapter.setDaysOfWeek(days);
        calendarPager.setAdapter(calendarAdapter);
        monthTextView.setText(monthString);
        calendarAdapter.notifyDataSetChanged();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public boolean onBackPressed() {
        if (bookingLayout.getVisibility() == View.VISIBLE) {
            bookingLayout.setVisibility(View.GONE);
            schedulerLayout.setVisibility(View.VISIBLE);
            updateCalendarAdapter();
            reset();
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCalendarAdapter();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    private void updateCalendar(boolean isSelectedFromCalendar, int selectedDay) {
//        int month = mCalendar.get(Calendar.MONTH);
//        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
//        int year = mCalendar.get(Calendar.YEAR);
//        int week = mCalendar.get(Calendar.DAY_OF_WEEK) - 2;
//        String monthString = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + Integer.toString(year);
//        week = week < 0 ? 6 : week;
//        int lastDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//        int[] actualDays = new int[7];
//        for (int i = 0; i < 7; i++) {
//            actualDays[i] = day + i - week;
//            if ((actualDays[i] == day && !isSelectedFromCalendar) || (isSelectedFromCalendar && selectedDay == actualDays[i])) {
//                days[i].setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
//                days[i].setBackground(getResources().getDrawable(R.drawable.calendar_day_selected, null));
//            } else {
//                days[i].setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_day));
//                days[i].setBackground(null);
//            }
//            if (actualDays[i] <= 0) {
//                mCalendar.set(year, month - 1, 1);
//                actualDays[i] += mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//                days[i].setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_day_disabled));
//            } else if (actualDays[i] > lastDay) {
//                actualDays[i] -= lastDay;
//                days[i].setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_day_disabled));
//            }
//
//            days[i].setText(Integer.toString(actualDays[i]));
//        }
//
//        monthTextView.setText(monthString);
//
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_button:
                break;
            case R.id.right_button:
                break;
//            case R.id.monday:
//                updateCalendar(true, Integer.parseInt(days[0].getText().toString()));
//                break;
//            case R.id.tuesday:
//                updateCalendar(true, Integer.parseInt(days[1].getText().toString()));
//                break;
//            case R.id.wednesday:
//                updateCalendar(true, Integer.parseInt(days[2].getText().toString()));
//                break;
//            case R.id.thursday:
//                updateCalendar(true, Integer.parseInt(days[3].getText().toString()));
//                break;
//            case R.id.friday:
//                updateCalendar(true, Integer.parseInt(days[4].getText().toString()));
//                break;
//            case R.id.saturday:
//                updateCalendar(true, Integer.parseInt(days[5].getText().toString()));
//                break;
//            case R.id.sunday:
//                updateCalendar(true, Integer.parseInt(days[6].getText().toString()));
//                break;
            case R.id.book_button:
                break;
            case R.id.mode:
                updateBookingSelectedItem(R.id.mode);
                break;
            case R.id.type:
                updateBookingSelectedItem(R.id.type);
                break;
            case R.id.location:
                updateBookingSelectedItem(R.id.location);
                break;
            case R.id.shop:
                updateBookingSelectedItem(R.id.shop);
                shopDialog.show();
                break;
            case R.id.service:
                updateBookingSelectedItem(R.id.service);
                if (laundryShop_id == -1) {
                    messageDialog.show();
                } else {
                    serviceDialog.show();
                }
                break;
            case R.id.summary:
                updateBookingSelectedItem(R.id.summary);
                break;
            case R.id.pickup_date_button:
                DialogFragment pickupDatePicker = new DatePickerFragment();
                ((DatePickerFragment) pickupDatePicker).setOnDateSetListener(this, PICKUP_DATE_TAG);
                pickupDatePicker.show(getFragmentManager(), DatePickerFragment.DATE_PICKER);
                break;
            case R.id.pickup_time_button:
                DialogFragment pickupTimePicker = new TimePickerFragment();
                ((TimePickerFragment) pickupTimePicker).setOnTimeSetListener(this, PICKUP_TIME_TAG);
                pickupTimePicker.show(getFragmentManager(), TimePickerFragment.TIME_PICKER);
                break;
            case R.id.return_date_button:
                DialogFragment returnDaatePicker = new DatePickerFragment();
                ((DatePickerFragment) returnDaatePicker).setOnDateSetListener(this, RETURN_DATE_TAG);
                returnDaatePicker.show(getFragmentManager(), DatePickerFragment.DATE_PICKER);
                break;
            case R.id.return_time_button:
                DialogFragment returnTimePicker = new TimePickerFragment();
                ((TimePickerFragment) returnTimePicker).setOnTimeSetListener(this, RETURN_TIME_TAG);
                returnTimePicker.show(getFragmentManager(), TimePickerFragment.TIME_PICKER);
                break;
            case R.id.confirm_button:
                createBooking();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(String tag, int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MM/dd/yyyy");
        String str = dateFormat.format(calendar.getTime());
        switch (tag) {
            case PICKUP_DATE_TAG:
                pickupDateButton.setText(str);
                pickupDate.year = year;
                pickupDate.month = month;
                pickupDate.date = date;
//                pickupDate = calendar.getTimeInMillis();
                break;
            case RETURN_DATE_TAG:
                returnDateButton.setText(str);
                returnDate.year = year;
                returnDate.month = month;
                returnDate.date = date;
//                returnDate = calendar.getTimeInMillis();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTimeSet(String tag, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String str = format.format(calendar.getTime());
        switch (tag) {
            case PICKUP_TIME_TAG:
                pickupTimeButton.setText(str);
                pickupTime.hour = hourOfDay;
                pickupTime.minute = minute;
                break;
            case RETURN_TIME_TAG:
                returnTimeButton.setText(str);
                returnTime.hour = hourOfDay;
                returnTime.minute = minute;
                break;
            default:
                break;
        }
    }

    public void updateBookingSelectedItem(int resId) {
        int selectedBackground = ContextCompat.getColor(getContext(), R.color.booking_field_background_blue);
        int deselectedBackground = ContextCompat.getColor(getContext(), R.color.booking_field_background_gray);
        int selectedText = ContextCompat.getColor(getContext(), R.color.booking_field_text_selected);
        int deselectedText = ContextCompat.getColor(getContext(), R.color.booking_field_text_deselected);
        modeLayout.setBackgroundColor(resId == R.id.mode ? selectedBackground : deselectedBackground);
        modeText.setTextColor(resId == R.id.mode ? selectedText : deselectedText);
        typeLayout.setBackgroundColor(resId == R.id.type ? selectedBackground : deselectedBackground);
        typeText.setTextColor(resId == R.id.type ? selectedText : deselectedText);
        locationLayout.setBackgroundColor(resId == R.id.location ? selectedBackground : deselectedBackground);
        locationText.setTextColor(resId == R.id.location ? selectedText : deselectedText);
        shopLayout.setBackgroundColor(resId == R.id.shop ? selectedBackground : deselectedBackground);
        shopText.setTextColor(resId == R.id.shop ? selectedText : deselectedText);
        serviceLayout.setBackgroundColor(resId == R.id.service ? selectedBackground : deselectedBackground);
        serviceText.setTextColor(resId == R.id.service ? selectedText : deselectedText);
        summaryLayout.setBackgroundColor(resId == R.id.summary ? selectedBackground : deselectedBackground);
        summaryText.setTextColor(resId == R.id.summary ? selectedText : deselectedText);

        modeToggleLayout.setVisibility(resId == R.id.mode ? View.VISIBLE : View.GONE);
        typeToggleLayout.setVisibility(resId == R.id.type ? View.VISIBLE : View.GONE);
        locationToggleLayout.setVisibility(resId == R.id.location ? View.VISIBLE : View.GONE);
        summaryToggleLayout.setVisibility(resId == R.id.summary ? View.VISIBLE : View.GONE);
    }

    public void updateLaundryShops() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.laundry_shop);
        shops = LaundryShop.listAll(LaundryShop.class);
        ArrayList<String> shopsName = new ArrayList<String>();
        Log.d(TAG, "Number of shops: " + shops.size());
        for (LaundryShop shop : shops) {
            shopsName.add(shop.getName());
            Log.d(TAG, "shop = " + shop.getName());
        }
        shopAdapter = new ListViewArrayAdapter(getContext(), shopsName, ListViewArrayAdapter.LAUNDRY_SHOP);
        shopAdapter.notifyDataSetChanged();
        builder.setSingleChoiceItems(shopAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (shopDialog != null) {
                    shopDialog.dismiss();
                }
            }
        });

        shopDialog = builder.create();
    }

    public void updateLaundryServices() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.laundry_services);
        List<LaundryShopService> allServices = LaundryShopService.listAll(LaundryShopService.class);
        ArrayList<String> servicesName = new ArrayList<String>();
        services.clear();
        for (LaundryShopService lss : allServices) {
            Log.d(TAG, "All services: " + lss.getLaundryServiceId() + " " + lss.getLaundryShopId() + " " + lss.getPrice());
            if (lss.getLaundryShopId() == laundryShop_id) {
                services.add(lss);
                LaundryService serv = LaundryService.findById(LaundryService.class, lss.getLaundryServiceId());
                servicesName.add(serv.getLabel());
            }
        }
        serviceAdapter = new ListViewArrayAdapter(getContext(), servicesName, ListViewArrayAdapter.LAUNDRY_SERVICE);
        serviceAdapter.notifyDataSetChanged();
        builder.setSingleChoiceItems(serviceAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (serviceDialog != null) {
                    serviceDialog.dismiss();
                }
            }
        });

        serviceDialog = builder.create();
    }

    public void updateMessageDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.note);
        builder.setMessage(str);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (messageDialog != null) {
                    messageDialog.dismiss();
                }
            }
        });

        messageDialog = builder.create();
    }

    public void updateMessageDialog(int stringId) {
        updateMessageDialog(getString(stringId));
    }

    public void createBooking() {
        final BookingDetails.Mode mMode = modeToggle.isChecked() ? BookingDetails.Mode.DELIVERY : BookingDetails.Mode.PICKUP;
        final BookingDetails.Type mType = typeToggle.isChecked() ? BookingDetails.Type.PERSONAL : BookingDetails.Type.COMMERCIAL;
        final long mUserId = User.listAll(User.class).get(0).getId();
        final String mLocation = locationEdittext.getText().toString();
        final String mNotes = notesEdittext.getText().toString();
        final String mNoOfClothes = noOfClothesEdittext.getText().toString();
        final String mEstimatedKilo = estimatedKiloEdittext.getText().toString();

        if (mLocation.equals("")) {
            updateMessageDialog(R.string.input_location);
            messageDialog.show();
        } else if (laundryShop_id == -1) {
            updateMessageDialog(R.string.select_laundry_shop);
            messageDialog.show();
        } else if (laundryShopService_id == -1) {
            updateMessageDialog(R.string.select_laundry_service);
            messageDialog.show();
        } else if (mNoOfClothes.equals("")) {
            updateMessageDialog(R.string.input_no_of_clothes);
            messageDialog.show();
        } else if (mEstimatedKilo.equals("")) {
            updateMessageDialog(R.string.input_estimated_kilo);
            messageDialog.show();
        } else {
            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            createdDate = new Date();
//            createdDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//            createdTime = new Time();
//            createdTime.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            calendar.set(pickupDate.year, pickupDate.month, pickupDate.date, pickupTime.hour, pickupTime.minute);
            final long pickupMillis = calendar.getTimeInMillis();
//            calendar.setTimeInMillis(pickupDate);
            String pick = format.format(calendar.getTime());
            calendar.set(returnDate.year, returnDate.month, returnDate.date, returnTime.hour, returnTime.minute);
//            calendar.setTimeInMillis(returnDate);
            final long returnMillis = calendar.getTimeInMillis();
            String ret = format.format(calendar.getTime());
//            calendar.set(createdDate.year, createdDate.month, createdDate.date, createdTime.hour, createdTime.minute);
//            String cre = format.format(calendar.getTime());
            float estimatedFee = LaundryShopService.findById(LaundryShopService.class, laundryShopService_id).getPrice() * Float.parseFloat(mEstimatedKilo);

            String message = "Please confirm details below:\n\n" +
                    "Mode: " + (mMode == BookingDetails.Mode.DELIVERY ? "Delivery" : "Pick-Up") + "\n" +
                    "Type: " + (mType == BookingDetails.Type.COMMERCIAL ? "Commercial" : "Personal") + "\n" +
                    "Location: " + mLocation + "\n" +
                    "Laundry Shop: " + LaundryShop.findById(LaundryShop.class, laundryShop_id).getName() + "\n" +
                    "Service: " + LaundryService.findById(LaundryService.class, LaundryShopService.findById(LaundryShopService.class, laundryShopService_id).getLaundryServiceId()).getLabel() + "\n" +
                    "Pickup Date/Time: " + pick + "\n" +
                    "Return Date/Time: " + ret + "\n" +
                    "No. of clothes: " + mNoOfClothes + "\n" +
                    "Estimated kilo: " + mEstimatedKilo + "\n" +
                    "Notes: " + mNotes + "\n" +
                    "Estimated fee: " + estimatedFee;

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Confirm");
            builder.setMessage(message);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (messageDialog != null) {
                        messageDialog.dismiss();
                    }
                }
            });
            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BookingDetails booking = new BookingDetails(mMode, mType, laundryShopService_id, laundryShop_id, mUserId, mLocation, mNotes, pickupMillis, returnMillis, Integer.parseInt(mNoOfClothes), Float.parseFloat(mEstimatedKilo));
                    booking.save();
                    reset();
                    calendarAdapter.updateCalendar();
                }
            });
            messageDialog = builder.create();
            messageDialog.show();
        }
    }

    public void updateDateTimeString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEE, MM/dd/yyyy");
        String dateString = format.format(calendar.getTime());
        format = new SimpleDateFormat("hh:mm a");
        String timeString = format.format(calendar.getTime());
        pickupDateButton.setText(dateString);
        pickupTimeButton.setText(timeString);
        returnDateButton.setText(dateString);
        returnTimeButton.setText(timeString);
//        pickupDate = calendar.getTimeInMillis();
//        returnDate = calendar.getTimeInMillis();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        pickupDate = new DatePickerFragment.Date();
        pickupTime = new TimePickerFragment.Time();
        returnDate = new DatePickerFragment.Date();
        returnTime = new TimePickerFragment.Time();
        pickupDate.setDate(year, month, date);
        pickupTime.setTime(hour, minute);
        returnDate.setDate(year, month, date);
        returnTime.setTime(hour, minute);
    }

    public void reset() {
        try {
            bookingLayout.setVisibility(View.GONE);
            schedulerLayout.setVisibility(View.VISIBLE);
            updateCalendarAdapter();
            modeToggle.setChecked(false);
            typeToggle.setChecked(false);
            locationCheckbox.setChecked(false);
            locationEdittext.setText("");
            laundryShop_id = -1;
            laundryShopService_id = -1;
            ((ListViewArrayAdapter) serviceAdapter).selectedPosition = -1;
            ((ListViewArrayAdapter) shopAdapter).selectedPosition = -1;
            notesEdittext.setText("");
            noOfClothesEdittext.setText("");
            estimatedKiloEdittext.setText("");
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(fragmentView.getWindowToken(), 0);
        } catch (NullPointerException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    class ListViewArrayAdapter extends ArrayAdapter<String> {
        static final int LAUNDRY_SHOP = 1;
        static final int LAUNDRY_SERVICE = 2;
        ArrayList<String> names;
        int from;
        int selectedPosition = -1;

        private class ViewHolder {
            TextView textView;
            RadioButton radioButton;
        }

        public ListViewArrayAdapter(Context context, ArrayList<String> names, int from) {
            super(context, R.layout.radio_button_option, names);
            this.names = names;
            this.from = from;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            String name = names.get(position);
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.radio_button_option, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.option_text);
                viewHolder.radioButton = (RadioButton) convertView.findViewById(R.id.radio_button);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(name);
            viewHolder.radioButton.setChecked(position == selectedPosition);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (from == LAUNDRY_SHOP) {
                        laundryShop_id = shops.get(position).getId();
                        Log.d(TAG, "Laundry Shop Id : " + laundryShop_id);
                        updateLaundryServices();
                        shopDialog.dismiss();
                    } else if (from == LAUNDRY_SERVICE) {
                        laundryShopService_id = services.get(position).getId();
                        Log.d(TAG, "Laundry Shop Service: " + laundryShopService_id);
                        serviceDialog.dismiss();
                    }
                    selectedPosition = position;
                }
            };
            convertView.setOnClickListener(listener);
            viewHolder.radioButton.setOnClickListener(listener);
            return convertView;
        }
    }
}

class CalendarWeekViewAdapter extends FragmentStatePagerAdapter {
    long[] daysOfWeek;
    Fragment fragment;

    public CalendarWeekViewAdapter(FragmentManager fm) {
        super(fm);
    }

    public void updateCalendar() {
        ((CalendarWeekViewFragment) fragment).updateCalendar(false, -1);
    }

    public void setDaysOfWeek(long[] daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public Fragment getItem(int position) {
        fragment = new CalendarWeekViewFragment();
        Bundle args = new Bundle();
        args.putLongArray(CalendarWeekViewFragment.CALENDAR_DAY, daysOfWeek);
        fragment.setArguments(args);
        return fragment;
    }
}

class ScheduleListAdapter extends ArrayAdapter<BookingDetails> {
    long dateInMillis;
    List<BookingDetails> bookings;

    public ScheduleListAdapter(Context context, List<BookingDetails> bookings) {
        super(context, R.layout.schedule_list_item);
        this.bookings = bookings;
    }

    private class ViewHolder {
        TextView timeView, detailView;
        Button infoButton, cancelButton;
    }

    @Override
    public int getCount() {
        return bookings.size();
    }

    public void setDate(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.schedule_list_item, parent, false);
            viewHolder.timeView = (TextView) convertView.findViewById(R.id.time_text);
            viewHolder.detailView = (TextView) convertView.findViewById(R.id.detail_text);
            viewHolder.infoButton = (Button) convertView.findViewById(R.id.info_button);
            viewHolder.cancelButton = (Button) convertView.findViewById(R.id.cancel_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BookingDetails details = bookings.get(position);
        BookingDetails.Mode modeId = details.getMode();
        String laundryShop = details.getLaundryShop().getName();
        String mode = details.getModeName();
        String location = details.getLocation();
        Calendar calendar = Calendar.getInstance();
        if (modeId == BookingDetails.Mode.PICKUP) {
            calendar.setTimeInMillis(details.getPickupDate());
        } else if (modeId == BookingDetails.Mode.DELIVERY) {
            calendar.setTimeInMillis(details.getReturnDate());
        }
        SimpleDateFormat format = new SimpleDateFormat("h:mma");
        viewHolder.timeView.setText(format.format(calendar.getTime()));
        viewHolder.detailView.setText(laundryShop + "\n" + mode + " at " + location);
        Log.d("SchedulerFragment", "Position " + position + ": " + format.format(calendar.getTime()) + "\n" + laundryShop + "\n" + mode + " at " + location);
        return convertView;
    }
}