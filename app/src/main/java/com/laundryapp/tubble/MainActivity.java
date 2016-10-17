package com.laundryapp.tubble;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.User;
import com.laundryapp.tubble.fragment.FindFragment;
import com.laundryapp.tubble.fragment.LaundryRequestFragment;
import com.laundryapp.tubble.fragment.ProfileFragment;
import com.laundryapp.tubble.fragment.SchedulerFragment;
import com.laundryapp.tubble.fragment.StatusFragment;
import com.laundryapp.tubble.fragment.TipsFragment;

public class MainActivity extends FragmentActivity implements
        FindFragment.OnFragmentInteractionListener, SchedulerFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, StatusFragment.OnFragmentInteractionListener,
        TipsFragment.OnFragmentInteractionListener, LaundryRequestFragment.OnFragmentInteractionListener {

    private final String TAG = this.getClass().getName();
    private TabPagerAdapter mTabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private MenuItem menuSearch, menuLogout, menuStatus, menuCancel;

    public static final String USER_ID = "user_id";

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
        User.Type userType = Utility.getUserType(getApplicationContext());
        if (User.Type.CUSTOMER == userType) {
            User user = User.findById(User.class, user_id);
        } else if (User.Type.LAUNDRY_SHOP == userType) {
            LaundryShop shop = LaundryShop.findById(LaundryShop.class, user_id);
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setActionBar(mToolbar);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        initializeFragments();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initializeFragments() {
        mTabPagerAdapter = new TabPagerAdapter(getApplicationContext(), getSupportFragmentManager());
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
                if (mTabLayout.getSelectedTabPosition() == 2) { // Status Fragment
                    if (StatusFragment.getCheckStatusFromScheduler() == StatusFragment.SCHEDULER) {

                    } else {
                        StatusFragment.updateLaundryList();
                    }
                }
                updateOptionsMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuSearch = menu.findItem(R.id.action_search);
        menuLogout = menu.findItem(R.id.action_logout);
        menuStatus = menu.findItem(R.id.action_status);
        menuCancel = menu.findItem(R.id.action_cancel);
        updateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    private void updateOptionsMenu() {
        int currentTab = mViewPager.getCurrentItem();
        menuSearch.setVisible(currentTab == 0);     // Find Fragment
        menuStatus.setVisible(currentTab == 2);     // Status Fragment
        menuLogout.setVisible(currentTab == 4);     // Profile Fragment
        menuCancel.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Utility.logout(this);
                return true;
            case R.id.action_search:
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.search_dialog);

                ImageButton closeBtn = (ImageButton) dialog.findViewById(R.id.close_btn);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();
                return true;
            case R.id.action_status:
                StatusFragment.updateApprovedBookingsList();
                menuStatus.setVisible(false);
                menuCancel.setVisible(true);
                return true;
            case R.id.action_cancel:
                StatusFragment.updateLaundryList();
                menuStatus.setVisible(true);
                menuCancel.setVisible(false);
            default:
                return super.onOptionsItemSelected(item);
        }
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
//            onBackPressed = ((SchedulerFragment) mTabPagerAdapter.getItem(1)).onBackPressed();
            onBackPressed = SchedulerFragment.onBackPressed();
        } else if (mViewPager.getCurrentItem() == 2) { // Status Fragment
            if (StatusFragment.getCheckStatusFromScheduler() == StatusFragment.APPROVED_STATUS_LIST) {
                StatusFragment.setCheckStatusFromScheduler(StatusFragment.DEFAULT);
                StatusFragment.updateLaundryList();
                menuStatus.setVisible(true);
                menuCancel.setVisible(false);
                onBackPressed = true;
            } else if (StatusFragment.getCheckStatusFromScheduler() == StatusFragment.SCHEDULER) {
                StatusFragment.setCheckStatusFromScheduler(StatusFragment.DEFAULT);
                mViewPager.setCurrentItem(1);   // go back to Scheduler Fragment
                onBackPressed = true;
            } else if (StatusFragment.getCheckStatusFromScheduler() == StatusFragment.STATUS_LIST) {
                StatusFragment.setCheckStatusFromScheduler(StatusFragment.DEFAULT);
                StatusFragment.updateLaundryList();
                onBackPressed = true;
            }
        } else if (mViewPager.getCurrentItem() == 4) {  // Profile Fragment
            onBackPressed = ProfileFragment.onBackPressed();
        }

        if (!onBackPressed) {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCheckBookingStatus(long id) {
        StatusFragment.onCheckBookingStatus(id, StatusFragment.SCHEDULER);
        mViewPager.setCurrentItem(2);
    }

    @Override
    public void showCreateBookingPage() {
        SchedulerFragment.setCreateBookingVisible();
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void onAddOrDeleteLaundrySchedule() {
//        ((StatusFragment) mTabPagerAdapter.getItem(2)).updateLaundryList();
        StatusFragment.updateLaundryList();
    }
}

class TabPagerAdapter extends FragmentPagerAdapter {
//    FindFragment mFindFragment;
//    LaundryRequestFragment mLaundryRequestFragment;
//    SchedulerFragment mSchedulerFragment;
//    StatusFragment mStatusFragment;
//    TipsFragment mTipsFragment;
//    ProfileFragment mProfileFragment;
    User.Type userType;

    public TabPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        userType = Utility.getUserType(context);
//        if (User.Type.CUSTOMER == userType) {
//            mFindFragment = new FindFragment();
//        } else if (User.Type.LAUNDRY_SHOP == userType) {
//            mLaundryRequestFragment = new LaundryRequestFragment();
//        }
//        mSchedulerFragment = new SchedulerFragment();
//        mStatusFragment = new StatusFragment();
//        mTipsFragment = new TipsFragment();
//        mProfileFragment = new ProfileFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new SchedulerFragment();
            case 2:
                return new StatusFragment();
            case 3:
                return new TipsFragment();
            case 4:
                return new ProfileFragment();
            case 0:
            default:    // for instances that there is no tab selected, return the 1st tab by default
                if (User.Type.CUSTOMER == userType) {
                    return new FindFragment();
                } else if (User.Type.LAUNDRY_SHOP == userType) {
                    return new LaundryRequestFragment();
                } else {
                    return null;
                }
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

}