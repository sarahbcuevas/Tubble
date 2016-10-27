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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toolbar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.laundryapp.tubble.entities.LaundryService;
import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.LaundryShopService;
import com.laundryapp.tubble.entities.User;
import com.laundryapp.tubble.fragment.CalendarWeekViewFragment;
import com.laundryapp.tubble.fragment.FindFragment;
import com.laundryapp.tubble.fragment.LaundryRequestFragment;
import com.laundryapp.tubble.fragment.ProfileFragment;
import com.laundryapp.tubble.fragment.SchedulerFragment;
import com.laundryapp.tubble.fragment.StatusFragment;
import com.laundryapp.tubble.fragment.TipsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements
        FindFragment.OnFragmentInteractionListener, SchedulerFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, StatusFragment.OnFragmentInteractionListener,
        TipsFragment.OnFragmentInteractionListener, LaundryRequestFragment.OnFragmentInteractionListener,
        View.OnClickListener {

    private final String TAG = this.getClass().getName();
    private TabPagerAdapter mTabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private MenuItem menuSearch, menuStatus, menuCancel, menuEdit, menuDone;
    private RelativeLayout menuBack, menuLogout;

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
        menuBack = (RelativeLayout) mToolbar.findViewById(android.R.id.home);
        menuLogout = (RelativeLayout) mToolbar.findViewById(R.id.action_logout);
        menuBack.setOnClickListener(this);
        menuLogout.setOnClickListener(this);
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
                    StatusFragment.updateLaundryList();
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
        menuStatus = menu.findItem(R.id.action_status);
        menuCancel = menu.findItem(R.id.action_cancel);
        menuEdit = menu.findItem(R.id.action_edit);
        menuDone = menu.findItem(R.id.action_done);
        updateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void updateBackButtonVisibility() {
        updateOptionsMenu();
    }

    private void updateOptionsMenu() {
        int currentTab = mViewPager.getCurrentItem();
        User.Type userType = Utility.getUserType(getApplicationContext());

        if (currentTab == 0) {                          // Find Fragment
            if (userType == User.Type.CUSTOMER) {
                menuSearch.setVisible(true);
                if (FindFragment.isLaundryInfoVisible()) {
                    menuBack.setVisibility(View.VISIBLE);
                } else {
                    menuBack.setVisibility(View.GONE);
                }
            } else {
                menuSearch.setVisible(false);
                menuBack.setVisibility(View.GONE);
            }
            menuLogout.setVisibility(View.GONE);
            menuStatus.setVisible(false);
            menuEdit.setVisible(false);
            menuDone.setVisible(false);
        } else if (currentTab == 2) {                           // Status Fragment
            if (userType == User.Type.CUSTOMER) {
                menuStatus.setVisible(true);
            } else {
                menuStatus.setVisible(false);
            }
            menuSearch.setVisible(false);
            menuEdit.setVisible(false);
            menuDone.setVisible(false);
            menuBack.setVisibility(View.GONE);
            menuLogout.setVisibility(View.GONE);
        } else if (currentTab == 4) {                          // Profile Fragment
            if (ProfileFragment.isTrackHistoryVisible()) {
                menuLogout.setVisibility(View.GONE);
                menuEdit.setVisible(false);
                menuDone.setVisible(false);
                menuBack.setVisibility(View.VISIBLE);
            } else {
                menuLogout.setVisibility(View.VISIBLE);
                menuBack.setVisibility(View.GONE);
                if (userType == User.Type.CUSTOMER) {
                    menuEdit.setVisible(!ProfileFragment.isOnEditMode());
                    menuDone.setVisible(ProfileFragment.isOnEditMode());
                } else {
                    menuEdit.setVisible(false);
                    menuDone.setVisible(false);
                }
            }
            menuSearch.setVisible(false);
            menuStatus.setVisible(false);
        } else {
            menuStatus.setVisible(false);
            menuSearch.setVisible(false);
            menuEdit.setVisible(false);
            menuDone.setVisible(false);
            menuLogout.setVisibility(View.GONE);
            menuBack.setVisibility(View.GONE);
        }
        menuCancel.setVisible(false);
    }

    @Override
    public void onClick(View view) {
        int currentTab = mViewPager.getCurrentItem();
        User.Type userType = Utility.getUserType(getApplicationContext());
        switch (view.getId()) {
            case android.R.id.home:
                if (currentTab == 0 && userType == User.Type.CUSTOMER && FindFragment.isLaundryInfoVisible()) {
                    FindFragment.onBackPressed();
                    menuBack.setVisibility(View.GONE);
                } else if (currentTab == 4 && ProfileFragment.isTrackHistoryVisible()) {     // Profile Fragment
                    ProfileFragment.onBackPressed();
                    menuBack.setVisibility(View.GONE);
                }
                break;
            case R.id.action_logout:
                Utility.logout(this);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

                final Spinner ratingSpinner = (Spinner) dialog.findViewById(R.id.rating_spinner);
                List<String> ratings = new ArrayList<String>();
                ratings.add("Rating");
                ratings.add("1");
                ratings.add("2");
                ratings.add("3");
                ratings.add("4");
                ratings.add("5");

                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                        R.layout.simple_spinner_item, ratings);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);

                // attaching data adapter to spinner
                ratingSpinner.setAdapter(dataAdapter);

                final Spinner serviceSpinner = (Spinner) dialog.findViewById(R.id.service_spinner);
                List<String> services = new ArrayList<String>();
                services.add("Service");

                List<LaundryService> serviceList = LaundryService.listAll(LaundryService.class);
                for (LaundryService service : serviceList) {
                    services.add(service.getLabel());
                }

                // Creating adapter for spinner
                ArrayAdapter<String> data1Adapter = new ArrayAdapter<String>(this,
                        R.layout.simple_spinner_item, services);

                // Drop down layout style - list view with radio button
                data1Adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);

                // attaching data adapter to spinner
                serviceSpinner.setAdapter(data1Adapter);

                final EditText location = (EditText) dialog.findViewById(R.id.location_search);

                ImageButton searchBtn = (ImageButton) dialog.findViewById(R.id.search_btn);
                searchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Search", "Click");
                        int ratingPos = ratingSpinner.getSelectedItemPosition();
                        int servicePos = serviceSpinner.getSelectedItemPosition();
                        openSearchResults(ratingPos, servicePos, location.getText().toString());
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
                if (mViewPager.getCurrentItem() == 1) {             // Scheduler Fragment
                    SchedulerFragment.onBackPressed();
                    menuCancel.setVisible(false);
                } else if (mViewPager.getCurrentItem() == 2) {     // Status Fragment
                    onBackPressed();
                }
                return true;
            case R.id.action_edit:
                ProfileFragment.onEditUserProfile(true);
                menuEdit.setVisible(false);
                menuDone.setVisible(true);
                return true;
            case R.id.action_done:
                ProfileFragment.updateUserProfile();
                ProfileFragment.onEditUserProfile(false);
                menuEdit.setVisible(true);
                menuDone.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSearchResults(int ratingPos, int servicePos, String location) {
        if (servicePos == 0) {
            List<LaundryShop> shops = LaundryShop.listAll(LaundryShop.class);
            List<LaundryShop> elligibleShops = new ArrayList<>();

            if (ratingPos == 0) {
                for (LaundryShop tempShop : shops) {
                    if (tempShop.getAddress().toLowerCase().contains(location.toLowerCase())) {
                        elligibleShops.add(tempShop);
                    }
                }

                FindFragment.showSearchResults(elligibleShops);
            } else {
                for (LaundryShop tempShop : shops) {
                    if ((tempShop.getRating() <= (ratingPos + .99))
                            && (tempShop.getRating() >= ratingPos)
                            && (tempShop.getAddress().toLowerCase().contains(location.toLowerCase()))) {
                        elligibleShops.add(tempShop);
                    }
                }

                FindFragment.showSearchResults(elligibleShops);
            }
        } else {
            List<LaundryShopService> shopsWithService = LaundryShopService.find(
                    LaundryShopService.class, "m_Laundry_Service_Id  = ?", servicePos + "");

            List<LaundryShop> elligibleShops = new ArrayList<>();
            for (LaundryShopService tempShop : shopsWithService) {
                LaundryShop shop = LaundryShop.findById(LaundryShop.class, tempShop.getLaundryShopId());

                Log.e("Search", "SHop " + shop.getName() + " " + shop.getRating() + " :: " + ratingPos);
                if (ratingPos == 0) {
                    if (shop.getAddress().toLowerCase().contains(location.toLowerCase())) {
                        elligibleShops.add(shop);
                    }
                } else if ((shop.getRating() <= (ratingPos + .99))
                        && (shop.getRating() >= ratingPos)
                        && (shop.getAddress().toLowerCase().contains(location.toLowerCase()))) {
                    elligibleShops.add(shop);
                }
            }

            FindFragment.showSearchResults(elligibleShops);
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
        if (mViewPager.getCurrentItem() == 0) { // Find Fragment
            onBackPressed = FindFragment.onBackPressed();
            menuCancel.setVisible(false);
        } else if (mViewPager.getCurrentItem() == 1) { // Scheduler Fragment
            onBackPressed = SchedulerFragment.onBackPressed();
            menuCancel.setVisible(false);
        } else if (mViewPager.getCurrentItem() == 2) { // Status Fragment
            if (StatusFragment.getCurrentVisibleMode() == StatusFragment.VisibleLayout.APPROVED_BOOKINGS_LAYOUT) {
                if (StatusFragment.getParentLayoutOfApprovedBooking() == StatusFragment.ParentLayout.STATUS_LIST_PARENT) {
                    StatusFragment.updateLaundryList();
                } else if (StatusFragment.getParentLayoutOfApprovedBooking() == StatusFragment.ParentLayout.STATUS_INFO_PARENT) {
                    StatusFragment.onCheckBookingStatus(StatusFragment.selectedBookingId);
                }
                menuStatus.setVisible(true);
                menuCancel.setVisible(false);
                onBackPressed = true;
            } else if (StatusFragment.getCurrentVisibleMode() == StatusFragment.VisibleLayout.STATUS_INFO_LAYOUT) {
                StatusFragment.updateLaundryList();
                menuStatus.setVisible(true);
                menuCancel.setVisible(false);
                onBackPressed = true;
            } else if (StatusFragment.getCurrentVisibleMode() == StatusFragment.VisibleLayout.EDIT_LAUNDRY_LAYOUT) {
                StatusFragment.updateLaundryList();
                menuCancel.setVisible(false);
                onBackPressed = true;
            }
        } else if (mViewPager.getCurrentItem() == 4) {  // Profile Fragment
            onBackPressed = ProfileFragment.onBackPressed();
        }

        if (!onBackPressed) {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.shut_down_dialog);

            Button closeBtn = (Button) dialog.findViewById(R.id.cancel_button);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            Button shutdownBtn = (Button) dialog.findViewById(R.id.shut_down_button);
            shutdownBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   finish();
                }
            });

            dialog.show();
//            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onViewLaundryScheduleDetails() {
        menuCancel.setVisible(true);
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
    User.Type userType;

    public TabPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        userType = Utility.getUserType(context);
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