package com.anvay.partnerspanel.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anvay.partnerspanel.R;
import com.anvay.partnerspanel.adapters.reviewAdapter;
import com.anvay.partnerspanel.models.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class dashboard extends Fragment {
    View rootView;
    ProgressDialog dialog;
    reviewAdapter adapter;

    public dashboard() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView timeRating = rootView.findViewById(R.id.time_rating);
        final TextView taskRating = rootView.findViewById(R.id.task_rating);
        final TextView communicationRating = rootView.findViewById(R.id.communication_rating);
        final TextView behaviourRating = rootView.findViewById(R.id.behaviour_rating);
        final TextView overallRating = rootView.findViewById(R.id.overall_rating);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        String firebaseId = sharedPreferences.getString("firebaseId", null);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("loading...");
        dialog.show();
        final String dbPath = "partnersPanel/maids/ratings/";
        DocumentReference docRef = db.collection(dbPath).document(firebaseId);
        docRef.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "No ratings", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                taskRating.setText("" + document.get("taskRating"));
                                timeRating.setText("" + document.get("timeRating"));
                                behaviourRating.setText("" + document.get("behaviourRating"));
                                communicationRating.setText("" + document.get("communicationRating"));
                                overallRating.setText("" + document.get("overallRating"));
                            } else
                                Toast.makeText(getContext(), "No ratings", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getContext(), "No ratings", Toast.LENGTH_SHORT).show();

                    }
                });
        String dbPath2 = "partnersPanel/maids/reviews";
        final ArrayList<Review> reviewList = new ArrayList<>();
        CollectionReference colRef = db.collection(dbPath2);
        colRef.whereEqualTo("servantId", firebaseId)
                .orderBy("date")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error in review", e.toString());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            reviewList.clear();
                            for (DocumentSnapshot doc : task.getResult()) {
                                Review review = doc.toObject(Review.class);
                                Log.e("doc from firestore", doc.toString());
                                Log.e("doc from firestoreName", review.getName());
                                reviewList.add(review);

                            }
                            if (getContext() != null) {
                                adapter = new reviewAdapter(getContext(), R.layout.review_list_item, reviewList);
                                ListView list = rootView.findViewById(R.id.reviews);
                                list.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        dialog.dismiss();
                    }
                });
        return rootView;
    }
}
