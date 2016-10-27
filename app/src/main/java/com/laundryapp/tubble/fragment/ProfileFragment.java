package com.laundryapp.tubble.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.laundryapp.tubble.R;
import com.laundryapp.tubble.Utility;
import com.laundryapp.tubble.entities.BookingDetails;
import com.laundryapp.tubble.entities.LaundryService;
import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.LaundryShopService;
import com.laundryapp.tubble.entities.User;
import com.laundryapp.tubble.entities.UserRating;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private final static String TAG = "ProfileFragment";
    private final static int IMG_RESULT = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static OnFragmentInteractionListener mListener;

    private View fragmentView;
    private EditText mMobileNumber, mFullName, mEmail;
    private static ScrollView profileLayout, shopLayout;
    private static RelativeLayout trackLayout;
    private LinearLayout noTrackLayout;
    private ListView trackListView;
    private Button trackHistoryButton, viewAddressButton;
    private User.Type userType;
    private ImageView selectPhotoButton, takePhotoButton;
    private CircleImageView userPhoto;
    private String imageDecode;

    /* Shop Layout */
    private LinearLayout shopInfoLinearLayout;
    private Button shopTrackHistoryButton;
    private ImageButton websiteButton, callButton;
    private TextView shopName, shopSchedule, disclaimer;
    private RatingBar laundryRating;
    private TableLayout servicesTable;

    private static Context mContext;


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
        shopLayout = (ScrollView) fragmentView.findViewById(R.id.shop_layout);
        trackLayout = (RelativeLayout) fragmentView.findViewById(R.id.track_history);
        noTrackLayout = (LinearLayout) fragmentView.findViewById(R.id.no_track);

        /* Track Layout */
        trackListView = (ListView) fragmentView.findViewById(R.id.track_history_list);

        /* Profile Layout */
        mMobileNumber = (EditText) fragmentView.findViewById(R.id.mobile_number);
        mFullName = (EditText) fragmentView.findViewById(R.id.full_name);
        mEmail = (EditText) fragmentView.findViewById(R.id.email);
        trackHistoryButton = (Button) fragmentView.findViewById(R.id.track_history_button);
        viewAddressButton = (Button) fragmentView.findViewById(R.id.view_address_button);
        selectPhotoButton = (ImageView) fragmentView.findViewById(R.id.select_photo_button);
        takePhotoButton = (ImageView) fragmentView.findViewById(R.id.take_photo_button);
        userPhoto = (CircleImageView) fragmentView.findViewById(R.id.profile_image);

        /* Shop Layout */
        shopInfoLinearLayout = (LinearLayout) shopLayout.findViewById(R.id.shop_info_layout);
        shopTrackHistoryButton = (Button) shopLayout.findViewById(R.id.track_history_button);
        websiteButton = (ImageButton) shopLayout.findViewById(R.id.website_button);
        callButton = (ImageButton) shopLayout.findViewById(R.id.call_button);
        shopName = (TextView) shopLayout.findViewById(R.id.shop_name);
        shopSchedule = (TextView) shopLayout.findViewById(R.id.shop_schedule);
        disclaimer = (TextView) shopLayout.findViewById(R.id.disclaimer);
        laundryRating = (RatingBar) shopLayout.findViewById(R.id.laundry_rating);
        servicesTable = (TableLayout) shopLayout.findViewById(R.id.services_table);

        String photo = "";
        List<BookingDetails> bookings = null;
        if (User.Type.CUSTOMER == userType) {
            User user = User.findById(User.class, Utility.getUserId(getContext()));
            photo = user.getUserPhoto();
            mEmail.setText(user.getEmailAddress());
            mFullName.setText(user.getFullName());
            mMobileNumber.setText(user.getMobileNumber());
            bookings = BookingDetails.find(BookingDetails.class, "m_User_id = ?", Long.toString(Utility.getUserId(getContext())));
            profileLayout.setVisibility(View.VISIBLE);
            shopLayout.setVisibility(View.GONE);
        } else if (User.Type.LAUNDRY_SHOP == userType) {
            LaundryShop shop = LaundryShop.findById(LaundryShop.class, Utility.getUserId(getContext()));
            bookings = BookingDetails.find(BookingDetails.class, "m_Laundry_Shop_Id = ?", Long.toString(Utility.getUserId(getContext())));
            shopName.setText(shop.getName());
            shopSchedule.setText(shop.getSchedule());
            disclaimer.setText(shop.getAddress());
            laundryRating.setRating(shop.getRating());
            updateServicesTable(shop);
            profileLayout.setVisibility(View.GONE);
            shopLayout.setVisibility(View.VISIBLE);
        }

        if (photo == null || photo.equals("")) {
            userPhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.userphoto));
            userPhoto.setBorderWidth(0);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(photo);
            if (bitmap != null) {
                userPhoto.setImageBitmap(bitmap);
                userPhoto.setBorderWidth(20);
            } else {
                userPhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.userphoto));
                userPhoto.setBorderWidth(0);
            }
        }

        trackLayout.setVisibility(View.GONE);
        websiteButton.setVisibility(View.GONE);
        callButton.setVisibility(View.GONE);
        shopInfoLinearLayout.setVisibility(View.VISIBLE);
        viewAddressButton.setOnClickListener(this);
        trackHistoryButton.setOnClickListener(this);
        selectPhotoButton.setOnClickListener(this);
        takePhotoButton.setOnClickListener(this);
        shopTrackHistoryButton.setOnClickListener(this);
        Picasso.with(getContext()).load(R.drawable.nolaundry).into((ImageView) fragmentView.findViewById(R.id.no_track_image));

        ArrayAdapter<BookingDetails> adapter = new TrackHistoryAdapter(getContext(), bookings);
        trackListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return fragmentView;
    }

    private void updateServicesTable(LaundryShop shop) {
        List<LaundryShopService> shopServices = LaundryShopService.find(LaundryShopService.class, "m_Laundry_Shop_Id = ?", Long.toString(shop.getId()));
        if (!shopServices.isEmpty()) {
            for (LaundryShopService service : shopServices) {
                TableRow row = new TableRow(mContext);
                LaundryService laundryService = LaundryService.findById(LaundryService.class, service.getLaundryServiceId());
                TextView serviceName = new TextView(mContext);
                serviceName.setText(laundryService.getLabel());
                serviceName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView servicePrice = new TextView(mContext);
                servicePrice.setText("P " + service.getPrice() + "/kilo");
                servicePrice.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                row.addView(serviceName);
                row.addView(servicePrice);
                servicesTable.addView(row);
            }
        }

    }

    public static boolean onBackPressed() {
        User.Type userType = Utility.getUserType(mContext);
        if (isTrackHistoryVisible()) {
            trackLayout.setVisibility(View.GONE);
            if (userType == User.Type.CUSTOMER) {
                profileLayout.setVisibility(View.VISIBLE);
            } else {
                shopLayout.setVisibility(View.VISIBLE);
            }
            mListener.updateBackButtonVisibility();
            return true;
        }
        return false;
    }

    public static boolean isTrackHistoryVisible() {
        if (trackLayout != null && trackLayout.getVisibility() == View.VISIBLE) {
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
        void updateBackButtonVisibility();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_address_button:
                if (userType == User.Type.CUSTOMER) {
                    User user = User.findById(User.class, Utility.getUserId(mContext));
                    final Dialog dialog = new Dialog(mContext);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.view_address_dialog);
                    TextView addressText = (TextView) dialog.findViewById(R.id.address_text);
                    addressText.setText(user.getAddress());
                    Button closeBtn = (Button) dialog.findViewById(R.id.close_button);
                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
                break;
            case R.id.track_history_button:
                setTrackHistoryVisibility(true);
                break;
            case R.id.select_photo_button:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMG_RESULT);
                break;
            case R.id.take_photo_button:
                if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = Utility.createImageFile(getActivity());
                            imageDecode = photoFile.getPath();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }

                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, Utility.CAPTURE_IMAGE_RESULT);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "No camera detected.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void setTrackHistoryVisibility(boolean isVisible) {
        if (isVisible) {
            profileLayout.setVisibility(View.GONE);
            shopLayout.setVisibility(View.GONE);
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
            if (userType == User.Type.CUSTOMER) {
                profileLayout.setVisibility(View.VISIBLE);
            } else {
                shopLayout.setVisibility(View.VISIBLE);
            }
            trackLayout.setVisibility(View.GONE);
        }
        mListener.updateBackButtonVisibility();
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
            TextView laundryStatus;
            RatingBar ratingBar;
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
                viewHolder.laundryStatus = (TextView) convertView.findViewById(R.id.laundry_status);
                viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
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
            String status = details.getStatus().name().toLowerCase();
            status = Character.toString(status.charAt(0)).toUpperCase() + status.substring(1);
            viewHolder.laundryStatus.setText(status);

            List<UserRating> userRating = UserRating.find(UserRating.class, "m_Schedule_Id = ?", Long.toString(details.getId()));
            if (userRating.isEmpty()) {
                viewHolder.ratingBar.setVisibility(View.GONE);
            } else {
                viewHolder.ratingBar.setVisibility(View.VISIBLE);
                viewHolder.ratingBar.setRating(userRating.get(0).getRating());
            }

            return convertView;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == IMG_RESULT && resultCode == Activity.RESULT_OK && data != null) {
                Uri URI = data.getData();
                String[] FILE = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContext().getContentResolver().query(URI, FILE, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(FILE[0]);
                imageDecode = cursor.getString(columnIndex);
                cursor.close();
                userPhoto.setImageBitmap(BitmapFactory.decodeFile(imageDecode));
                User user = User.findById(User.class, Utility.getUserId(getContext()));
                user.setUserPhoto(imageDecode);
                user.save();
            } else if (requestCode == Utility.CAPTURE_IMAGE_RESULT && resultCode == Activity.RESULT_OK) {
                Utility.scaleAndRotateImage(userPhoto, imageDecode);
                Utility.savePicToGallery(getActivity(), imageDecode);
                User user = User.findById(User.class, Utility.getUserId(getContext()));
                user.setUserPhoto(imageDecode);
                user.save();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
