package com.psh.maids.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.psh.maids.R;
import com.psh.maids.adapters.notificationAdapter;
import com.psh.maids.models.Notification;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class notifications extends Fragment {
    private View rootView;
    private SharedPreferences sharedPreferences;
    private notificationAdapter adapter;

    public notifications() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        final ArrayList<Notification> notificationsList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ListView listView = rootView.findViewById(R.id.list_notifi);
        sharedPreferences = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        String firebaseId = sharedPreferences.getString("firebaseId", null);
        String dbPath = "partnersPanel/maids/requirements";
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.show();
        CollectionReference colRef = db.collection(dbPath);
        colRef.whereEqualTo("confirmStatus", true)
                .whereEqualTo("customerId", firebaseId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            return;
                        }
                        notificationsList.clear();
                        if (getContext() != null) {
                            adapter = new notificationAdapter(getContext(), R.layout.list_item_notifi, notificationsList);
                            listView.setAdapter(adapter);
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            String requirementType = doc.getString("requirementType");
                            if (requirementType.equals("instant maid")) {
                                String amount = doc.getString("amount");
                                String time = doc.getString("time");
                                String id = doc.getId();
                                Log.e("amount from firebase", amount);
                                String confirmedId = doc.getString("confirmedId");
                                String info = "Your post for requirement of instant maid for " + time + " with INR " + amount + " is accepted by a maid.";
                                Notification current = new Notification(id, info, confirmedId, requirementType, amount);
                                notificationsList.add(current);
                            } else if (requirementType.equals("24X7 maid")) {
                                Long salary = (Long) doc.get("salary");
                                String category = doc.getString("category");
                                String id = doc.getId();
                                String confirmedId = doc.getString("confirmedId");
                                String info = "Your post for requirement of 24X7 maid for " + category + " purpose with salary amount INR " + salary + " is accepted by a maid.";
                                Notification current = new Notification(id, info, confirmedId, requirementType, "" + salary);
                                notificationsList.add(current);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        dialog.dismiss();
        return rootView;
    }
}
