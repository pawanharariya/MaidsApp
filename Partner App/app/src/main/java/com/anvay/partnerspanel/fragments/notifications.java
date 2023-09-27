package com.anvay.partnerspanel.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anvay.partnerspanel.R;
import com.anvay.partnerspanel.adapters.notificationAdapter;
import com.anvay.partnerspanel.models.NotificationItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class notifications extends Fragment {
    View rootView;

    public notifications() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        final ArrayList<NotificationItem> notificationsList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        final notificationAdapter adapter = new notificationAdapter(getContext(), R.layout.list_item_notifi, notificationsList);
        String pincode = sharedPreferences.getString("pincode", null);
        String dbPath = "partnersPanel/maids/requirements";
        CollectionReference colRef = db.collection(dbPath);
        colRef.whereEqualTo("pincode", pincode)
                .whereEqualTo("confirmStatus", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        notificationsList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.getString("requirementType").equals("instant maid")) {
                                String amount = doc.getString("amount");
                                String time = doc.getString("time");
                                String id = doc.getId();
                                String name = doc.getString("customerName");
                                String address = doc.getString("address");
                                String info = "A need for instant maid for " + time + " with INR " + amount;
                                NotificationItem current = new NotificationItem(id, info, name, "", address, 0);
                                notificationsList.add(current);
                            } else if (doc.getString("requirementType").equals("24X7 maid")) {
                                String salary = String.valueOf(doc.get("salary"));
                                String comment = ". The user also says: " + doc.getString("comment");
                                String category = doc.getString("category");
                                String name = doc.getString("customerName");
                                String address = doc.getString("address");
                                String info;
                                String id = doc.getId();
                                if (doc.getString("comment").equals("")) {
                                    info = "A need for 24X7 maid for " + category + " purpose with salary INR " + salary;
                                } else {
                                    info = "A need for 24X7 maid for " + category + " purpose with salary INR " + salary + comment;
                                }
                                NotificationItem current = new NotificationItem(id, info, name, "", address, 1);
                                notificationsList.add(current);
                            }
                        }
                        ListView listView = rootView.findViewById(R.id.list_notifi);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                });
        return rootView;
    }
}
