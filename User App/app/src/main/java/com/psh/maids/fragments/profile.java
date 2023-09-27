package com.psh.maids.fragments;

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

import com.psh.maids.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends Fragment {
    private EditText nameTextView, mobileTextView, cityTextView, localityTextView, pincodeTextView;
    private CircleImageView profileImage;

    public profile() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        profileImage = rootView.findViewById(R.id.profilePicture);
        nameTextView = rootView.findViewById(R.id.name);
        mobileTextView = rootView.findViewById(R.id.mobileNumber);
        cityTextView = rootView.findViewById(R.id.city);
        localityTextView = rootView.findViewById(R.id.locality);
        pincodeTextView = rootView.findViewById(R.id.pincode);
        nameTextView.setText(sharedPreferences.getString("name", null));
        mobileTextView.setText(sharedPreferences.getString("mobile", null));
        pincodeTextView.setText(sharedPreferences.getString("pincode", null));
        localityTextView.setText(sharedPreferences.getString("locality", null));
        cityTextView.setText(sharedPreferences.getString("city", null));
        String imageUrl = sharedPreferences.getString("imageUrl", null);
        Picasso.with(getContext())
                .load(imageUrl)
                .resize(100, 100)
                .placeholder(R.drawable.user)
                .into(profileImage);
        return rootView;
    }
}
