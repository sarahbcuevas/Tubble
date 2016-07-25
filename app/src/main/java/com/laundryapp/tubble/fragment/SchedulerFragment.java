package com.laundryapp.tubble.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import android.widget.Toast;

import com.laundryapp.tubble.R;
import com.laundryapp.tubble.entities.LaundryService;
import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.LaundryShopService;
import com.laundryapp.tubble.entities.User;
import com.orm.query.Condition;
import com.orm.query.Select;

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
public class SchedulerFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "SchedulerFragment";
    private Calendar mCalendar;
    private View fragmentView;
    private TextView monthTextView, noScheduleText;
    private FrameLayout leftButton, rightButton;
    private LinearLayout modeLayout, typeLayout, locationLayout, shopLayout, serviceLayout, summaryLayout;
    private Switch modeToggle, typeToggle;
    private LinearLayout modeToggleLayout, typeToggleLayout, locationToggleLayout;
    private TextView modeText, typeText, locationText, shopText, serviceText, summaryText;
    private TextView[] days;
    private ListView listView;
    private ImageButton bookButton;
    private LinearLayout schedulerLayout, bookingLayout;
    private CheckBox locationCheckbox;
    private EditText locationEdittext;
    private Dialog shopDialog, serviceDialog, messageDialog;
    private long laundryShop_id = -1, laundryShopService_id = -1;
    List<LaundryShop> shops;
    List<LaundryShopService> services = new ArrayList<LaundryShopService>();

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
        monthTextView = (TextView) fragmentView.findViewById(R.id.month_text);
        noScheduleText = (TextView) fragmentView.findViewById(R.id.no_schedule_text);
        leftButton = (FrameLayout) fragmentView.findViewById(R.id.left_button);
        rightButton = (FrameLayout) fragmentView.findViewById(R.id.right_button);
        locationCheckbox = (CheckBox) fragmentView.findViewById(R.id.location_checkbox);
        locationEdittext = (EditText) fragmentView.findViewById(R.id.location_edittext);
        days = new TextView[7];
        days[0] = (TextView) fragmentView.findViewById(R.id.monday);
        days[1] = (TextView) fragmentView.findViewById(R.id.tuesday);
        days[2] = (TextView) fragmentView.findViewById(R.id.wednesday);
        days[3] = (TextView) fragmentView.findViewById(R.id.thursday);
        days[4] = (TextView) fragmentView.findViewById(R.id.friday);
        days[5] = (TextView) fragmentView.findViewById(R.id.saturday);
        days[6] = (TextView) fragmentView.findViewById(R.id.sunday);
        listView = (ListView) fragmentView.findViewById(R.id.schedule_list);
        bookButton = (ImageButton) fragmentView.findViewById(R.id.book_button);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        modeLayout.setOnClickListener(this);
        typeLayout.setOnClickListener(this);
        locationLayout.setOnClickListener(this);
        shopLayout.setOnClickListener(this);
        serviceLayout.setOnClickListener(this);
        summaryLayout.setOnClickListener(this);
        for (TextView day : days) {
            day.setOnClickListener(this);
        }
        bookButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    schedulerLayout.setVisibility(View.GONE);
                    bookingLayout.setVisibility(View.VISIBLE);
                    updateBookingSelectedItem(R.id.mode);
//                    modeToggleLayout.setVisibility(View.VISIBLE);
//                    typeToggleLayout.setVisibility(View.GONE);
//                    locationToggleLayout.setVisibility(View.GONE);
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
        updateCalendar();
        updateLaundryShops();
        updateMessageDialog();
        return fragmentView;
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

    private void updateCalendar() {
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int year = mCalendar.get(Calendar.YEAR);
        int week = mCalendar.get(Calendar.DAY_OF_WEEK) - 2;
        String monthString = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + Integer.toString(year);
        week = week < 0 ? 6 : week;
        int lastDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int[] actualDays = new int[7];
        for (int i = 0; i < 7; i++) {
            actualDays[i] = day + i - week;
            if (actualDays[i] == day) {
                days[i].setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                days[i].setBackground(getResources().getDrawable(R.drawable.calendar_day_selected, null));
            } else {
                days[i].setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_day));
                days[i].setBackground(null);
            }
            if (actualDays[i] <= 0) {
                mCalendar.set(year, month - 1, 1);
                actualDays[i] += mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                days[i].setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_day_disabled));
            } else if (actualDays[i] > lastDay) {
                actualDays[i] -= lastDay;
                days[i].setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_day_disabled));
            }

            days[i].setText(Integer.toString(actualDays[i]));
        }

        monthTextView.setText(monthString);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_button:
                break;
            case R.id.right_button:
                break;
            case R.id.monday:
                break;
            case R.id.tuesday:
                break;
            case R.id.wednesday:
                break;
            case R.id.thursday:
                break;
            case R.id.friday:
                break;
            case R.id.saturday:
                break;
            case R.id.sunday:
                break;
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
        final ArrayAdapter<String> adapter = new ListViewArrayAdapter(getContext(), shopsName, ListViewArrayAdapter.LAUNDRY_SHOP);
        adapter.notifyDataSetChanged();
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                laundryShop_id = shops.get(i).getId();
//                Toast.makeText(getContext(), "Laundry Shop Id : " + laundryShop_id, Toast.LENGTH_SHORT).show();
//                updateLaundryServices();
//                adapter.notifyDataSetChanged();
//                shopDialog.dismiss();
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
//        services = LaundryShopService.find(LaundryShopService.class, "laundryshop_id = ?", Long.toString(laundryShop_id));
//        services = Select.from(LaundryShopService.class).where(Condition.prop("laundryshop_id").eq(Long.toString(laundryShop_id))).list();
//        for (LaundryShopService service : services) {
//            LaundryService serv = LaundryService.findById(LaundryService.class, service.getLaundryServiceId());
//            servicesName.add(serv.getLabel());
//        }
        ArrayAdapter<String> adapter = new ListViewArrayAdapter(getContext(), servicesName, ListViewArrayAdapter.LAUNDRY_SERVICE);
        adapter.notifyDataSetChanged();
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
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

    public void updateMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.note);
        builder.setMessage(R.string.select_laundry_shop);
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

    class ListViewArrayAdapter extends ArrayAdapter<String> {
        static final int LAUNDRY_SHOP = 1;
        static final int LAUNDRY_SERVICE = 2;
        ArrayList<String> names;
        int from;

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

            convertView.setOnClickListener(new View.OnClickListener() {
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
                }
            });
            return convertView;
        }
    }
}
