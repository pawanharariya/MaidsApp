package com.psh.maids.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.psh.maids.R;
import com.psh.maids.adapters.BookingsAdapter;
import com.psh.maids.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Bookings extends AppCompatActivity {
    Drawable selectedBg, unSelectedBg;
    Button currentBookings, pastBookings;
    Boolean activeStatus;
    ListView bookingsListView;
    ArrayList<User> bookingsList;
    Context context;
    String firebaseId;
    BookingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);
        context = Bookings.this;
        bookingsList = new ArrayList<>();
        currentBookings = findViewById(R.id.current_bookings);
        pastBookings = findViewById(R.id.previous_bookings);
        bookingsListView = findViewById(R.id.bookings_list);
        selectedBg = ContextCompat.getDrawable(Bookings.this, R.drawable.green_corner);
        unSelectedBg = ContextCompat.getDrawable(Bookings.this, R.drawable.default_button);
        SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        firebaseId = sharedPreferences.getString("firebaseId", null);
        currentBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentBookings.setBackground(selectedBg);
                pastBookings.setBackground(unSelectedBg);
                activeStatus = true;
                bookingsList.clear();
                getList();
            }
        });
        pastBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pastBookings.setBackground(selectedBg);
                currentBookings.setBackground(unSelectedBg);
                activeStatus = false;
                bookingsList.clear();
                getList();
            }
        });
        currentBookings.performClick();
    }

    private void getList() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ProgressDialog loading = new ProgressDialog(Bookings.this);
        loading.setMessage("Fetching your bookings");
        loading.setCancelable(false);
        loading.show();
        CollectionReference colRef = db.collection("/customerPanel/bookings/" + firebaseId);
        colRef.whereEqualTo("activeStatus", activeStatus)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            bookingsList.clear();
                            if (context != null) {
                                adapter = new BookingsAdapter(context, R.layout.list_item_bookings, bookingsList);
                                bookingsListView.setAdapter(adapter);
                            }
                            for (DocumentSnapshot doc : task.getResult()) {
                                Log.e("document from firebase ", doc.toString());
                                User user = doc.toObject(User.class);
                                Log.e("Bookings user activity", user.getFirebaseId());
                                bookingsList.add(user);
                            }
                            adapter.notifyDataSetChanged();
                            loading.dismiss();
                        } else
                            Toast.makeText(context, "Failed to load", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
