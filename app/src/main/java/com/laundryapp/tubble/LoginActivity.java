package com.laundryapp.tubble;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.laundryapp.tubble.entities.LaundryShop;
import com.laundryapp.tubble.entities.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();
    EditText mobileNumber, password;
    Button loginButton, signupButton, forgotButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mobileNumber = (EditText) findViewById(R.id.mobile_number);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login_button);
        signupButton = (Button) findViewById(R.id.signup_button);
//        forgotButton = (Button) findViewById(R.id.forgot_button);
        loginButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
//        forgotButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                String mobileInput = mobileNumber.getText().toString();
                String passwordInput = password.getText().toString();
                if (mobileInput.equals("") || passwordInput.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please input mobile number and password.", Toast.LENGTH_SHORT).show();
                } else {
                    List<User> users = User.find(User.class, "m_Mobile_Number = ? and m_Password = ?", mobileInput, passwordInput);
                    if (users.isEmpty()) {
                        List<LaundryShop> shops = LaundryShop.find(LaundryShop.class, "m_Login = ? and m_Password = ?", mobileInput, passwordInput);
                        if (shops.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Please input correct login details.", Toast.LENGTH_SHORT).show();
                            mobileNumber.setText("");
                            password.setText("");
                        } else {
                            Utility.setUserType(getApplicationContext(), User.Type.LAUNDRY_SHOP);
                            Utility.setUserId(getApplicationContext(), shops.get(0).getId());
                            Intent nextActivity = new Intent(this, MainActivity.class);
                            nextActivity.putExtra(MainActivity.USER_ID, shops.get(0).getId());
                            nextActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(nextActivity);
                        }
                    } else {
                        Utility.setUserType(getApplicationContext(), User.Type.CUSTOMER);
                        Utility.setUserId(getApplicationContext(), users.get(0).getId());
                        Intent nextActivity = new Intent(this, MainActivity.class);
                        nextActivity.putExtra(MainActivity.USER_ID, users.get(0).getId());
                        nextActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(nextActivity);
                    }
                }
                break;
            case R.id.signup_button:
                Intent signupActivity = new Intent(this, UserProfileActivity.class);
                startActivity(signupActivity);
                break;
//            case R.id.forgot_button:
//                break;
            default:
                break;
        }
    }
}
