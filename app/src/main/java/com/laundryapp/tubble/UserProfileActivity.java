package com.laundryapp.tubble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.laundryapp.tubble.entities.User;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private final String TAG = this.getClass().getName();
    private Button mClearButton, mSaveButton;
    private EditText mMobileNumber, mFullName, mEmail, mPassword, mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        mClearButton = (Button) findViewById(R.id.clear_button);
        mSaveButton = (Button) findViewById(R.id.save_button);
        mMobileNumber = (EditText) findViewById(R.id.mobile_number);
        mFullName = (EditText) findViewById(R.id.full_name);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mAddress = (EditText) findViewById(R.id.address);
        mClearButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        Picasso.with(getApplicationContext()).load(R.drawable.userphoto).into((ImageView) findViewById(R.id.user_photo));
        Picasso.with(getApplicationContext()).load(R.drawable.photo).into((ImageView) findViewById(R.id.photo));
        Picasso.with(getApplicationContext()).load(R.drawable.cam).into((ImageView) findViewById(R.id.camera));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_button:
                mMobileNumber.setText("");
                mFullName.setText("");
                mEmail.setText("");
                mPassword.setText("");
                mAddress.setText("");
                break;
            case R.id.save_button:
                String mobileNumber = mMobileNumber.getText().toString();
                String fullName = mFullName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String address = mAddress.getText().toString();
                User user = new User(fullName, mobileNumber, email, address, password);
                long user_id = user.save();
                Utility.setUserId(this, user_id);
                Utility.setUserType(this, User.Type.CUSTOMER);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(MainActivity.USER_ID, user_id);
                startActivity(intent);
                Log.d(TAG, "user id: " + user_id);
                break;
            default:
                break;
        }
    }
}
