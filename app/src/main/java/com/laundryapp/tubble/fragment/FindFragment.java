package com.laundryapp.tubble.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.laundryapp.tubble.R;
import com.laundryapp.tubble.entities.LaundryShop;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnInfoWindowClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = this.getClass().getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FrameLayout mapLayout;
    private LinearLayout infoLayout;

    private GoogleMap myMap;
    private MapView mMapView;
    private List<Marker> mapMarkers = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public FindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();
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
        final View view = inflater.inflate(R.layout.fragment_find, container, false);

        mapLayout = (FrameLayout) view.findViewById(R.id.map_layout);
        infoLayout = (LinearLayout) view.findViewById(R.id.shop_info_layout);

        try {
            MapsInitializer.initialize(this.getActivity());
            mMapView = (MapView) view.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);

        } catch (Exception e) {
            Log.e("Map", e.getMessage(), e);
        }

        return view;
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

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            myMap.setMyLocationEnabled(true);
        }

        // default position
        double lat = 14.6430239;
        double lng = 121.0660785;

        LatLng change = new LatLng(lat, lng);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(change));
        myMap.setOnMyLocationButtonClickListener(this);
        myMap.setOnInfoWindowClickListener(this);

        setUpMarkers();

        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                // Getting view from the layout file info_window_layout
                View v = getActivity().getLayoutInflater().inflate(R.layout.marker_info_layout, null);

                // Getting reference to the TextView to set latitude
                TextView shopName = (TextView) v.findViewById(R.id.laundry_shop_name_text);
                RatingBar laundryRating = (RatingBar) v.findViewById(R.id.laundry_rating);
                ImageButton infoButton = (ImageButton) v.findViewById(R.id.info_button);

                shopName.setText(marker.getTitle());
                LaundryShop shop = LaundryShop.find(LaundryShop.class, "m_Name = ?", marker.getTitle()).get(0);

                laundryRating.setRating(shop.getRating());

                // Returning the view containing InfoWindow contents
                return v;

            }
        });
    }

    public void setUpMarkers() {
        for (Marker marker : mapMarkers) {
            marker.remove();
        }

        mapMarkers = new ArrayList<>();
        List<MarkerOptions> markerOptionses = new ArrayList<>();

        List<LaundryShop> shopList = LaundryShop.listAll(LaundryShop.class);

        for (LaundryShop shop : shopList) {
            Log.e("Shops", shop.getName() + " " + shop.getAddress());

            double[] coords = shop.getLocationCoordinates();

            Log.e("ShopCoords", "Coords = " + coords[0] + " , " + coords[1]);
            MarkerOptions marker = new MarkerOptions().position(new LatLng(coords[0], coords[1]))
                    .title(shop.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.find));

            markerOptionses.add(marker);
        }

        for (MarkerOptions options : markerOptionses) {
            mapMarkers.add(myMap.addMarker(options));
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.e("wew", "infoclick");
        LaundryShop shop = LaundryShop.find(LaundryShop.class, "m_Name = ?", marker.getTitle()).get(0);

        mapLayout.setVisibility(View.GONE);
        infoLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (infoLayout.getVisibility() == View.VISIBLE) {
                infoLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onBackPressed() {
        if (infoLayout.getVisibility() == View.VISIBLE) {
            infoLayout.setVisibility(View.GONE);
            mapLayout.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
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
}
