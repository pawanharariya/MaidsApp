package com.anvay.partnerspanel.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anvay.partnerspanel.R;
import com.anvay.partnerspanel.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter {
    GeoPoint gp = null;
    String destinationLocation = null;
    String currentLocation;

    public UserAdapter(@NonNull Context context, int resource, ArrayList<User> userList) {
        super(context, resource, userList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null)
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_bookings, parent, false);
        final User currentUser = (User) getItem(position);
        TextView name = listView.findViewById(R.id.user_name);
        TextView contact = listView.findViewById(R.id.user_contact);
        TextView address = listView.findViewById(R.id.user_address);
        ImageView customerLocation = listView.findViewById(R.id.customer_location);
        customerLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCustomerLocation(currentUser.getFirebaseId());
            }
        });
        name.setText(currentUser.getName());
        contact.setText(currentUser.getMobile());
        address.setText(currentUser.getAddress());
        return listView;
    }

    private void getCustomerLocation(String customerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference location = db.collection("/customerPanel/locations/currentLocations/").document(customerId);
        location.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        gp = document.getGeoPoint("location");
                        String lat = String.valueOf(gp.getLatitude());
                        String lng = String.valueOf(gp.getLongitude());
                        destinationLocation = lat + "," + lng;
                        Log.e("geopoint", String.valueOf(gp.getLatitude()));
                        Log.e("success", "DocumentSnapshot data: " + document.getData());
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
                        currentLocation = sharedPreferences.getString("currentLocation", "");
                        if (destinationLocation != null) {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr=" + currentLocation + "&daddr=" + destinationLocation));
                            getContext().startActivity(intent);
                        } else
                            Toast.makeText(getContext(), "No location shown", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("no document", "No such document");
                    }
                } else {
                    Log.e("failed", "get failed with ", task.getException());
                }
            }
        });
    }
}
