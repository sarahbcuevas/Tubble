package com.laundryapp.tubble;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.User;

import java.util.List;

public class StartUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

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
        List<LaundryShop> shops = LaundryShop.listAll(LaundryShop.class);
        if (shops.isEmpty()) {
            //Name, Address, Schedule, Contact
            LaundryShop shop = new LaundryShop("Suds", "Ground Floor, 94 Xavier Ave, Quezon City, 1105 Metro Manila", "Daily: 8AM to 8PM", "(02) 375 1704", 4);
            shop.save();
            shop = new LaundryShop("Panda Cleaners", "F Dela Rosa Street, Quezon City, Manila", "Daily: 6AM to 10PM", "(02) 579 0002", 4);
            shop.save();
        }

    }
}
