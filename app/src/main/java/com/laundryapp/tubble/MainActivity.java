package com.laundryapp.tubble;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.laundryapp.tubble.entities.User;
import com.laundryapp.tubble.fragment.FindFragment;
import com.laundryapp.tubble.fragment.ProfileFragment;
import com.laundryapp.tubble.fragment.SchedulerFragment;
import com.laundryapp.tubble.fragment.StatusFragment;
import com.laundryapp.tubble.fragment.TipsFragment;
import com.squareup.picasso.Picasso;

public class MainActivity extends FragmentActivity implements FindFragment.OnFragmentInteractionListener, SchedulerFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, StatusFragment.OnFragmentInteractionListener, TipsFragment.OnFragmentInteractionListener {

    private final String TAG = this.getClass().getName();
    private TabPagerAdapter mTabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        long user_id = getIntent().getLongExtra(User.USER_ID, -1);
        Log.d(TAG, "user id: " + user_id);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        Picasso.with(getApplicationContext()).load(R.drawable.tubblelogo).into((ImageView) mToolbar.findViewById(R.id.tubble_logo));

        mTabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mTabPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.selector_find);
        mTabLayout.getTabAt(1).setIcon(R.drawable.selector_scheduler);
        mTabLayout.getTabAt(2).setIcon(R.drawable.selector_status);
        mTabLayout.getTabAt(3).setIcon(R.drawable.selector_tips);
        mTabLayout.getTabAt(4).setIcon(R.drawable.selector_profile);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(mTabLayout.getSelectedTabPosition());
//                if (mTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition()) instanceof SchedulerFragment) {
//                    SchedulerFragment.updateCalendarAdapter();
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.laundryapp.tubble/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.laundryapp.tubble/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onBackPressed() {
        boolean onBackPressed = false;
        if (mViewPager.getCurrentItem() == 1) { // Scheduler Fragment
            onBackPressed = ((SchedulerFragment) mTabPagerAdapter.getItem(1)).onBackPressed();
        } else if (mViewPager.getCurrentItem() == 4) {  // Profile Fragment
            onBackPressed = ((ProfileFragment) mTabPagerAdapter.getItem(4)).onBackPressed();
        }

        if (!onBackPressed) {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

class TabPagerAdapter extends FragmentPagerAdapter {
    FindFragment mFindFragment;
    SchedulerFragment mSchedulerFragment;
    StatusFragment mStatusFragment;
    TipsFragment mTipsFragment;
    ProfileFragment mProfileFragment;

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
        mFindFragment = new FindFragment();
        mSchedulerFragment = new SchedulerFragment();
        mStatusFragment = new StatusFragment();
        mTipsFragment = new TipsFragment();
        mProfileFragment = new ProfileFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mFindFragment;
            case 1:
                return mSchedulerFragment;
            case 2:
                return mStatusFragment;
            case 3:
                return mTipsFragment;
            case 4:
                return mProfileFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

}