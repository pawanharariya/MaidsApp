package com.anvay.partnerspanel.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anvay.partnerspanel.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class profile extends Fragment {
    private View rootView;
    private CircleImageView profilePic;
    private EditText nameTextView, mobileTextView, cityTextView, localityTextView, pincodeTextView, aboutTextView, nameOtherTextView, costTextView, ageTextView;

    public profile() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        getUI();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        String imageUrl = sharedPreferences.getString("imageUrl", null);
        Picasso.with(getContext()).load(imageUrl).resize(100, 100).placeholder(R.drawable.user).into(profilePic);
        nameOtherTextView.setText(sharedPreferences.getString("nameOther", null));
        nameTextView.setText(sharedPreferences.getString("name", null));
        pincodeTextView.setText(sharedPreferences.getString("pincode", null));
        localityTextView.setText(sharedPreferences.getString("locality", null));
        cityTextView.setText(sharedPreferences.getString("city", null));
        aboutTextView.setText(sharedPreferences.getString("about", null));
        ageTextView.setText(sharedPreferences.getString("age", null));
        costTextView.setText(sharedPreferences.getString("cost", null));
        mobileTextView.setText(sharedPreferences.getString("mobile", null));
        return rootView;
    }

    public void getUI() {
        nameTextView = rootView.findViewById(R.id.name);
        mobileTextView = rootView.findViewById(R.id.mobileNumber);
        cityTextView = rootView.findViewById(R.id.city);
        localityTextView = rootView.findViewById(R.id.locality);
        pincodeTextView = rootView.findViewById(R.id.pincode);
        ageTextView = rootView.findViewById(R.id.age);
        aboutTextView = rootView.findViewById(R.id.about);
        nameOtherTextView = rootView.findViewById(R.id.nameOther);
        costTextView = rootView.findViewById(R.id.workRate);
        profilePic = rootView.findViewById(R.id.profilePicture);
    }
}
