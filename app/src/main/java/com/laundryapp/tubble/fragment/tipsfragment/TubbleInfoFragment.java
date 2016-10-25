package com.laundryapp.tubble.fragment.tipsfragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.laundryapp.tubble.R;

public class TubbleInfoFragment extends Fragment implements View.OnClickListener {

    private ImageButton knowMoreButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tubble_info, container, false);
        knowMoreButton = (ImageButton) view.findViewById(R.id.know_more_button);
        knowMoreButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.know_more_button:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tubbleapp.com/"));
                startActivity(browserIntent);
                break;
            default:
                break;
        }
    }
}
