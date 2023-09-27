package com.psh.maids.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.psh.maids.Activities.Payment;
import com.psh.maids.R;
import com.psh.maids.adapters.servantAdapter;
import com.psh.maids.models.Servant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class cart extends Fragment {
    static ListView listView;//TOdo think of some changes here
    View rootView;
    Servant servant;
    Button bookNow;
    LinearLayout cartEmpty;
    servantAdapter adapter;
    ProgressDialog dialog;
    int totalCost;

    public cart() {
    }

    public static void listStateChanged() {
        setListViewHeightBasedOnChildren(listView);
    }

    public static void setListViewHeightBasedOnChildren
            (ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.cart_fragment, container, false);
        cartEmpty = rootView.findViewById(R.id.cart_empty);
        bookNow = rootView.findViewById(R.id.book_now);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        final ArrayList<Servant> cartList = new ArrayList<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        final String firebaseId = sharedPreferences.getString("firebaseId", null);
        String dbPath = "customerPanel/cart/" + firebaseId;
        CollectionReference colRef = db.collection(dbPath);
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dialog.setMessage("Loading your cart..");
                            dialog.show();
                            cartList.clear();
                            if (getContext() != null) {
                                adapter = new servantAdapter(getContext(), R.layout.servant_list_item, cartList, 2);
                                listView = rootView.findViewById(R.id.list_cart);
                                listView.setAdapter(adapter);
                                setListViewHeightBasedOnChildren(listView);
                            }
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Servant cartItem = document.toObject(Servant.class);
                                totalCost += Integer.parseInt(cartItem.getCost());
                                cartList.add(cartItem);
                            }
                            adapter.notifyDataSetChanged();
                            setListViewHeightBasedOnChildren(listView);
                            if (cartList.size() != 0) {
                                bookNow.setVisibility(View.VISIBLE);
                                cartEmpty.setVisibility(View.GONE);
                            }
                            dialog.dismiss();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), Payment.class);
                i.putExtra("totalCost",""+totalCost);
                i.putExtra("cartBooking", true);
                getContext().startActivity(i);
            }
        });
        return rootView;
    }
}