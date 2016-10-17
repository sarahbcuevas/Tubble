package com.laundryapp.tubble.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class StatusFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static String TAG = "StatusFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // for handling back button press; must return to Scheduler if true, else, return to status list.
    private static int fragmentCaller = 0;
    public static final int DEFAULT = 0;
    public static final int SCHEDULER = 1;
    public static final int STATUS_LIST = 2;
    public static final int APPROVED_STATUS_LIST = 3;

    View fragmentView;
    static LinearLayout noLaundryLayout, laundryProcessedLayout, laundryListLayout;
    static LayoutInflater mInflater;

    /* No laundry layout */
    TextView noLaundryShopText, noLaundryPickup, noLaundryDelivery;
    static RelativeLayout nextScheduleLayout;
    static TextView nextLaundryShop, nextPickup, nextReturn;
    private ImageView nextAddButton, nextCancelButton;

    /* Laundry Processed */
    static TextView laundryProcessedStatus, laundryProcessedTime, laundryProcessedShop,
            laundryProcessedService, laundryProcessedPickup, laundryProcessedDelivery,
            laundryProcessedFee;
    private static ImageView laundryProcessedCallButton, laundryProcessedCancelButton;

    /* Laundry List */
    private static LinearLayout laundryListLayoutList;

    /* Approved Bookings List */
    private static LinearLayout approvedBookingsStatus, approvedBookingsLayout, deniedBookingsLayout, approvedBookingsList, deniedBookingsList;

    private OnFragmentInteractionListener mListener;
    private static Context mContext;

    private static long selectedBookingId = -1;

    private Dialog dialog;

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
        laundryListLayout = (LinearLayout) fragmentView.findViewById(R.id.laundry_list_layout);
        approvedBookingsStatus = (LinearLayout) fragmentView.findViewById(R.id.approved_bookings_status);

        /* No Laundry Layout */
        nextScheduleLayout = (RelativeLayout) noLaundryLayout.findViewById(R.id.next_schedule);
        nextLaundryShop = (TextView) nextScheduleLayout.findViewById(R.id.laundry_shop_text);
        nextPickup = (TextView) nextScheduleLayout.findViewById(R.id.pick_up_text);
        nextReturn = (TextView) nextScheduleLayout.findViewById(R.id.delivery_text);
        nextAddButton = (ImageView) nextScheduleLayout.findViewById(R.id.add_button);
        nextCancelButton = (ImageView) nextScheduleLayout.findViewById(R.id.cancel_button);

        /* Laundry Processed */
        laundryProcessedStatus = (TextView) laundryProcessedLayout.findViewById(R.id.status);
        laundryProcessedTime = (TextView) laundryProcessedLayout.findViewById(R.id.time_remaining);
        laundryProcessedShop = (TextView) laundryProcessedLayout.findViewById(R.id.laundry_shop_text);
        laundryProcessedService = (TextView) laundryProcessedLayout.findViewById(R.id.laundry_service_text);
        laundryProcessedPickup = (TextView) laundryProcessedLayout.findViewById(R.id.pick_up_text);
        laundryProcessedDelivery = (TextView) laundryProcessedLayout.findViewById(R.id.delivery_text);
        laundryProcessedFee = (TextView) laundryProcessedLayout.findViewById(R.id.fee);
        laundryProcessedCallButton = (ImageView) laundryProcessedLayout.findViewById(R.id.call_button);
        laundryProcessedCancelButton = (ImageView) laundryProcessedLayout.findViewById(R.id.cancel_button);

        /* Laundry List Layout */
        laundryListLayoutList = (LinearLayout) laundryListLayout.findViewById(R.id.laundry_list);

        /* Approved Bookings List */
        approvedBookingsLayout = (LinearLayout) approvedBookingsStatus.findViewById(R.id.approved_bookings);
        approvedBookingsList = (LinearLayout) approvedBookingsLayout.findViewById(R.id.approved_bookings_list);
        deniedBookingsLayout = (LinearLayout) approvedBookingsStatus.findViewById(R.id.denied_bookings);
        deniedBookingsList = (LinearLayout) deniedBookingsLayout.findViewById(R.id.denied_bookings_list);

        nextAddButton.setOnClickListener(this);
        nextCancelButton.setOnClickListener(this);
        laundryProcessedCallButton.setOnClickListener(this);
        laundryProcessedCancelButton.setOnClickListener(this);

        if (getCheckStatusFromScheduler() == SCHEDULER) {
            // execute onCheckBookingStatus(id)
        } else {
            updateLaundryList();
        }

        return fragmentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            setCheckStatusFromScheduler(DEFAULT);
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
        void showCreateBookingPage();

    }

    public static void onCheckBookingStatus(long id, int caller) {
        setCheckStatusFromScheduler(caller);
        noLaundryLayout.setVisibility(View.GONE);
        laundryListLayout.setVisibility(View.GONE);
        approvedBookingsStatus.setVisibility(View.GONE);
        laundryProcessedLayout.setVisibility(View.VISIBLE);
        updateLaundryProcessedLayout(id);
    }

    public static void updateLaundryList() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        List<BookingDetails> details = BookingDetails.find(BookingDetails.class, "m_User_Id = ?", Long.toString(Utility.getUserId(mContext)));
        long startTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, 7);
        long endTime = calendar.getTimeInMillis();
        boolean isLaundryExists = false;
        laundryListLayoutList.removeAllViews();
        BookingDetails nextLaundry = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma, MMMM dd, yyyy");
        long detail_id = -1;

        for (BookingDetails detail : details) {
            if (detail.getStatus() == Status.REJECTED) {
                continue;
            }
            long returnDate = detail.getReturnDate();
            if ((startTime <= returnDate) && (returnDate <= endTime)) {
                isLaundryExists = true;
                /* Status List Item */
                View statusListItemView = mInflater.inflate(R.layout.booking_status_list_item, null);
                TextView statusLaundryShop = (TextView) statusListItemView.findViewById(R.id.laundry_shop);
                TextView statusLaundryShopId = (TextView) statusListItemView.findViewById(R.id.laundry_shop_id);
                TextView statusFee = (TextView) statusListItemView.findViewById(R.id.fee);
                TextView statusTimeRemaining = (TextView) statusListItemView.findViewById(R.id.time_remaining);
                statusLaundryShop.setText(detail.getLaundryShop().getName());
                statusLaundryShopId.setText(Long.toString(detail.getId()));
                statusFee.setText("Fee: " + detail.getFee());
                statusTimeRemaining.setText(Utility.getTimeDifference(mContext, System.currentTimeMillis(), returnDate));
                detail_id = detail.getId();
                statusListItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long id = Long.parseLong(((TextView) view.findViewById(R.id.laundry_shop_id)).getText().toString());
                        onCheckBookingStatus(id, StatusFragment.STATUS_LIST);
                    }
                });
                laundryListLayoutList.addView(statusListItemView);
                // add to list
            } else if (returnDate > System.currentTimeMillis()) {
                if ((nextLaundry == null) || (nextLaundry.getReturnDate() > returnDate)) {
                    nextLaundry = detail;
                }
            }
        }

        laundryProcessedLayout.setVisibility(View.GONE);
        approvedBookingsStatus.setVisibility(View.GONE);
        if(isLaundryExists) {
            noLaundryLayout.setVisibility(View.GONE);
            if (laundryListLayoutList.getChildCount() == 1) {
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
                selectedBookingId = nextLaundry.getId();
            }
        }
    }

    private static void updateLaundryProcessedLayout(long id) {
        selectedBookingId = id;
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

            laundryProcessedTime.setText(Utility.getTimeDifference(mContext, dateCreated, deliveryDate));
            laundryProcessedShop.setText(laundryShop);
            laundryProcessedService.setText(laundryService);
            laundryProcessedPickup.setText(dateFormat.format(pickupDate));
            laundryProcessedDelivery.setText(dateFormat.format(deliveryDate));
            laundryProcessedFee.setText(Float.toString(fee));
        }
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder;
        switch (view.getId()) {
            case R.id.add_button:
                mListener.showCreateBookingPage();
                break;
            case R.id.cancel_button:
                builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Cancel");
                builder.setMessage("Are you sure you want to cancel your laundry schedule?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BookingDetails details = BookingDetails.findById(BookingDetails.class, selectedBookingId);
                        if (details != null) {
                            boolean isDeleted = details.delete();
                            if (isDeleted) {
//                              details.delete();
                                updateLaundryList();
                                SchedulerFragment.updateScheduleListAndCalendar();
                            }
                        }
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;
            case R.id.call_button:
                BookingDetails details = BookingDetails.findById(BookingDetails.class, selectedBookingId);
                if (details != null) {
                    LaundryShop shop = details.getLaundryShop();
                    final String[] contacts = shop.getContact().split("/");
                    for (int i = 0; i < contacts.length; i++) {
                        contacts[i] = contacts[i].replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace(".", "").trim();
                    }

                    ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, contacts);
                    final Intent intent = new Intent(Intent.ACTION_DIAL);
                    if (contacts.length > 1) {
                        builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Contact");
                        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                intent.setData(Uri.parse("tel:" + contacts[i]));
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        dialog = builder.create();
                        dialog.show();
                    } else if (contacts.length == 1) {
                        intent.setData(Uri.parse("tel:" + contacts[0]));
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "No contact number available.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    public static void updateApprovedBookingsList() {
        setCheckStatusFromScheduler(APPROVED_STATUS_LIST);
        laundryListLayout.setVisibility(View.GONE);
        laundryProcessedLayout.setVisibility(View.GONE);

        List<BookingDetails> approvedBookings = BookingDetails.find(BookingDetails.class, "m_User_Id = ? and m_Status = ?", Long.toString(Utility.getUserId(mContext)), BookingDetails.Status.ACCEPTED.name());
        List<BookingDetails> deniedBookings = BookingDetails.find(BookingDetails.class, "m_User_Id = ? and m_Status = ?", Long.toString(Utility.getUserId(mContext)), BookingDetails.Status.REJECTED.name());

        if (approvedBookings.isEmpty() && deniedBookings.isEmpty()) {
            noLaundryLayout.setVisibility(View.VISIBLE);
            approvedBookingsStatus.setVisibility(View.GONE);
            return;
        } else {
            noLaundryLayout.setVisibility(View.GONE);
            approvedBookingsStatus.setVisibility(View.VISIBLE);
        }

        if (approvedBookings.isEmpty()) {
            approvedBookingsLayout.setVisibility(View.GONE);
        } else {
            approvedBookingsLayout.setVisibility(View.VISIBLE);
            approvedBookingsList.removeAllViews();
            for (BookingDetails details : approvedBookings) {
                View view = mInflater.inflate(R.layout.approved_bookings_list_item, null);
                TextView laundryShop = (TextView) view.findViewById(R.id.laundry_shop);
                TextView laundryService = (TextView) view.findViewById(R.id.laundry_service);
                TextView pickupDate = (TextView) view.findViewById(R.id.pick_up_datetime);
                TextView returnDate = (TextView) view.findViewById(R.id.return_datetime);
                TextView fee = (TextView) view.findViewById(R.id.fee);
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma, MMMM dd, yyyy");

                laundryShop.setText(details.getLaundryShop().getName());
                laundryService.setText(details.getLaundryServiceName());
                pickupDate.setText("Pick Up: " + dateFormat.format(details.getPickupDate()));
                returnDate.setText("Delivery: " + dateFormat.format(details.getReturnDate()));
                fee.setText("P " + details.getFee());

                approvedBookingsList.addView(view);
            }
        }

        if (deniedBookings.isEmpty()) {
            deniedBookingsLayout.setVisibility(View.GONE);
        } else {
            deniedBookingsLayout.setVisibility(View.VISIBLE);
            deniedBookingsList.removeAllViews();

            for (BookingDetails details : deniedBookings) {
                View view = mInflater.inflate(R.layout.approved_bookings_list_item, null);
                TextView laundryShop = (TextView) view.findViewById(R.id.laundry_shop);
                TextView laundryService = (TextView) view.findViewById(R.id.laundry_service);
                TextView pickupDate = (TextView) view.findViewById(R.id.pick_up_datetime);
                TextView returnDate = (TextView) view.findViewById(R.id.return_datetime);
                TextView fee = (TextView) view.findViewById(R.id.fee);
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma, MMMM dd, yyyy");

                laundryShop.setText(details.getLaundryShop().getName());
                laundryService.setText(details.getLaundryServiceName());
                pickupDate.setText("Pick Up: " + dateFormat.format(details.getPickupDate()));
                returnDate.setText("Delivery: " + dateFormat.format(details.getReturnDate()));
                fee.setText("P " + details.getFee());

                deniedBookingsList.addView(view);
            }
        }
    }

    public static void setCheckStatusFromScheduler (int status) {
        fragmentCaller = status;
    }

    public static int getCheckStatusFromScheduler() {
        return fragmentCaller;
    }
}
