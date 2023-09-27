package com.anvay.partnerspanel.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.anvay.partnerspanel.R;
import com.anvay.partnerspanel.activities.Location;
import com.anvay.partnerspanel.adapters.UserAdapter;
import com.anvay.partnerspanel.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class home extends Fragment {
    View rootView;
    TextView notificationNumber, userLocation;

    public home() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ImageView notification = rootView.findViewById(R.id.notification_button);
        userLocation = rootView.findViewById(R.id.user_location);
        notificationNumber = rootView.findViewById(R.id.notification_number);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        int notificationNo = sharedPreferences.getInt("notificationNumber", 0);
        if (notificationNo > 0)
            notificationNumber.setText("" + notificationNo);
        userLocation.setText(sharedPreferences.getString("address", null));
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifications notificationsFragment = new notifications();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_holder, notificationsFragment)
                        .addToBackStack("tag")
                        .commit();
            }
        });
        final ArrayList<User> currentBookings = new ArrayList<>();
        String firebaseId = sharedPreferences.getString("firebaseId", null);
        final ListView list = rootView.findViewById(R.id.bookings_list);
        String dbPath = "partnersPanel/bookings/" + firebaseId;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection(dbPath);
        colRef.whereEqualTo("activeStatus", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        currentBookings.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            User user = doc.toObject(User.class);
                            currentBookings.add(user);
                        }
                        if (getContext() != null) {
                            UserAdapter adapter = new UserAdapter(getContext(), R.layout.list_item_bookings, currentBookings);
                            list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                    }
                });
        userLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), Location.class);
                startActivity(i);
            }
        });
        return rootView;
    }
}
