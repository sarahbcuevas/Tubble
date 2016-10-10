package com.laundryapp.tubble;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener,
        FindFragment.OnFragmentInteractionListener, SchedulerFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, StatusFragment.OnFragmentInteractionListener,
        TipsFragment.OnFragmentInteractionListener, LaundryRequestFragment.OnFragmentInteractionListener {

    private final String TAG = this.getClass().getName();
    private TabPagerAdapter mTabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

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

//        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setTitle("");
//        setActionBar(mToolbar);

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
//                if (mTabPagerAdapter.getItem(mTabLayout.getSelectedTabPosition()) instanceof StatusFragment) {
//                    StatusFragment.updateLaundryList();
//                }
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

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Utility.logout(this);
                return true;
            case R.id.search:
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setOnQueryTextListener(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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

    @Override
    public void onCheckBookingStatus(long id) {
        ((StatusFragment) mTabPagerAdapter.getItem(2)).onCheckBookingStatus(id);
        mViewPager.setCurrentItem(2);
    }

    @Override
    public void onAddOrDeleteLaundrySchedule() {
        ((StatusFragment) mTabPagerAdapter.getItem(2)).updateLaundryList();
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
            case 0:
                if (User.Type.CUSTOMER == userType) {
                    return new FindFragment();
                } else if (User.Type.LAUNDRY_SHOP == userType) {
                    return new LaundryRequestFragment();
                }
            case 1:
                return new SchedulerFragment();
            case 2:
                return new StatusFragment();
            case 3:
                return new TipsFragment();
            case 4:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

}