package com.psh.maids.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.psh.maids.Activities.RateUser;
import com.psh.maids.R;
import com.psh.maids.models.User;

import java.util.ArrayList;

public class BookingsAdapter extends ArrayAdapter<User> {
    User currentUser;
    TextView name, contact;
    Button rate;
    Context context;
    String servantFirebaseId;

    public BookingsAdapter(@NonNull Context context, int resource, ArrayList<User> bookingsList) {
        super(context, resource, bookingsList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null)
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_bookings, parent, false);
        currentUser = getItem(position);
        name = listView.findViewById(R.id.name);
        contact = listView.findViewById(R.id.contact);
        name.setText(currentUser.getName());
        rate = listView.findViewById(R.id.rate_servant);
        contact.setText(currentUser.getMobile());
        servantFirebaseId = currentUser.getFirebaseId();
        if (!currentUser.getActiveStatus()) {
            rate.setEnabled(false);
        }
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("BookingsAdapter", currentUser.getFirebaseId());
                Log.e("BookingsAdapterName", currentUser.getName());
                Intent i = new Intent(getContext(), RateUser.class);
                i.putExtra("servantFirebaseId", servantFirebaseId);
                getContext().startActivity(i);
            }
        });
        return listView;
    }
}
