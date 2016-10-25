package com.laundryapp.tubble.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laundryapp.tubble.R;
import com.laundryapp.tubble.fragment.tipsfragment.ContactUsFragment;
import com.laundryapp.tubble.fragment.tipsfragment.TubbleInfoFragment;
import com.laundryapp.tubble.fragment.tipsfragment.VideoFragment;

import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TipsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TipsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TipsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = this.getClass().getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View fragmentView;
    private ViewPager viewPager;
    private TipsFragmentAdapter pagerAdapter;
    private TabLayout tabLayout;

    private OnFragmentInteractionListener mListener;

    public TipsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TipsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TipsFragment newInstance(String param1, String param2) {
        TipsFragment fragment = new TipsFragment();
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
        fragmentView = inflater.inflate(R.layout.fragment_tips, container, false);
        viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) fragmentView.findViewById(R.id.tablayout);
        new Thread(new Runnable() {
            @Override
            public void run() {
                pagerAdapter = new TipsFragmentAdapter(getContext(), getChildFragmentManager());
                fragmentView.post(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setOffscreenPageLimit(1);
                        viewPager.setAdapter(pagerAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                        for (int i=0; i<3; i++) {
                            tabLayout.getTabAt(i).setIcon(R.drawable.tips_tab_icon_selector);
                        }

                        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                viewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {

                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {

                            }
                        });
                    }
                });
            }
        }).start();

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
}

class TipsFragmentAdapter extends FragmentPagerAdapter {
    Context context;

    public TipsFragmentAdapter (Context context, FragmentManager fm) {
        super(fm);
        this.context = context;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TubbleInfoFragment();
            case 1:
                return new VideoFragment();
            case 2:
                return new ContactUsFragment();
            default:
                return new TubbleInfoFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
