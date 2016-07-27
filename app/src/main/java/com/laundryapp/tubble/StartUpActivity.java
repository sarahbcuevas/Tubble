package com.laundryapp.tubble;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.laundryapp.tubble.entities.LaundryService;
import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.LaundryShopService;
import com.laundryapp.tubble.entities.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StartUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        Picasso.with(getApplicationContext()).load(R.drawable.start_up).into((ImageView) findViewById(R.id.start_up_image));
        setupDatabase();
        List<User> users = User.listAll(User.class);
        final Intent nextActivity;
        if (users.isEmpty()) {
            nextActivity = new Intent(this, UserProfileActivity.class);
        } else {
            nextActivity = new Intent(this, MainActivity.class);
        }
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(nextActivity);
            }
        }, 1500);
    }

    public void setupDatabase() {
        List<LaundryService> services = LaundryService.listAll(LaundryService.class);
        List<Long> services_id = new ArrayList<Long>();

        if (services.isEmpty()) {
            LaundryService service = new LaundryService("Wash-Dry-Fold");
            services_id.add(LaundryService.WASH_DRY_FOLD, service.save());
            service = new LaundryService("Wash-Dry-Press");
            services_id.add(LaundryService.WASH_DRY_PRESS, service.save());
            service = new LaundryService("Comforter Cleaning");
            services_id.add(LaundryService.COMFORTER_CLEANING, service.save());
            service = new LaundryService("Household Item Cleaning");
            services_id.add(LaundryService.HOUSEHOLD_ITEM_CLEANING, service.save());
            service = new LaundryService("Dry Cleaning");
            services_id.add(LaundryService.DRY_CLEANING, service.save());
        }
        List<LaundryShop> shops = LaundryShop.listAll(LaundryShop.class);
        long shop_id = -1;
        if (shops.isEmpty()) {
            //Name, Address, Schedule, Contact, Rating
            LaundryShop shop = new LaundryShop("Suds", "Ground Floor, 94 Xavier Ave, Quezon City, 1105 Metro Manila", "Daily: 8AM to 8PM", "(02) 375 1704", 4);
            shop_id = shop.save();
            LaundryShopService shopService = new LaundryShopService(shop_id, services_id.get(LaundryService.WASH_DRY_FOLD), 35);
            shopService.save();
            shop = new LaundryShop("Panda Cleaners", "F Dela Rosa Street, Quezon City, Manila", "Daily: 6AM to 10PM", "(02) 579 0002", 4);
            shop_id = shop.save();
            shopService = new LaundryShopService(shop_id, services_id.get(LaundryService.WASH_DRY_FOLD), 40);
            shopService.save();
            shop = new LaundryShop("Metropole", "Gilmore Corner E. Rodriguez Sr. Avenue, Quezon City, New Manila, Quezon City, Metro Manila", "", "(02) 414 4587", 4);
            shop_id = shop.save();
            shopService = new LaundryShopService(shop_id, services_id.get(LaundryService.WASH_DRY_FOLD), 30);
            shopService.save();
            shopService = new LaundryShopService(shop_id, services_id.get(LaundryService.WASH_DRY_PRESS), 40);
            shopService.save();
            shopService = new LaundryShopService(shop_id, services_id.get(LaundryService.COMFORTER_CLEANING), 45);
            shopService.save();
            shopService = new LaundryShopService(shop_id, services_id.get(LaundryService.HOUSEHOLD_ITEM_CLEANING), 25);
            shopService.save();
            shopService = new LaundryShopService(shop_id, services_id.get(LaundryService.DRY_CLEANING), 50);
            shopService.save();
            shop = new LaundryShop("Quicklean", "107 Maginhawa Diliman, Quezon City, Manila", "Daily: 7AM to 10PM", "0917 517 1470 / (02) 505 5349 / (02) 434 2834", 4);
            shop_id = shop.save();
            shopService = new LaundryShopService(shop_id, services_id.get(LaundryService.WASH_DRY_FOLD), 50);
            shopService.save();
        }
    }
}
