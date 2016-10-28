package com.laundryapp.tubble.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.laundryapp.tubble.R;
import com.laundryapp.tubble.Utility;
import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.LaundryAssignment;
import com.laundryapp.tubble.entities.LaundryService;
import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.LaundryShopService;
import com.laundryapp.tubble.entities.User;
import com.laundryapp.tubble.fragment.DatePickerFragment.Date;
import com.laundryapp.tubble.fragment.TimePickerFragment.Time;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private static View fragmentView;
    protected static TextView monthTextView;
    protected static TextView noScheduleText;
    private FrameLayout leftButton, rightButton;
    private static LinearLayout modeLayout, typeLayout, locationLayout, shopLayout, serviceLayout, summaryLayout;
    private static Switch modeToggle, typeToggle;
    private static LinearLayout modeToggleLayout, typeToggleLayout, locationToggleLayout, summaryToggleLayout, confirmButton;
    private static TextView modeText, typeText, locationText, shopText, serviceText, summaryText;
    protected static ListView listView;
    protected static List<BookingDetails> allBookings;
    protected static ScheduleListAdapter listAdapter;
    private ImageButton bookButton;
    private static LinearLayout schedulerLayout, bookingLayout, manageBookingLayout;
    private static CheckBox locationCheckbox;
    private static EditText locationEdittext, notesEdittext, noOfClothesEdittext, estimatedKiloEdittext;
    private static Button pickupDateButton, pickupTimeButton, returnDateButton, returnTimeButton;
    private Dialog shopDialog, serviceDialog, messageDialog;
    private static long laundryShop_id = -1, laundryShopService_id = -1;
    List<LaundryShop> shops;
    List<LaundryShopService> services = new ArrayList<LaundryShopService>();
    private static Date pickupDate, returnDate;
    private static Time pickupTime, returnTime;
    private static ArrayAdapter<String> serviceAdapter, shopAdapter;
    private static CalendarWeekViewAdapter calendarAdapter;
    public static ViewPager calendarPager;
    private static FragmentManager fm;
    private static Context mContext;
    public static RelativeLayout laundryScheduleDetails;
    private static TextView scheduleCustomer, scheduleFee, scheduleMode, scheduleType, scheduleLocation, scheduleService, schedulePickupDate, schedulePickupLocation, scheduleDeliveryDate, scheduleDeliveryLocation, scheduleNoOfClothes, scheduleEstimatedKilo, scheduleNotes;

    private static boolean isModeDone, isTypeDone, isLocationDone, isLaundyShopDone, isServiceDone;
    private LinearLayout goToCurrent;

    static OnFragmentInteractionListener mListener;

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
        manageBookingLayout = (LinearLayout) fragmentView.findViewById(R.id.manage_laundry_layout);
        manageBookingLayout.setVisibility(Utility.getUserType(mContext) == User.Type.CUSTOMER ? View.VISIBLE : View.GONE);
        laundryScheduleDetails = (RelativeLayout) fragmentView.findViewById(R.id.laundry_schedule_details);
        laundryScheduleDetails.setVisibility(View.GONE);

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

        scheduleCustomer = (TextView) laundryScheduleDetails.findViewById(R.id.customer_name);
        scheduleFee = (TextView) laundryScheduleDetails.findViewById(R.id.fee);
        scheduleMode = (TextView) laundryScheduleDetails.findViewById(R.id.mode);
        scheduleType = (TextView) laundryScheduleDetails.findViewById(R.id.type);
        scheduleLocation = (TextView) laundryScheduleDetails.findViewById(R.id.location);
        scheduleService = (TextView) laundryScheduleDetails.findViewById(R.id.service);
        schedulePickupDate = (TextView) laundryScheduleDetails.findViewById(R.id.pickup_datetime);
        schedulePickupLocation = (TextView) laundryScheduleDetails.findViewById(R.id.pickup_location);
        scheduleDeliveryDate = (TextView) laundryScheduleDetails.findViewById(R.id.delivery_datetime);
        scheduleDeliveryLocation = (TextView) laundryScheduleDetails.findViewById(R.id.delivery_location);
        scheduleNoOfClothes = (TextView) laundryScheduleDetails.findViewById(R.id.no_of_clothes);
        scheduleEstimatedKilo = (TextView) laundryScheduleDetails.findViewById(R.id.estimated_kilo);
        scheduleNotes = (TextView) laundryScheduleDetails.findViewById(R.id.notes);

        listView = (ListView) fragmentView.findViewById(R.id.schedule_list);
        allBookings = new ArrayList<BookingDetails>();
        listAdapter = new ScheduleListAdapter((Context) mListener, allBookings);
        listView.setAdapter(listAdapter);
        bookButton = (ImageButton) fragmentView.findViewById(R.id.book_button);
        fm = getChildFragmentManager();
        calendarPager = (ViewPager) fragmentView.findViewById(R.id.calendar_pager);
        calendarPager.setOffscreenPageLimit(1);
        calendarAdapter = new CalendarWeekViewAdapter(fm);
        calendarPager.setAdapter(calendarAdapter);
        calendarPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setSelectedDateInCalendar(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

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

        bookButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    setCreateBookingVisible();
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
                    User user = User.findById(User.class, Utility.getUserId(getContext()));
                    str = user.getAddress();
                }
                locationEdittext.setText(str);
            }
        });

        mCalendar = Calendar.getInstance();
        mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        updateLaundryShops();
        updateMessageDialog(R.string.select_laundry_shop);

        goToCurrent = (LinearLayout) fragmentView.findViewById(R.id.go_to_current);
        goToCurrent.setOnClickListener(this);
        return fragmentView;
    }

    public static void setCreateBookingVisible() {
        schedulerLayout.setVisibility(View.GONE);
        bookingLayout.setVisibility(View.VISIBLE);
        updateDateTimeString();
        updateBookingSelectedItem(R.id.mode);
    }

    public static void updateCalendarAdapter() {
        try {
            calendarPager.setCurrentItem(5000);
            calendarAdapter.notifyDataSetChanged();
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void updateScheduleList(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        listAdapter.setDate(timeInMillis);
        List<BookingDetails> bookings = null;
        String user_id = Long.toString(Utility.getUserId(mContext));
        if (Utility.getUserType(mContext) == User.Type.CUSTOMER) {
            bookings = Select.from(BookingDetails.class).where(Condition.prop("m_User_Id").eq(user_id)).orderBy("m_Return_Date").list();
        } else if (Utility.getUserType(mContext) == User.Type.LAUNDRY_SHOP) {
            bookings = Select.from(BookingDetails.class).where(Condition.prop("m_Laundry_Shop_Id").eq(user_id)).orderBy("m_Return_Date").list();
        }
        SchedulerFragment.allBookings.clear();
        for (BookingDetails booking : bookings) {
            if (booking.getStatus() == BookingDetails.Status.REJECTED) {
                continue;
            }
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
        if (allBookings.isEmpty()) {
            noScheduleText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            noScheduleText.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listAdapter = new ScheduleListAdapter(mContext, SchedulerFragment.allBookings);
            listView.setAdapter(SchedulerFragment.listAdapter);
            listAdapter.notifyDataSetChanged();
        }
    }

    public static boolean onBackPressed() {
        if (bookingLayout != null && bookingLayout.getVisibility() == View.VISIBLE) {
            bookingLayout.setVisibility(View.GONE);
            schedulerLayout.setVisibility(View.VISIBLE);
            updateCalendarAdapter();
            reset();
            return true;
        } else if (laundryScheduleDetails != null && laundryScheduleDetails.getVisibility() == View.VISIBLE) {
            schedulerLayout.setVisibility(View.VISIBLE);
            laundryScheduleDetails.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
        fm = getChildFragmentManager();
        updateCalendarAdapter();
        updateScheduleList(System.currentTimeMillis());
//        calendarAdapter.updateCalendar();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            updateScheduleList(System.currentTimeMillis());
            schedulerLayout.setVisibility(View.VISIBLE);
            bookingLayout.setVisibility(View.GONE);
            laundryScheduleDetails.setVisibility(View.GONE);
        } else {
            if (mContext != null) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(schedulerLayout.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException ex1) {
            Log.e(TAG, ex1.getMessage(), ex1);
        } catch (IllegalAccessException ex2) {
            Log.e(TAG, ex2.getMessage(), ex2);
        } catch (IllegalStateException ex3) {
            Log.e(TAG, ex3.getMessage(), ex3);
        }
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
        void onCheckBookingStatus(long id);

        void onAddOrDeleteLaundrySchedule();

        void onViewLaundryScheduleDetails();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_button:
                int currentItem = calendarPager.getCurrentItem();
                if (currentItem > 0) {
                    currentItem--;
                    calendarPager.setCurrentItem(currentItem);
                }
                break;
            case R.id.right_button:
                int currentItem2 = calendarPager.getCurrentItem();
                if (currentItem2 < calendarAdapter.getCount() - 1) {
                    currentItem2++;
                    calendarPager.setCurrentItem(currentItem2);
                }
                break;
            case R.id.go_to_current:
                updateCalendarAdapter();
            case R.id.book_button:
                break;
            case R.id.mode:
                updateBookingSelectedItem(R.id.mode);
                isModeDone = true;
                break;
            case R.id.type:
                updateBookingSelectedItem(R.id.type);
                isTypeDone = true;
                break;
            case R.id.location:
                if (isTypeDone) {
                    updateBookingSelectedItem(R.id.location);
                }
                break;
            case R.id.shop:
                    if (!locationEdittext.getText().toString().isEmpty()) {
                        isLocationDone = true;
                        updateBookingSelectedItem(R.id.shop);
                        shopDialog.show();
                    }
                break;
            case R.id.service:
                if (laundryShop_id != -1) {
                    isLaundyShopDone = true;
                    updateBookingSelectedItem(R.id.service);
                    serviceDialog.show();
                }
                break;
            case R.id.summary:
                if (laundryShopService_id != -1) {
                    isServiceDone = true;
                    updateBookingSelectedItem(R.id.summary);
                }
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

    public static void updateBookingSelectedItem(int resId) {
        int confirmedBg = ContextCompat.getColor(mContext, R.color.tubble_yellow);
        int selectedBackground = ContextCompat.getColor(mContext, R.color.booking_field_background_blue);
        int deselectedBackground = ContextCompat.getColor(mContext, R.color.booking_field_background_gray);
        int selectedText = ContextCompat.getColor(mContext, R.color.booking_field_text_selected);
        int confirmedText = ContextCompat.getColor(mContext, R.color.booking_field_text_confirmed);
        int deselectedText = ContextCompat.getColor(mContext, R.color.booking_field_text_deselected);

        modeLayout.setBackgroundColor(resId == R.id.mode ? selectedBackground : confirmedBg);
        modeText.setTextColor(resId == R.id.mode ? selectedText : confirmedText);

        if (resId == R.id.type) {
            typeLayout.setBackgroundColor(selectedBackground);
            typeText.setTextColor(selectedText);

        } else {
            if (isTypeDone) {
                typeLayout.setBackgroundColor(confirmedBg);
                typeText.setTextColor(confirmedText);
            } else {
                typeLayout.setBackgroundColor(deselectedBackground);
                typeText.setTextColor(deselectedText);
            }
        }

        if (resId == R.id.location) {
            locationLayout.setBackgroundColor(selectedBackground);
            locationText.setTextColor(selectedText);

        } else {
            if (isLocationDone) {
                locationLayout.setBackgroundColor(confirmedBg);
                locationText.setTextColor(confirmedText);
            } else {
                locationLayout.setBackgroundColor(deselectedBackground);
                locationText.setTextColor(deselectedText);
            }
        }

        if (resId == R.id.shop) {
            shopLayout.setBackgroundColor(selectedBackground);
            shopText.setTextColor(selectedText);

        } else {
            if (isLaundyShopDone) {
                shopLayout.setBackgroundColor(confirmedBg);
                shopText.setTextColor(confirmedText);
            } else {
                shopLayout.setBackgroundColor(deselectedBackground);
                shopText.setTextColor(deselectedText);
            }
        }

        if (resId == R.id.service) {
            serviceLayout.setBackgroundColor(selectedBackground);
            serviceText.setTextColor(selectedText);

        } else {
            if (isLaundyShopDone) {
                serviceLayout.setBackgroundColor(confirmedBg);
                serviceText.setTextColor(confirmedText);
            } else {
                serviceLayout.setBackgroundColor(deselectedBackground);
                serviceText.setTextColor(deselectedText);
            }
        }

        if (resId == R.id.summary) {
            summaryLayout.setBackgroundColor(selectedBackground);
            summaryText.setTextColor(selectedText);

        } else {
            if (isServiceDone) {
                summaryLayout.setBackgroundColor(confirmedBg);
                summaryText.setTextColor(confirmedText);
            } else {
                summaryLayout.setBackgroundColor(deselectedBackground);
                summaryText.setTextColor(deselectedText);
            }
        }

//        shopLayout.setBackgroundColor(resId == R.id.shop ? selectedBackground : deselectedBackground);
//        shopText.setTextColor(resId == R.id.shop ? selectedText : deselectedText);
//        serviceLayout.setBackgroundColor(resId == R.id.service ? selectedBackground : deselectedBackground);
//        serviceText.setTextColor(resId == R.id.service ? selectedText : deselectedText);
//        summaryLayout.setBackgroundColor(resId == R.id.summary ? selectedBackground : deselectedBackground);
//        summaryText.setTextColor(resId == R.id.summary ? selectedText : deselectedText);

        modeToggleLayout.setVisibility(resId == R.id.mode ? View.VISIBLE : View.GONE);
        typeToggleLayout.setVisibility(resId == R.id.type ? View.VISIBLE : View.GONE);
        locationToggleLayout.setVisibility(resId == R.id.location ? View.VISIBLE : View.GONE);
        summaryToggleLayout.setVisibility(resId == R.id.summary ? View.VISIBLE : View.GONE);
    }

    public void resetTrackers() {
        isModeDone = false;
        isTypeDone = false;
        isLocationDone = false;
        isLaundyShopDone = false;
        isServiceDone = false;
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

    public static void setSelectedDateInCalendar(int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.DATE, (position - 5_000) * 7);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, -1);
        }
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        int year = calendar.get(Calendar.YEAR);
        String monthString = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + Integer.toString(year);
        SchedulerFragment.monthTextView.setText(monthString);
        int day = CalendarWeekViewFragment.getSelectedDayPosition() + 2;
        if (day == 8) {
            day = 1;
        }
        calendar.set(Calendar.DAY_OF_WEEK, day);
        updateScheduleList(calendar.getTimeInMillis());
    }

    public void createBooking() {
        final long dateCreated = System.currentTimeMillis();
        final BookingDetails.Mode mMode = modeToggle.isChecked() ? BookingDetails.Mode.DELIVERY : BookingDetails.Mode.PICKUP;
        final BookingDetails.Type mType = typeToggle.isChecked() ? BookingDetails.Type.PERSONAL : BookingDetails.Type.COMMERCIAL;
        final long mUserId = Utility.getUserId(getContext());
        final String mLocation = locationEdittext.getText().toString();
        final String mNotes = notesEdittext.getText().toString();
        final String mNoOfClothes = noOfClothesEdittext.getText().toString();
        final String mEstimatedKilo = estimatedKiloEdittext.getText().toString();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        calendar.set(pickupDate.year, pickupDate.month, pickupDate.date, pickupTime.hour, pickupTime.minute);
        final long pickupMillis = calendar.getTimeInMillis();
        String pick = format.format(calendar.getTime());
        calendar.set(returnDate.year, returnDate.month, returnDate.date, returnTime.hour, returnTime.minute);
        final long returnMillis = calendar.getTimeInMillis();
        String ret = format.format(calendar.getTime());

        if (mLocation.equals("")) {
            updateMessageDialog(R.string.input_location);
            messageDialog.show();
        } else if (laundryShop_id == -1) {
            updateMessageDialog(R.string.select_laundry_shop);
            messageDialog.show();
        } else if (laundryShopService_id == -1) {
            updateMessageDialog(R.string.select_laundry_service);
            messageDialog.show();
        } else if (getTimeDifferenceInHours(dateCreated, pickupMillis) < 3 || getTimeDifferenceInHours(dateCreated, returnMillis) < 3) {
            updateMessageDialog("You're laundry schedule is too close, you may choose to drop off your laundry in the shop or select another day.");
            messageDialog.show();
        } else if (mNoOfClothes.equals("")) {
            updateMessageDialog(R.string.input_no_of_clothes);
            messageDialog.show();
        } else if (mEstimatedKilo.equals("")) {
            updateMessageDialog(R.string.input_estimated_kilo);
            messageDialog.show();
        } else {
            final float estimatedFee = LaundryShopService.findById(LaundryShopService.class, laundryShopService_id).getPrice() * Float.parseFloat(mEstimatedKilo);

            String message = "Please confirm details below:\n\n" +
                    "Mode: " + (mMode == BookingDetails.Mode.DELIVERY ? "Delivery" : "Pick-Up") + "\n" +
                    "Type: " + (mType == BookingDetails.Type.COMMERCIAL ? "Commercial" : "Personal") + "\n" +
                    "Location: " + mLocation + "\n" +
                    "Laundry Shop: " + LaundryShop.findById(LaundryShop.class, laundryShop_id).getName() + "\n" +
                    "Service: " + LaundryService.findById(LaundryService.class, LaundryShopService.findById(LaundryShopService.class, laundryShopService_id).getLaundryServiceId()).getLabel() + "\n" +
                    "Pickup Date/Time: " + pick + "\n" +
                    "Return Date/Time: " + ret + "\n" +
                    "No. of clothes: " + mNoOfClothes + "\n" +
                    "Estimated weight (kg): " + mEstimatedKilo + "kg\n" +
                    "Notes: " + mNotes + "\n" +
                    "Estimated fee: " + estimatedFee;

            View view = View.inflate(getContext(), R.layout.confirm_dialog_view, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Confirm");
            ((TextView) view.findViewById(R.id.message)).setText(message);
            builder.setView(view);
//            builder.setMessage(message);
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
                    BookingDetails booking = new BookingDetails(dateCreated, mMode, mType, laundryShopService_id, laundryShop_id, mUserId, mLocation, mNotes, pickupMillis, returnMillis, Integer.parseInt(mNoOfClothes), Float.parseFloat(mEstimatedKilo), estimatedFee);
//                    booking.save();
                    reset();
                    Utility.sendLaundryRequestThruSms(getContext(), booking);
//                    calendarAdapter.updateCalendar();
//                    mListener.onAddOrDeleteLaundrySchedule();
//                    listAdapter.notifyDataSetChanged();
                }
            });
            messageDialog = builder.create();
            messageDialog.show();
        }
    }

    private long getTimeDifferenceInHours(long startTime, long endTime) {
        long difference = endTime - startTime;
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long elapsedHours = difference / hoursInMilli;
        return elapsedHours;
    }

    public static void updateScheduleListAndCalendar() {
        listAdapter.notifyDataSetChanged();
    }

    public static void updateDateTimeString() {
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

    public static void reset() {
        try {
            bookingLayout.setVisibility(View.GONE);
            schedulerLayout.setVisibility(View.VISIBLE);
            updateBookingSelectedItem(R.id.mode);
            isModeDone = false;
            isTypeDone = false;
            isLocationDone = false;
            isLaundyShopDone = false;
            isServiceDone = false;
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
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
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

    public static void updateLaundryScheduleDetails(long detailsId) {
        BookingDetails details = BookingDetails.findById(BookingDetails.class, detailsId);
        if (details == null) {
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma, MMMM dd, yyyy");
        String location = details.getLaundryShop().getName();
        List<LaundryAssignment> assignments = LaundryAssignment.find(LaundryAssignment.class, "m_Booking_Details_Id = ?", Long.toString(details.getId()));
        if (!assignments.isEmpty()) {
            location += ", " + assignments.get(0).getLaundryStaffName();
        }
        laundryScheduleDetails.setVisibility(View.VISIBLE);
        schedulerLayout.setVisibility(View.GONE);
        bookingLayout.setVisibility(View.GONE);
        mListener.onViewLaundryScheduleDetails();
        scheduleCustomer.setText(details.getCustomerName());
        scheduleFee.setText("P" + details.getFee());
        scheduleMode.setText(details.getModeName());
        scheduleType.setText(details.getTypeName());
        scheduleLocation.setText(location);
        scheduleService.setText(details.getLaundryServiceName());
        schedulePickupDate.setText(dateFormat.format(details.getPickupDate()));
        schedulePickupLocation.setText(details.getLocation());
        scheduleDeliveryDate.setText(dateFormat.format(details.getReturnDate()));
        scheduleDeliveryLocation.setText(details.getLocation());
        scheduleNoOfClothes.setText(details.getNoOfClothes() + "");
        scheduleEstimatedKilo.setText(details.getEstimatedKilo() + "kg");
        scheduleNotes.setText(details.getNotes());
    }
}

class CalendarWeekViewAdapter extends FragmentStatePagerAdapter {
    CalendarWeekViewFragment fragment;

    public CalendarWeekViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 10_000;
    }

    @Override
    public Fragment getItem(int position) {
        fragment = new CalendarWeekViewFragment();
        fragment.setPosition(position);
        return fragment;
    }
}

class ScheduleListAdapter extends ArrayAdapter<BookingDetails> {
    long dateInMillis;
    List<BookingDetails> bookings;
    Context context;
    Dialog messageDialog;

    public ScheduleListAdapter(Context context, List<BookingDetails> bookings) {
        super(context, R.layout.schedule_list_item);
        this.context = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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

        final BookingDetails details = bookings.get(position);
        BookingDetails.Mode modeId = details.getMode();
        final String laundryShop = details.getLaundryShop().getName();
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
        viewHolder.cancelButton.setVisibility(Utility.getUserType(context) == User.Type.CUSTOMER ? View.VISIBLE : View.GONE);
        viewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cancel");
                builder.setMessage("Are you sure you want to cancel your laundry schedule with " + laundryShop + "?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (messageDialog != null) {
                            messageDialog.dismiss();
                        }
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        boolean isDeleted = BookingDetails.delete(details);
                        boolean isDeleted = details.delete();
                        if (isDeleted) {
                            bookings.remove(position);
                            notifyDataSetChanged();
                            ((SchedulerFragment.OnFragmentInteractionListener) context).onAddOrDeleteLaundrySchedule();
                        }
                    }
                });
                messageDialog = builder.create();
                messageDialog.show();
            }
        });
        viewHolder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.Type userType = Utility.getUserType(context);
                if (userType == User.Type.CUSTOMER) {
                    ((SchedulerFragment.OnFragmentInteractionListener) context).onCheckBookingStatus(details.getId());
                } else if (userType == User.Type.LAUNDRY_SHOP) {
                    SchedulerFragment.updateLaundryScheduleDetails(details.getId());
                }
            }
        });
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (bookings.isEmpty()) {
            SchedulerFragment.noScheduleText.setVisibility(View.VISIBLE);
            SchedulerFragment.listView.setVisibility(View.GONE);
        }
    }
}