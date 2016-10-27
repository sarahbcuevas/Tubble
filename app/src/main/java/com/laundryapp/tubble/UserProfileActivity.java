package com.laundryapp.tubble;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.laundryapp.tubble.entities.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private final String TAG = this.getClass().getName();
    private final static int IMG_RESULT = 1;
    private Button mClearButton, mSaveButton;
    private EditText mMobileNumber, mFullName, mEmail, mPassword, mAddress;
    private CircleImageView mUserPhoto;
    private ImageView selectPhotoButton, takePhotoButton;
    private String imageDecode;

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
        mUserPhoto = (CircleImageView) findViewById(R.id.profile_image);
        selectPhotoButton = (ImageView) findViewById(R.id.select_photo_button);
        takePhotoButton = (ImageView) findViewById(R.id.take_photo_button);
        selectPhotoButton.setOnClickListener(this);
        takePhotoButton.setOnClickListener(this);
        mClearButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        Picasso.with(getApplicationContext()).load(R.drawable.userphoto).into((ImageView) findViewById(R.id.profile_image));
        Picasso.with(getApplicationContext()).load(R.drawable.photo).into((ImageView) findViewById(R.id.select_photo_button));
        Picasso.with(getApplicationContext()).load(R.drawable.cam).into((ImageView) findViewById(R.id.take_photo_button));
        mUserPhoto.setBorderWidth(0);
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
                mUserPhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.userphoto));
                mUserPhoto.setBorderWidth(0);
                break;
            case R.id.save_button:
                String mobileNumber = mMobileNumber.getText().toString();
                String fullName = mFullName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String address = mAddress.getText().toString();
                User user = new User(fullName, mobileNumber, email, address, password, imageDecode);
                long user_id = user.save();
                Utility.setUserId(this, user_id);
                Utility.setUserType(this, User.Type.CUSTOMER);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(MainActivity.USER_ID, user_id);
                startActivity(intent);
                Log.d(TAG, "user id: " + user_id);
                break;
            case R.id.select_photo_button:
                Intent intentPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentPhoto, IMG_RESULT);
                break;
            case R.id.take_photo_button:
                imageDecode = Utility.takePhotoUsingCamera(this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == IMG_RESULT && resultCode == Activity.RESULT_OK && data != null) {
                Uri URI = data.getData();
                String[] FILE = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(URI, FILE, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(FILE[0]);
                imageDecode = cursor.getString(columnIndex);
                Log.d(TAG, "Image Decode: " + imageDecode);
                cursor.close();
                mUserPhoto.setImageBitmap(BitmapFactory.decodeFile(imageDecode));
                mUserPhoto.setBorderWidth(20);
            } else if (requestCode == Utility.CAPTURE_IMAGE_RESULT && resultCode == Activity.RESULT_OK) {
                Utility.scaleAndRotateImage(mUserPhoto, imageDecode);
                Utility.savePicToGallery(this, imageDecode);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
