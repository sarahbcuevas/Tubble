package com.laundryapp.tubble.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.laundryapp.tubble.MainActivity;
import com.laundryapp.tubble.R;
import com.laundryapp.tubble.Utility;
import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.BookingDetails.Status;
import com.laundryapp.tubble.entities.LaundryAssignment;
import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.LaundryShopStaff;
import com.laundryapp.tubble.entities.User;
import com.laundryapp.tubble.entities.UserRating;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    static LinearLayout noLaundryLayout, laundryProcessedLayout, laundryListLayout, laundryScheduleDetailsLayout;
    static RelativeLayout laundryCompletedLayout;
    static LayoutInflater mInflater;

    /* No laundry layout */
    TextView noLaundryShopText, noLaundryPickup, noLaundryDelivery;
    static RelativeLayout nextScheduleLayout;
    static TextView nextLaundryShop, nextPickup, nextReturn, noLaundryDetails;
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

    /* Laundry Schedule Details */
    private static TextView scheduleCustomer, scheduleFee, scheduleMode, scheduleType, scheduleLocation, scheduleService, schedulePickupDate, schedulePickupLocation, scheduleDeliveryDate, scheduleDeliveryLocation, scheduleNoOfClothes, scheduleEstimatedKilo, scheduleNotes;
    private static ImageView editButton, doneButton;
    private static LinearLayout laundryAcceptedButton, processingButton, returningButton;
    private static RelativeLayout laundryAcceptedBg, processingBg, returningBg;
    private static EditText feeEdittext, noOfClothesEdittext, estimatedKiloEdittext;

    /* Laundry Completed */
    private static RatingBar ratingBar;
    private static EditText commentsEdittext;
    private static ImageView sendRatingButton;

    private static OnFragmentInteractionListener mListener;
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
        laundryScheduleDetailsLayout = (LinearLayout) fragmentView.findViewById(R.id.laundry_schedule_details);
        laundryCompletedLayout = (RelativeLayout) fragmentView.findViewById(R.id.laundry_completed);

        /* No Laundry Layout */
        noLaundryDetails = (TextView) noLaundryLayout.findViewById(R.id.details);
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

        /* Laundry Schedule Details */
        scheduleCustomer = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.customer_name);
        scheduleFee = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.fee);
        scheduleMode = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.mode);
        scheduleType = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.type);
        scheduleLocation = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.location);
        scheduleService = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.service);
        schedulePickupDate = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.pickup_datetime);
        schedulePickupLocation = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.pickup_location);
        scheduleDeliveryDate = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.delivery_datetime);
        scheduleDeliveryLocation = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.delivery_location);
        scheduleNoOfClothes = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.no_of_clothes);
        scheduleEstimatedKilo = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.estimated_kilo);
        scheduleNotes = (TextView) laundryScheduleDetailsLayout.findViewById(R.id.notes);
        editButton = (ImageView) laundryScheduleDetailsLayout.findViewById(R.id.edit_button);
        laundryAcceptedButton = (LinearLayout) laundryScheduleDetailsLayout.findViewById(R.id.laundry_accepted_button);
        processingButton = (LinearLayout) laundryScheduleDetailsLayout.findViewById(R.id.processing_button);
        returningButton = (LinearLayout) laundryScheduleDetailsLayout.findViewById(R.id.returning_button);
        laundryAcceptedBg = (RelativeLayout) laundryScheduleDetailsLayout.findViewById(R.id.laundry_accepted_bg);
        processingBg = (RelativeLayout) laundryScheduleDetailsLayout.findViewById(R.id.processing_bg);
        returningBg = (RelativeLayout) laundryScheduleDetailsLayout.findViewById(R.id.returning_bg);
        feeEdittext = (EditText) laundryScheduleDetailsLayout.findViewById(R.id.fee_edittext);
        noOfClothesEdittext = (EditText) laundryScheduleDetailsLayout.findViewById(R.id.no_of_clothes_edittext);
        estimatedKiloEdittext = (EditText) laundryScheduleDetailsLayout.findViewById(R.id.estimated_kilo_edittext);
        doneButton = (ImageView) laundryScheduleDetailsLayout.findViewById(R.id.done_button);

        /* Laundry Completed */
        ratingBar = (RatingBar) laundryCompletedLayout.findViewById(R.id.rating_bar);
        commentsEdittext = (EditText) laundryCompletedLayout.findViewById(R.id.comments_edittext);
        sendRatingButton = (ImageView) laundryCompletedLayout.findViewById(R.id.send_rating_button);

        nextAddButton.setOnClickListener(this);
        nextCancelButton.setOnClickListener(this);
        laundryProcessedCallButton.setOnClickListener(this);
        laundryProcessedCancelButton.setOnClickListener(this);
        laundryAcceptedButton.setOnClickListener(this);
        processingButton.setOnClickListener(this);
        returningButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        sendRatingButton.setOnClickListener(this);

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
            if (mContext != null && fragmentView != null) {
                InputMethodManager imm  = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fragmentView.getWindowToken(), 0);
            }
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
        void onViewLaundryScheduleDetails();
    }

    public static void onCheckBookingStatus(long id, int caller) {
        setCheckStatusFromScheduler(caller);
        noLaundryLayout.setVisibility(View.GONE);
        laundryListLayout.setVisibility(View.GONE);
        approvedBookingsStatus.setVisibility(View.GONE);
        laundryCompletedLayout.setVisibility(View.GONE);
        laundryProcessedLayout.setVisibility(View.VISIBLE);
        updateLaundryProcessedLayout(id);
    }

    public static void updateLaundryList() {
        final User.Type userType = Utility.getUserType(mContext);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        List<BookingDetails> details = null;
        if (userType == User.Type.CUSTOMER) {
            details = BookingDetails.find(BookingDetails.class, "m_User_Id = ?", Long.toString(Utility.getUserId(mContext)));
        } else if (userType == User.Type.LAUNDRY_SHOP) {
            details = BookingDetails.find(BookingDetails.class, "m_Laundry_Shop_Id = ?", Long.toString(Utility.getUserId(mContext)));
        } else {
            return;
        }
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
                statusLaundryShopId.setText(Long.toString(detail.getId()));
                statusFee.setText("Fee: P" + detail.getFee());
                statusTimeRemaining.setText(Utility.getTimeDifference(mContext, System.currentTimeMillis(), returnDate));
                String laundryShopName;
                if (userType == User.Type.CUSTOMER) {
                    laundryShopName = detail.getLaundryShop().getName();
                    List<LaundryAssignment> assignments = LaundryAssignment.find(LaundryAssignment.class, "m_Booking_Details_Id = ?", Long.toString(detail.getId()));
                    if (!assignments.isEmpty()) {
                        laundryShopName += ", " + assignments.get(0).getLaundryStaffName();
                    }
                } else {
                    User user = User.findById(User.class, detail.getUserId());
                    laundryShopName = user.getFullName();
                }
                statusLaundryShop.setText(laundryShopName);
                detail_id = detail.getId();
                statusListItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long id = Long.parseLong(((TextView) view.findViewById(R.id.laundry_shop_id)).getText().toString());
                        if (userType == User.Type.CUSTOMER) {
                            onCheckBookingStatus(id, StatusFragment.STATUS_LIST);
                        } else if (userType == User.Type.LAUNDRY_SHOP) {
                            updateLaundryScheduleDetailsLayout(id);
                        }
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
        laundryScheduleDetailsLayout.setVisibility(View.GONE);
        laundryCompletedLayout.setVisibility(View.GONE);
        if(isLaundryExists) {
            noLaundryLayout.setVisibility(View.GONE);
            if (laundryListLayoutList.getChildCount() == 1 && Utility.getUserType(mContext) == User.Type.CUSTOMER) {
                laundryListLayout.setVisibility(View.GONE);
                laundryProcessedLayout.setVisibility(View.VISIBLE);
                updateLaundryProcessedLayout(detail_id);
            } else {
                laundryListLayout.setVisibility(View.VISIBLE);
            }
        } else {
            noLaundryLayout.setVisibility(View.VISIBLE);
            noLaundryDetails.setText(R.string.no_laundry_for_the_week);
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

    private static void updateLaundryCompletedLayout(long id) {
        selectedBookingId = id;
        BookingDetails details = BookingDetails.findById(BookingDetails.class, id);
        LaundryShop shop = details.getLaundryShop();
        if (details != null && details.getStatus() == Status.COMPLETED) {
            noLaundryLayout.setVisibility(View.GONE);
            laundryProcessedLayout.setVisibility(View.GONE);
            laundryListLayout.setVisibility(View.GONE);
            laundryScheduleDetailsLayout.setVisibility(View.GONE);
            laundryCompletedLayout.setVisibility(View.VISIBLE);

            List<UserRating> userRating = UserRating.find(UserRating.class, "m_Schedule_Id = ?", Long.toString(id));
            if (userRating.isEmpty()) {
                ratingBar.setIsIndicator(false);
                ratingBar.setRating(shop.getRating());
                commentsEdittext.setText("");
                commentsEdittext.setEnabled(true);
                sendRatingButton.setVisibility(View.VISIBLE);
            } else {
                onRatingSent(userRating.get(0).getId());
            }
        }
    }

    public static void onRatingSent(long ratingId) {
        UserRating userRating = UserRating.findById(UserRating.class, ratingId);
        if (userRating != null) {
            ratingBar.setIsIndicator(true);
            ratingBar.setRating(userRating.getRating());
            commentsEdittext.setText(userRating.getComments());
            commentsEdittext.setEnabled(false);
            sendRatingButton.setVisibility(View.GONE);
        }
    }

    private static void updateLaundryProcessedLayout(long id) {
        selectedBookingId = id;
        BookingDetails details = BookingDetails.findById(BookingDetails.class, id);
        if (details != null) {
            Status status = details.getStatus();
            if (status == Status.NEW) {
                laundryProcessedStatus.setText(R.string.laundry_processed_new);
            } else if (status == Status.ACCEPTED) {
                laundryProcessedStatus.setText(R.string.laundry_processed_accepted);
            } else if (status == Status.PROCESSING) {
                laundryProcessedStatus.setText(R.string.laundry_processed_processing);
            } else if (status == Status.COMPLETED) {
                updateLaundryCompletedLayout(id);
            }

            String laundryShop = details.getLaundryShop().getName();
            String laundryService = details.getLaundryServiceName();
            long dateCreated = System.currentTimeMillis();
            long pickupDate = details.getPickupDate();
            long deliveryDate = details.getReturnDate();
            float fee = details.getFee();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma, MMMM dd, yyyy");
            List<LaundryAssignment> assignments = LaundryAssignment.find(LaundryAssignment.class, "m_Booking_Details_Id = ?", Long.toString(details.getId()));
            if (!assignments.isEmpty()) {
                laundryShop += ", " + assignments.get(0).getLaundryStaffName();
            }

            laundryProcessedTime.setText(Utility.getTimeDifference(mContext, dateCreated, deliveryDate));
            laundryProcessedShop.setText(laundryShop);
            laundryProcessedService.setText(laundryService);
            laundryProcessedPickup.setText(dateFormat.format(pickupDate));
            laundryProcessedDelivery.setText(dateFormat.format(deliveryDate));
            laundryProcessedFee.setText("P" + Float.toString(fee));
        }
    }

    public static void updateLaundrySchedule(long detailsId) {
        if (laundryScheduleDetailsLayout.getVisibility() == View.VISIBLE) {
            updateLaundryScheduleDetailsLayout(detailsId);
        } else if (laundryListLayout.getVisibility() == View.VISIBLE) {
            updateLaundryList();
        } else if (laundryProcessedLayout.getVisibility() == View.VISIBLE) {
            updateLaundryProcessedLayout(detailsId);
        }
    }

    public static void updateLaundryScheduleDetailsLayout(long detailsId) {
        selectedBookingId = detailsId;
        setCheckStatusFromScheduler(STATUS_LIST);
        final BookingDetails details = BookingDetails.findById(BookingDetails.class, detailsId);
        if (details == null) {
            return;
        }

        laundryScheduleDetailsLayout.setVisibility(View.VISIBLE);
        noLaundryLayout.setVisibility(View.GONE);
        laundryProcessedLayout.setVisibility(View.GONE);
        laundryListLayout.setVisibility(View.GONE);
        doneButton.setVisibility(View.GONE);
        scheduleFee.setVisibility(View.VISIBLE);
        feeEdittext.setVisibility(View.GONE);
        scheduleNoOfClothes.setVisibility(View.VISIBLE);
        noOfClothesEdittext.setVisibility(View.GONE);
        scheduleEstimatedKilo.setVisibility(View.VISIBLE);
        estimatedKiloEdittext.setVisibility(View.GONE);

        mListener.onViewLaundryScheduleDetails();

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma, MMMM dd, yyyy");
        scheduleCustomer.setText(details.getCustomerName());
        scheduleFee.setText("P" + details.getFee());
        scheduleMode.setText(details.getModeName());
        scheduleType.setText(details.getTypeName());
        scheduleService.setText(details.getLaundryServiceName());
        schedulePickupDate.setText(dateFormat.format(details.getPickupDate()));
        schedulePickupLocation.setText(details.getLocation());
        scheduleDeliveryDate.setText(dateFormat.format(details.getReturnDate()));
        scheduleDeliveryLocation.setText(details.getLocation());
        scheduleNoOfClothes.setText(details.getNoOfClothes() + "");
        scheduleEstimatedKilo.setText(details.getEstimatedKilo() + "kg");
        scheduleNotes.setText(details.getNotes());

        String location = details.getLaundryShop().getName();
        if (Utility.getUserType(mContext) == User.Type.LAUNDRY_SHOP) {
            List<LaundryAssignment> assignment = LaundryAssignment.find(LaundryAssignment.class, "m_Booking_Details_Id = ?", Long.toString(detailsId));
            if (!assignment.isEmpty()) {
                location += (", " + assignment.get(0).getLaundryStaffName());
            }
        }
        scheduleLocation.setText(location);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButton.setVisibility(View.GONE);
                doneButton.setVisibility(View.VISIBLE);

                scheduleFee.setVisibility(View.GONE);
                feeEdittext.setVisibility(View.VISIBLE);
                scheduleNoOfClothes.setVisibility(View.GONE);
                noOfClothesEdittext.setVisibility(View.VISIBLE);
                scheduleEstimatedKilo.setVisibility(View.GONE);
                estimatedKiloEdittext.setVisibility(View.VISIBLE);

                feeEdittext.setText(details.getFee() + "");
                noOfClothesEdittext.setText(details.getNoOfClothes() + "");
                estimatedKiloEdittext.setText(details.getEstimatedKilo() + "");
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float newFee, newEstimatedKilo;
                int newNoOfClothes;
                try {
                    newFee = Float.parseFloat(feeEdittext.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(mContext, "Fee input invalid.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    newNoOfClothes = Integer.parseInt(noOfClothesEdittext.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(mContext, "No. of clothes input invalid.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    newEstimatedKilo = Float.parseFloat(estimatedKiloEdittext.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(mContext, "Estimated kilo input invalid.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (details.getFee() == newFee && details.getNoOfClothes() == newNoOfClothes && details.getEstimatedKilo() == newEstimatedKilo) {
                    // do nothing
                } else {
                    Utility.sendEditDetailsThruSms(mContext, details, newFee, newNoOfClothes, newEstimatedKilo);
                }

                editButton.setVisibility(View.VISIBLE);
                doneButton.setVisibility(View.GONE);

                scheduleFee.setVisibility(View.VISIBLE);
                feeEdittext.setVisibility(View.GONE);
                scheduleNoOfClothes.setVisibility(View.VISIBLE);
                noOfClothesEdittext.setVisibility(View.GONE);
                scheduleEstimatedKilo.setVisibility(View.VISIBLE);
                estimatedKiloEdittext.setVisibility(View.GONE);

                feeEdittext.setText("");
                noOfClothesEdittext.setText("");
                estimatedKiloEdittext.setText("");

                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(laundryScheduleDetailsLayout.getWindowToken(), 0);
            }
        });

        Status status = details.getStatus();
        Log.d(TAG, "Status = " + status.name());
        if (status == Status.COMPLETED || status == Status.REJECTED) {
            editButton.setVisibility(View.GONE);
        } else {
            editButton.setVisibility(View.VISIBLE);
        }
        int yellow = ContextCompat.getColor(mContext, R.color.tubble_yellow);
        int white = ContextCompat.getColor(mContext, R.color.background_color_light);
        int gray = ContextCompat.getColor(mContext, R.color.disabled_button);
        if (status == Status.ACCEPTED) {
            editButton.setEnabled(true);
            laundryAcceptedBg.setBackgroundColor(yellow);
            processingBg.setBackgroundColor(white);
            returningBg.setBackgroundColor(white);
            laundryAcceptedButton.setEnabled(false);
            processingButton.setEnabled(true);
            returningButton.setEnabled(false);
        } else if (status == Status.PROCESSING) {
            editButton.setEnabled(true);
            laundryAcceptedBg.setBackgroundColor(yellow);
            processingBg.setBackgroundColor(yellow);
            returningBg.setBackgroundColor(white);
            laundryAcceptedButton.setEnabled(false);
            processingButton.setEnabled(false);
            returningButton.setEnabled(true);
        } else if (status == Status.COMPLETED) {
            editButton.setEnabled(false);
            laundryAcceptedBg.setBackgroundColor(yellow);
            processingBg.setBackgroundColor(yellow);
            returningBg.setBackgroundColor(yellow);
            laundryAcceptedButton.setEnabled(false);
            processingButton.setEnabled(false);
            returningButton.setEnabled(false);
        } else if (status == Status.NEW) {
            editButton.setEnabled(true);
            laundryAcceptedBg.setBackgroundColor(white);
            processingBg.setBackgroundColor(white);
            returningBg.setBackgroundColor(white);
            laundryAcceptedButton.setEnabled(true);
            processingButton.setEnabled(false);
            returningButton.setEnabled(false);
        } else if (status == Status.REJECTED) {
            editButton.setEnabled(false);
            laundryAcceptedBg.setBackgroundColor(gray);
            processingBg.setBackgroundColor(gray);
            returningBg.setBackgroundColor(gray);
            laundryAcceptedButton.setEnabled(false);
            processingButton.setEnabled(false);
            returningButton.setEnabled(false);
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
                                details.delete();
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
                    Utility.callLaundryShop(mContext, shop);
                }
                break;
            case R.id.laundry_accepted_button:
                BookingDetails details1 = BookingDetails.findById(BookingDetails.class, selectedBookingId);
                details1.setStatus(Status.ACCEPTED);
                Utility.sendLaundryStatusThruSms(mContext, details1, Status.ACCEPTED);
                laundryAcceptedBg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.tubble_yellow));
                laundryAcceptedBg.setEnabled(false);
                processingButton.setEnabled(true);
                break;
            case R.id.processing_button:
                final BookingDetails details2 = BookingDetails.findById(BookingDetails.class, selectedBookingId);
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.assign_laundry_dialog);

                final Spinner assigneeSpinner = (Spinner) dialog.findViewById(R.id.assignee_spinner);
                final List<LaundryShopStaff> staffList = LaundryShopStaff.find(LaundryShopStaff.class, "m_Laundry_Shop_Id = ?", Long.toString(details2.getLaundryShop().getId()));
                List<String> staffListString = new ArrayList<>();
                for (LaundryShopStaff staff : staffList) {
                    staffListString.add(staff.getName());
                }

                ArrayAdapter<String> data1Adapter = new ArrayAdapter<String>(mContext,
                        R.layout.simple_spinner_item, staffListString);
                data1Adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                assigneeSpinner.setAdapter(data1Adapter);

                Button closeBtn = (Button) dialog.findViewById(R.id.cancel_button);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                Button assignBtn = (Button) dialog.findViewById(R.id.assign_button);
                assignBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To do: Send assignee thry SMS
                        Utility.sendAssignLaundryProcessing(mContext, details2, staffList.get(assigneeSpinner.getSelectedItemPosition()));
//                        details2.setStatus(Status.PROCESSING);
//                        Utility.sendLaundryStatusThruSms(mContext, details2, Status.PROCESSING);
                        processingBg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.tubble_yellow));
                        processingButton.setEnabled(false);
                        returningButton.setEnabled(true);
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            case R.id.returning_button:
                BookingDetails details3 = BookingDetails.findById(BookingDetails.class, selectedBookingId);
                details3.setStatus(Status.COMPLETED);
                Utility.sendLaundryStatusThruSms(mContext, details3, Status.COMPLETED);
                returningBg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.tubble_yellow));
                returningButton.setEnabled(false);
                break;
            case R.id.send_rating_button:
                BookingDetails details4 = BookingDetails.findById(BookingDetails.class, selectedBookingId);
                Utility.sendRatingThruSms(mContext, details4, ratingBar.getRating(), commentsEdittext.getText().toString());
                // To do: Send comments/suggestions and rating to LSP via SMS
                break;
            default:
                break;
        }
    }

    public static void updateApprovedBookingsList() {
        setCheckStatusFromScheduler(APPROVED_STATUS_LIST);
        laundryListLayout.setVisibility(View.GONE);
        laundryProcessedLayout.setVisibility(View.GONE);
        laundryCompletedLayout.setVisibility(View.GONE);

        List<BookingDetails> approvedBookings = BookingDetails.find(BookingDetails.class, "m_User_Id = ? and m_Status = ?", Long.toString(Utility.getUserId(mContext)), Status.ACCEPTED.name());
        List<BookingDetails> deniedBookings = BookingDetails.find(BookingDetails.class, "m_User_Id = ? and m_Status = ?", Long.toString(Utility.getUserId(mContext)), Status.REJECTED.name());
        List<BookingDetails> processingBookings = BookingDetails.find(BookingDetails.class, "m_User_Id = ? and m_Status = ?", Long.toString(Utility.getUserId(mContext)), Status.PROCESSING.name());
        List<BookingDetails> completedBookings = BookingDetails.find(BookingDetails.class, "m_User_Id = ? and m_Status = ?", Long.toString(Utility.getUserId(mContext)), Status.COMPLETED.name());

        approvedBookings.addAll(processingBookings);
        approvedBookings.addAll(completedBookings);

        if (approvedBookings.isEmpty() && deniedBookings.isEmpty()) {
            noLaundryLayout.setVisibility(View.VISIBLE);
            noLaundryDetails.setText(R.string.no_approved_bookings);
            nextScheduleLayout.setVisibility(View.GONE);
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
