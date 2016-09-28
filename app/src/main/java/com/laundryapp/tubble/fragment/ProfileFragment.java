package com.laundryapp.tubble.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.laundryapp.tubble.R;
import com.laundryapp.tubble.Utility;
import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.User;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = this.getClass().getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View fragmentView;
    private EditText mMobileNumber, mFullName, mEmail;
    private ScrollView profileLayout;
    private RelativeLayout trackLayout;
    private LinearLayout noTrackLayout;
    private ListView trackListView;
    private Button trackHistoryButton;
    private User.Type userType;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        userType = Utility.getUserType(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        profileLayout = (ScrollView) fragmentView.findViewById(R.id.user_profile);
        trackLayout = (RelativeLayout) fragmentView.findViewById(R.id.track_history);
        noTrackLayout = (LinearLayout) fragmentView.findViewById(R.id.no_track);
        trackListView = (ListView) fragmentView.findViewById(R.id.track_history_list);
        mMobileNumber = (EditText) fragmentView.findViewById(R.id.mobile_number);
        mFullName = (EditText) fragmentView.findViewById(R.id.full_name);
        mEmail = (EditText) fragmentView.findViewById(R.id.email);
        trackHistoryButton = (Button) fragmentView.findViewById(R.id.track_history_button);

        String mobileNumber = "", fullName = "";
        List<BookingDetails> bookings = null;
        if (User.Type.CUSTOMER == userType) {
            User user = User.findById(User.class, Utility.getUserId(getContext()));
            mobileNumber = user.getMobileNumber();
            fullName = user.getFullName();
            mEmail.setText(user.getEmailAddress());
            mEmail.setVisibility(View.VISIBLE);
            bookings = BookingDetails.find(BookingDetails.class, "m_User_id = ?", Long.toString(Utility.getUserId(getContext())));
        } else if (User.Type.LAUNDRY_SHOP == userType) {
            LaundryShop shop = LaundryShop.findById(LaundryShop.class, Utility.getUserId(getContext()));
            mobileNumber = shop.getContact();
            fullName = shop.getName();
            mEmail.setVisibility(View.GONE);
            bookings = BookingDetails.find(BookingDetails.class, "m_Laundry_Shop_Id = ?", Long.toString(Utility.getUserId(getContext())));
        }
        mMobileNumber.setText(mobileNumber);
        mFullName.setText(fullName);
        mMobileNumber.setEnabled(false);
        mFullName.setEnabled(false);
        mEmail.setEnabled(false);
        profileLayout.setVisibility(View.VISIBLE);
        trackLayout.setVisibility(View.GONE);
        trackHistoryButton.setOnClickListener(this);
        Picasso.with(getContext()).load(R.drawable.nolaundry).into((ImageView) fragmentView.findViewById(R.id.no_track_image));

//        if (bookings.isEmpty()) {
//            trackHistoryButton.setEnabled(false);
//        } else {
//            trackHistoryButton.setEnabled(true);
            ArrayAdapter<BookingDetails> adapter = new TrackHistoryAdapter(getContext(), bookings);
            trackListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
//        }

        return fragmentView;
    }

    public boolean onBackPressed() {
        if (trackLayout != null && trackLayout.getVisibility() == View.VISIBLE) {
            trackLayout.setVisibility(View.GONE);
            profileLayout.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.track_history_button:
                setTrackHistoryVisibility(true);
                break;
            default:
                break;
        }
    }

    private void setTrackHistoryVisibility(boolean isVisible) {
        if (isVisible) {
            profileLayout.setVisibility(View.GONE);
            trackLayout.setVisibility(View.VISIBLE);
            List<BookingDetails> bookings = null;
            if (Utility.getUserType(getContext()) == User.Type.CUSTOMER) {
                bookings = BookingDetails.find(BookingDetails.class, "m_User_id = ?", String.valueOf(Utility.getUserId(getContext())));
            } else if (Utility.getUserType(getContext()) == User.Type.LAUNDRY_SHOP) {
                bookings = BookingDetails.find(BookingDetails.class, "m_Laundry_Shop_Id = ?", String.valueOf(Utility.getUserId(getContext())));
            }

            if (bookings == null || bookings.isEmpty()) {
                noTrackLayout.setVisibility(View.VISIBLE);
                trackListView.setVisibility(View.GONE);
            } else {
                noTrackLayout.setVisibility(View.GONE);
                trackListView.setVisibility(View.VISIBLE);
            }
        } else {
            profileLayout.setVisibility(View.VISIBLE);
            trackLayout.setVisibility(View.GONE);
        }
    }

    class TrackHistoryAdapter extends ArrayAdapter<BookingDetails> {
        List<BookingDetails> bookings;
        User.Type userType;

        private class ViewHolder {
            TextView laundryShop;
            TextView dateCreated;
            TextView serviceType;
            TextView mode;
            TextView fee;
        }

        public TrackHistoryAdapter(Context context, List<BookingDetails> bookings) {
            super(context, R.layout.track_list_item, bookings);
            this.bookings = bookings;
            userType = Utility.getUserType(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BookingDetails details = bookings.get(position);
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.track_list_item, parent, false);
                viewHolder.laundryShop = (TextView) convertView.findViewById(R.id.laundry_shop);
                viewHolder.dateCreated = (TextView) convertView.findViewById(R.id.date_created);
                viewHolder.serviceType = (TextView) convertView.findViewById(R.id.service_type);
                viewHolder.mode = (TextView) convertView.findViewById(R.id.mode);
                viewHolder.fee = (TextView) convertView.findViewById(R.id.fee);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (User.Type.CUSTOMER == userType) {
                viewHolder.laundryShop.setText(details.getLaundryShop().getName());
            } else if (User.Type.LAUNDRY_SHOP == userType) {
                viewHolder.laundryShop.setText(User.findById(User.class, details.getUserId()).getFullName());
            }

            viewHolder.dateCreated.setText(details.getDateTimeCreated());
            viewHolder.serviceType.setText(details.getTypeName() + " - " + details.getLaundryServiceName());
            viewHolder.mode.setText(details.getModeName());
            viewHolder.fee.setText("Fee: " + details.getFee());
            return convertView;
        }
    }
}
