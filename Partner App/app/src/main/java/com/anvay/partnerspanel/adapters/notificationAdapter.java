package com.anvay.partnerspanel.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.anvay.partnerspanel.R;
import com.anvay.partnerspanel.fragments.notifications;
import com.anvay.partnerspanel.models.NotificationItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class notificationAdapter extends ArrayAdapter {

    public notificationAdapter(Context context, int list_item_notifi, ArrayList<NotificationItem> notificationsList) {
        super(context, list_item_notifi, notificationsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null)
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_notifi, parent, false);
        final NotificationItem current = (NotificationItem) getItem(position);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        final String firebaseId = sharedPreferences.getString("firebaseId", null);
        TextView info = listView.findViewById(R.id.info);
        TextView customerName = listView.findViewById(R.id.customer_name);
        TextView address = listView.findViewById(R.id.address);
        customerName.setText("Customer's Name: " + current.getCustomerName());
        address.setText("Address: " + current.getAddress());
        info.setText(current.getInfo());
        final String documentId = current.getId();
        Button accept = listView.findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(getContext());
                dialog.setCancelable(false);
                dialog.setMessage("accepting...");
                dialog.show();
                Map<String, Object> data = new HashMap<>();
                data.put("confirmedId", firebaseId);
                data.put("confirmStatus", true);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("/partnersPanel/maids/requirements/").document(documentId);
                docRef.set(data, SetOptions.merge())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifications notifications = new notifications();
                                FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                                fm.beginTransaction()
                                        .replace(R.id.fragment_holder, notifications)
                                        .commit();
                                dialog.dismiss();
                                notifyDataSetChanged();
                                Toast.makeText(getContext(), "Your accept request is sent.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Unknown Error.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        notifyDataSetChanged();
        return listView;
    }
}
