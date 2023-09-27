package com.psh.maids.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.psh.maids.R;

public class dashboard extends Fragment {
    View rootView;

    public dashboard() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dashboard_fragment, container, false);
        ImageView back_instant = rootView.findViewById(R.id.back_instant);
        back_instant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout view = rootView.findViewById(R.id.instant1);
                view.setVisibility(View.VISIBLE);
                RelativeLayout bill = rootView.findViewById(R.id.bill);
                bill.setVisibility(View.GONE);
                ImageView back_instant = rootView.findViewById(R.id.back_instant);
                back_instant.setVisibility(View.GONE);
            }
        });
        return rootView;
    }
}
