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

import org.w3c.dom.Text;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View laundryRequestFragmentLayout;
    private LinearLayout laundryRequestLayout, noLaundryRequestLayout;
    private SwipeFlingAdapterView flingContainer;
    private FlingContainerAdapter flingAdapter;
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
        LaundryShop laundryShop = LaundryShop.findById(LaundryShop.class, Utility.getUserId(getContext()));
        final List<BookingDetails> bookings = BookingDetails.find(BookingDetails.class, "m_Laundry_Shop_Id = ? and m_Status = ?", laundryShop.getId().toString(), BookingDetails.Status.NEW.toString());

        laundryRequestLayout.setVisibility(bookings.isEmpty() ? View.GONE : View.VISIBLE);
        noLaundryRequestLayout.setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);

        Log.d("Sarah", "Bookings size: " + bookings.size());
        if (!bookings.isEmpty()) {
            flingContainer = (SwipeFlingAdapterView) laundryRequestLayout.findViewById(R.id.fling_container);
//            ArrayList<String> array = new ArrayList<String>();
//            for (BookingDetails booking: bookings) {
//                array.add(booking.getCustomerName());
//            }
//            flingAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, array);
            flingAdapter = new FlingContainerAdapter(getContext(), bookings);
            Log.d("Sarah", "flingAdapter = " + flingAdapter);
            Log.d("Sarah", "flingAdapter size: " + flingAdapter.getCount());
            flingContainer.setAdapter(flingAdapter);
            Log.d("Sarah", "flingAdapter size: " + flingContainer.getAdapter().getCount());
            flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    if (!bookings.isEmpty()) {
                        bookings.remove(0);
                        flingAdapter.notifyDataSetChanged();
                        if (bookings.isEmpty()) {
                            noLaundryRequestLayout.setVisibility(View.VISIBLE);
                            laundryRequestLayout.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onLeftCardExit(Object o) {
//                    bookings.get(0).setStatus(BookingDetails.Status.REJECTED);
                    Toast.makeText(getContext(), "Left!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRightCardExit(Object o) {
//                    bookings.get(0).setStatus(BookingDetails.Status.ACCEPTED);
                    Toast.makeText(getContext(), "Right!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdapterAboutToEmpty(int i) {

                }

                @Override
                public void onScroll(float v) {

                }
            });
        }

        return laundryRequestFragmentLayout;
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