package com.laundryapp.tubble;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.laundryapp.tubble.entities.User;

import java.util.List;

public class StartUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

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

}
