package com.laundryapp.tubble.fragment.tipsfragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.laundryapp.tubble.R;

public class ContactUsFragment extends Fragment implements View.OnClickListener {

    private ImageView phoneButton, emailButton, knowMoreButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        phoneButton = (ImageView) view.findViewById(R.id.phone_button);
        emailButton = (ImageView) view.findViewById(R.id.email_button);
        knowMoreButton = (ImageView) view.findViewById(R.id.know_more_button);
        phoneButton.setOnClickListener(this);
        emailButton.setOnClickListener(this);
        knowMoreButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone_button:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "09063931566"));
                startActivity(intent);
                break;
            case R.id.email_button:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "lyssaplacido@gmail.com, burton.ina@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tubble Inquiry");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
            case R.id.know_more_button:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tubbleapp.com/"));
                startActivity(browserIntent);
                break;
            default:
                break;
        }

    }
}
