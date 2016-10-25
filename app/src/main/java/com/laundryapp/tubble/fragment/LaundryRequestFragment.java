package com.laundryapp.tubble.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.laundryapp.tubble.R;
import com.laundryapp.tubble.Utility;
import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.LaundryShop;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LaundryRequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LaundryRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LaundryRequestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "LaundryRequestFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View laundryRequestFragmentLayout;
    private static LinearLayout laundryRequestLayout, noLaundryRequestLayout;
    private static SwipeFlingAdapterView flingContainer;
    private static FlingContainerAdapter flingAdapter;
    private static List<BookingDetails> bookingsList;
//    private ArrayAdapter<String> flingAdapter;

    private OnFragmentInteractionListener mListener;

    public LaundryRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LaundryRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LaundryRequestFragment newInstance(String param1, String param2) {
        LaundryRequestFragment fragment = new LaundryRequestFragment();
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

        laundryRequestFragmentLayout = inflater.inflate(R.layout.fragment_laundry_request, container, false);
        laundryRequestLayout = (LinearLayout) laundryRequestFragmentLayout.findViewById(R.id.laundry_request_layout);
        noLaundryRequestLayout = (LinearLayout) laundryRequestFragmentLayout.findViewById(R.id.no_laundry_request);

        updateBookingsList(getContext());

        return laundryRequestFragmentLayout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public static void updateBookingsList(final Context context) {
        LaundryShop laundryShop = LaundryShop.findById(LaundryShop.class, Utility.getUserId(context));
        bookingsList = Select.from(BookingDetails.class).where(Condition.prop("m_Laundry_Shop_Id").eq(laundryShop.getId().toString()), Condition.prop("m_Status").eq(BookingDetails.Status.NEW.toString())).orderBy("m_Return_Date").list();

        laundryRequestLayout.setVisibility(bookingsList.isEmpty() ? View.GONE : View.VISIBLE);
        noLaundryRequestLayout.setVisibility(bookingsList.isEmpty() ? View.VISIBLE : View.GONE);

        if (!bookingsList.isEmpty()) {
            flingContainer = (SwipeFlingAdapterView) laundryRequestLayout.findViewById(R.id.fling_container);
            flingAdapter = new FlingContainerAdapter(context, bookingsList);
            flingContainer.setAdapter(flingAdapter);
            flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                BookingDetails details;

                @Override
                public void removeFirstObjectInAdapter() {
                    if (!bookingsList.isEmpty()) {
                        details = bookingsList.get(0);
                        bookingsList.remove(0);
                        flingAdapter.notifyDataSetChanged();
                        if (bookingsList.isEmpty()) {
                            noLaundryRequestLayout.setVisibility(View.VISIBLE);
                            laundryRequestLayout.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onLeftCardExit(Object o) {
                    if (details != null) {
                        Utility.sendLaundryStatusThruSms(context, details, BookingDetails.Status.REJECTED);
                        Toast.makeText(context, "Denied!", Toast.LENGTH_SHORT).show();
                        details = null;
                    }
                }

                @Override
                public void onRightCardExit(Object o) {
                    if (details != null) {
                        Utility.sendLaundryStatusThruSms(context, details, BookingDetails.Status.ACCEPTED);
                        Toast.makeText(context, "Accepted!", Toast.LENGTH_SHORT).show();
                        details = null;
                    }
                }

                @Override
                public void onAdapterAboutToEmpty(int i) {

                }

                @Override
                public void onScroll(float v) {

                }
            });
        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

class FlingContainerAdapter extends ArrayAdapter {

    private List<BookingDetails> bookings;
    private Context context;

    public FlingContainerAdapter(Context context, List<BookingDetails> bookings) {
        super(context, R.layout.laundry_request_item);
        this.context = context;
        this.bookings = bookings;
    }

    public void setBookings(List<BookingDetails> bookings) {
        this.bookings = bookings;
    }

    public class ViewHolder {
        TextView customerName, fee, mode, type, location, service, pickupDate, pickupLocation, deliveryDate, deliveryLocation, noOfClothes, estimatedKilo, notes;
    }

    @Override
    public int getCount() {
        return bookings.size();
    }

    @Override
    public Object getItem(int position) {
        if (bookings.isEmpty()) {
            return null;
        }
        return bookings.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.laundry_request_item, parent, false);
            viewHolder.customerName = (TextView) convertView.findViewById(R.id.customer_name);
            viewHolder.fee = (TextView) convertView.findViewById(R.id.fee);
            viewHolder.mode = (TextView) convertView.findViewById(R.id.mode);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            viewHolder.location = (TextView) convertView.findViewById(R.id.location);
            viewHolder.service = (TextView) convertView.findViewById(R.id.service);
            viewHolder.pickupDate = (TextView) convertView.findViewById(R.id.pickup_datetime);
            viewHolder.pickupLocation = (TextView) convertView.findViewById(R.id.pickup_location);
            viewHolder.deliveryDate = (TextView) convertView.findViewById(R.id.delivery_datetime);
            viewHolder.deliveryLocation = (TextView) convertView.findViewById(R.id.delivery_location);
            viewHolder.noOfClothes = (TextView) convertView.findViewById(R.id.no_of_clothes);
            viewHolder.estimatedKilo = (TextView) convertView.findViewById(R.id.estimated_kilo);
            viewHolder.notes = (TextView) convertView.findViewById(R.id.notes);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final BookingDetails details = bookings.get(position);
        viewHolder.customerName.setText(details.getCustomerName());
        viewHolder.fee.setText("Fee: " + details.getFee());
        viewHolder.mode.setText(details.getModeName());
        viewHolder.type.setText(details.getTypeName());
        viewHolder.location.setText(details.getLaundryShop().getName());
        viewHolder.service.setText(details.getLaundryServiceName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma MMMM dd, yyyy");
        viewHolder.pickupDate.setText(dateFormat.format(details.getPickupDate()));
        viewHolder.pickupLocation.setText(details.getLocation());
        viewHolder.deliveryDate.setText(dateFormat.format(details.getReturnDate()));
        viewHolder.deliveryLocation.setText(details.getLocation());
        viewHolder.noOfClothes.setText(Integer.toString(details.getNoOfClothes()));
        viewHolder.estimatedKilo.setText(details.getEstimatedKilo() + "kg");
        viewHolder.notes.setText(details.getNotes());

        return convertView;
    }
}