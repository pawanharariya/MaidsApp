package com.psh.maids.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.psh.maids.R;
import com.psh.maids.adapters.servantAdapter;
import com.psh.maids.models.Servant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class listPeople extends Fragment {
    static View rootView;
    servantAdapter adapter;
    //    TextView sunday, monday, tuesday, wednesday, thursday, friday, saturday, allDays;
//    private boolean daySelected = false;
//    View.OnClickListener day_name = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            daySelected = !daySelected;
//            if (daySelected == true)
//                v.setBackgroundResource(R.drawable.round_selected);
//            else
//                v.setBackgroundResource(R.drawable.round_not_selected);
//        }
//    };
    private String category;
    private String databasePath;

    public listPeople() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.list_people_fragment, container, false);
        TextView title = rootView.findViewById(R.id.category);
        final LinearLayout noResults = rootView.findViewById(R.id.no_results);
        ListView list = rootView.findViewById(R.id.list_servants);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        final String pincode = sharedPref.getString("pincode", null);
        title.setText(sharedPref.getString("title", "Search Results"));
        category = sharedPref.getString("category", null);
//        sunday = rootView.findViewById(R.id.sunday);
//        monday = rootView.findViewById(R.id.monday);
//        tuesday = rootView.findViewById(R.id.tuesday);
//        wednesday = rootView.findViewById(R.id.wednesday);
//        thursday = rootView.findViewById(R.id.thursday);
//        friday = rootView.findViewById(R.id.friday);
//        saturday = rootView.findViewById(R.id.saturday);
//        allDays = rootView.findViewById(R.id.all_days);
//
//        sunday.setOnClickListener(day_name);
//        monday.setOnClickListener(day_name);
//        tuesday.setOnClickListener(day_name);
//        wednesday.setOnClickListener(day_name);
//        thursday.setOnClickListener(day_name);
//        friday.setOnClickListener(day_name);
//        saturday.setOnClickListener(day_name);
//        allDays.setOnClickListener(day_name);
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Finding best results..");
        dialog.setCancelable(false);
        dialog.show();
        databasePath = "partnersPanel/maids/details/";
        final ArrayList<Servant> servantsList = new ArrayList<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection(databasePath);
        colRef.whereArrayContainsAny("category", Arrays.asList(category))
                .whereEqualTo("pincode", pincode)
                .orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error failure listener", e.toString() + e.getCause());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int count = 0;
                        if (task.isSuccessful()) {
                            Log.e("hi", "i");
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Servant servant = document.toObject(Servant.class);
                                servantsList.add(servant);
                                Log.e("inside profile", document.toString());
                                ++count;
                            }
                            if (getContext() != null) {
                                adapter = new servantAdapter(getContext(), R.layout.servant_list_item, servantsList, 1);
                                ListView listView = rootView.findViewById(R.id.list_servants);
                                listView.setAdapter(adapter);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("error tasknotsuccessful", "Error getting documents: ", task.getException());
                        }
                        if (count == 0)
                            noResults.setVisibility(View.VISIBLE);
                        dialog.dismiss();

                    }
                });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                servantProfile servantProfile = new servantProfile(getContext());
                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentHolder, servantProfile)
                        .addToBackStack("tag")
                        .commit();
                Servant currentServant = adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("currentServant", currentServant);
                servantProfile.setArguments(bundle);
            }
        });

        return rootView;
    }
}
