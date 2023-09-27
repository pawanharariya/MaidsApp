package com.psh.maids.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import com.psh.maids.Activities.Payment;
import com.psh.maids.fragments.servantProfile;
import com.psh.maids.R;
import com.psh.maids.models.Notification;
import com.psh.maids.models.Servant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class notificationAdapter extends ArrayAdapter<Notification> {
    Servant currentServant;
    Notification current;
    Context context;
    String firebaseId;
    ProgressDialog loading, booking;
    String confirmedId;
    String totalCost, details, documentId;

    public notificationAdapter(Context context, int list_item_notifi, ArrayList<Notification> notificationsList) {
        super(context, list_item_notifi, notificationsList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null)
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_notifi, parent, false);
        current = getItem(position);
        loading = new ProgressDialog(context);
        booking = new ProgressDialog(context);
        confirmedId = current.getConfirmedId();
        totalCost = current.getAmount();
        Log.e("current.getAmount",current.getAmount());
        documentId = current.getId();
        details = current.getInfo();
        TextView info = listView.findViewById(R.id.info);
        info.setText(current.getInfo());
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        firebaseId = sharedPreferences.getString("firebaseId", null);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Button bookNow = listView.findViewById(R.id.book_now);
        Button viewProfile = listView.findViewById(R.id.view_profile);

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setMessage("Loading Profile..");
                loading.setCancelable(false);
                loading.show();
                DocumentReference docRef = db.collection("partnersPanel/maids/details").document(confirmedId);
                docRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot != null) {
                                    currentServant = documentSnapshot.toObject(Servant.class);
                                    loading.dismiss();
                                    servantProfile servantProfile = new servantProfile(context);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("currentServant", currentServant);
                                    bundle.putBoolean("instantRequirement", true);
                                    servantProfile.setArguments(bundle);
                                    FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.fragmentHolder, servantProfile)
                                            .addToBackStack("tag")
                                            .commit();
                                }

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
        bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                booking.setMessage("Please Wait...");
                booking.setCancelable(false);
                booking.show();
                Log.e("post in partnersPanel", "posted");
                DocumentReference docRef = db.collection("partnersPanel/maids/details").document(confirmedId);
                docRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot != null) {
                                    currentServant = documentSnapshot.toObject(Servant.class);
                                    Intent i = new Intent(getContext(), Payment.class);
                                    i.putExtra("totalCost", totalCost);
                                    Log.e("totalCost",totalCost);
                                    i.putExtra("servantName", currentServant.getName());
                                    i.putExtra("servantMobile", currentServant.getMobile());
                                    i.putExtra("servantAddress", currentServant.getAddress());
                                    i.putExtra("info", details);
                                    i.putExtra("documentId", documentId);
                                    i.putExtra("instantBooking", true);
                                    i.putExtra("servantId", confirmedId);
                                    i.putExtra("cartBooking",false);
                                    getContext().startActivity(i);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Unknown Error", Toast.LENGTH_SHORT).show();
                                booking.dismiss();
                            }
                        });
                booking.dismiss();
            }
        });
        return listView;
    }
}
