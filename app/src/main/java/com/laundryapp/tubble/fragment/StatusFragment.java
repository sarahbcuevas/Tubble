package com.laundryapp.tubble.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laundryapp.tubble.R;
import com.laundryapp.tubble.Utility;
import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.BookingDetails.Status;
import com.laundryapp.tubble.entities.LaundryShop;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = this.getClass().getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    boolean isCheckBookingStatus = false;

    View fragmentView;
    static LinearLayout noLaundryLayout, laundryProcessedLayout, laundryListLayout;
    static LayoutInflater mInflater;

    /* No laundry layout */
    TextView noLaundryShopText, noLaundryPickup, noLaundryDelivery;
    static RelativeLayout nextScheduleLayout;
    static TextView nextLaundryShop, nextPickup, nextReturn;

    /* Laundry Processed */
    static TextView laundryProcessedStatus, laundryProcessedTime, laundryProcessedShop,
            laundryProcessedService, laundryProcessedPickup, laundryProcessedDelivery,
            laundryProcessedFee;

    private OnFragmentInteractionListener mListener;
    private static Context mContext;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
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
        mInflater = inflater;
        fragmentView = inflater.inflate(R.layout.fragment_status, container, false);
        noLaundryLayout = (LinearLayout) fragmentView.findViewById(R.id.no_laundry);
        laundryProcessedLayout = (LinearLayout) fragmentView.findViewById(R.id.laundry_processed);
        laundryListLayout = (LinearLayout) fragmentView.findViewById(R.id.laundry_list);

        /* No Laundry Layout */
        nextScheduleLayout = (RelativeLayout) noLaundryLayout.findViewById(R.id.next_schedule);
        nextLaundryShop = (TextView) nextScheduleLayout.findViewById(R.id.laundry_shop_text);
        nextPickup = (TextView) nextScheduleLayout.findViewById(R.id.pick_up_text);
        nextReturn = (TextView) nextScheduleLayout.findViewById(R.id.delivery_text);

        /* Laundry Processed */
        laundryProcessedStatus = (TextView) laundryProcessedLayout.findViewById(R.id.status);
        laundryProcessedTime = (TextView) laundryProcessedLayout.findViewById(R.id.time_remaining);
        laundryProcessedShop = (TextView) laundryProcessedLayout.findViewById(R.id.laundry_shop_text);
        laundryProcessedService = (TextView) laundryProcessedLayout.findViewById(R.id.laundry_service_text);
        laundryProcessedPickup = (TextView) laundryProcessedLayout.findViewById(R.id.pick_up_text);
        laundryProcessedDelivery = (TextView) laundryProcessedLayout.findViewById(R.id.delivery_text);
        laundryProcessedFee = (TextView) laundryProcessedLayout.findViewById(R.id.fee);

        if (isCheckBookingStatus) {

        } else {
            updateLaundryList();
        }

        return fragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        void onFragmentInteraction(Uri uri);
    }

    public void onCheckBookingStatus(long id) {
        isCheckBookingStatus = true;
        noLaundryLayout.setVisibility(View.GONE);
        laundryListLayout.setVisibility(View.GONE);
        laundryProcessedLayout.setVisibility(View.VISIBLE);
        updateLaundryProcessedLayout(id);
        isCheckBookingStatus = false;
    }

    public static void updateLaundryList() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        List<BookingDetails> details = BookingDetails.find(BookingDetails.class, "m_User_Id = ?", Long.toString(Utility.getUserId(mContext)));
//        List<BookingDetails> details = BookingDetails.listAll(BookingDetails.class);
//        int startMonth = calendar.get(Calendar.MONTH);
//        int startDay = calendar.get(Calendar.DAY_OF_MONTH);
//        int startYear = calendar.get(Calendar.YEAR);
        long startTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, 7);
//        int endMonth = calendar.get(Calendar.MONTH);
//        int endDay = calendar.get(Calendar.DAY_OF_MONTH);
//        int endYear = calendar.get(Calendar.YEAR);
        long endTime = calendar.getTimeInMillis();
        boolean isLaundryExists = false;
        laundryListLayout.removeAllViews();
        BookingDetails nextLaundry = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma, MMMM dd, yyyy");
        long detail_id = -1;
//        Log.d("Sarah", "Date start: " + startMonth + "/" + startDay + "/" + startYear);
//        Log.d("Sarah", "Date end: " + endMonth + "/" + endDay + "/" + endYear);

        for (BookingDetails detail : details) {
            long returnDate = detail.getReturnDate();
//            calendar.setTimeInMillis(returnDate);
//            int detailMonth = calendar.get(Calendar.MONTH);
//            int detailDay = calendar.get(Calendar.DAY_OF_MONTH);
//            int detailYear = calendar.get(Calendar.YEAR);
//            Log.d("Sarah", "Date return: " + detailMonth + "/" + detailDay + "/" + detailYear);
//            if (((detailYear == startYear) || (detailYear == endYear)) && ((detailMonth == startMonth) ||
//                    (detailMonth == endMonth)) && ((startDay <= detailDay) && (detailDay <= endDay))) {
            if ((startTime <= returnDate) && (returnDate <= endTime)) {
                isLaundryExists = true;
                /* Status List Item */
                View statusListItemView = mInflater.inflate(R.layout.booking_status_list_item, null);
                TextView statusLaundryShop = (TextView) statusListItemView.findViewById(R.id.laundry_shop);
                TextView statusFee = (TextView) statusListItemView.findViewById(R.id.fee);
                TextView statusTimeRemaining = (TextView) statusListItemView.findViewById(R.id.time_remaining);
                statusLaundryShop.setText(detail.getLaundryShop().getName());
                statusFee.setText("Fee: " + detail.getFee());
                statusTimeRemaining.setText(Utility.getTimeDifference(System.currentTimeMillis(), returnDate));
                laundryListLayout.addView(statusListItemView);
                detail_id = detail.getId();
                // add to list
            } else if (returnDate > System.currentTimeMillis()) {
                if ((nextLaundry == null) || (nextLaundry.getReturnDate() > returnDate)) {
                    nextLaundry = detail;
                }
            }
        }

        laundryProcessedLayout.setVisibility(View.GONE);
        if(isLaundryExists) {
            noLaundryLayout.setVisibility(View.GONE);
            if (laundryListLayout.getChildCount() == 1) {
                laundryListLayout.setVisibility(View.GONE);
                laundryProcessedLayout.setVisibility(View.VISIBLE);
                updateLaundryProcessedLayout(detail_id);
            } else {
                laundryListLayout.setVisibility(View.VISIBLE);
            }
        } else {
            noLaundryLayout.setVisibility(View.VISIBLE);
            laundryListLayout.setVisibility(View.GONE);
            if (nextLaundry == null) {
                nextScheduleLayout.setVisibility(View.GONE);
            } else {
                nextScheduleLayout.setVisibility(View.VISIBLE);
                nextLaundryShop.setText(nextLaundry.getLaundryShop().getName());
                nextPickup.setText("Pick up: " + dateFormat.format(nextLaundry.getPickupDate()));
                nextReturn.setText("Delivery: " + dateFormat.format(nextLaundry.getReturnDate()));
            }
        }
    }

    private static void updateLaundryProcessedLayout(long id) {
        BookingDetails details = BookingDetails.findById(BookingDetails.class, id);
        if (details != null) {
            Status status = details.getStatus();
            String laundryShop = details.getLaundryShop().getName();
            String laundryService = details.getLaundryServiceName();
            long dateCreated = System.currentTimeMillis();
            long pickupDate = details.getPickupDate();
            long deliveryDate = details.getReturnDate();
            float fee = details.getFee();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma, MMMM dd, yyyy");

            if (status == Status.NEW) {
                laundryProcessedStatus.setText(R.string.laundry_processed_new);
            } else if (status == Status.ACCEPTED) {
                laundryProcessedStatus.setText(R.string.laundry_processed_accepted);
            }

            laundryProcessedTime.setText(Utility.getTimeDifference(dateCreated, deliveryDate));
            laundryProcessedShop.setText(laundryShop);
            laundryProcessedService.setText(laundryService);
            laundryProcessedPickup.setText(dateFormat.format(pickupDate));
            laundryProcessedDelivery.setText(dateFormat.format(deliveryDate));
            laundryProcessedFee.setText(Float.toString(fee));
        }
    }
}
