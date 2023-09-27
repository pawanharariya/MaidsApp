package com.psh.maids.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.psh.maids.R;
import com.psh.maids.adapters.reviewAdapter;
import com.psh.maids.models.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class reviews extends Fragment {
    reviewAdapter adapter;
    private String mServantId;

    public reviews(String servantId) {
        this.mServantId = servantId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.reviews, container, false);
        final ArrayList<Review> reviewList = new ArrayList<>();
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        final TextView noReviews = rootView.findViewById(R.id.no_reviews);
        dialog.setMessage("loading..");
        dialog.setCancelable(false);
        dialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String dbPath = "partnersPanel/maids/reviews";
        CollectionReference colRef = db.collection(dbPath);
        colRef.whereEqualTo("servantId", mServantId)
                .orderBy("date")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error in review", e.toString());
                        noReviews.setVisibility(View.VISIBLE);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            reviewList.clear();
                            noReviews.setVisibility(View.GONE);
                            if (getContext() != null) {
                                adapter = new reviewAdapter(getContext(), R.layout.review_list_item, reviewList);
                                ListView list = rootView.findViewById(R.id.review_list);
                                list.setAdapter(adapter);
                            }
                            for (DocumentSnapshot doc : task.getResult()) {
                                Review review = doc.toObject(Review.class);
                                Log.e("doc from firestore", doc.toString());
                                Log.e("doc from firestoreName", review.getName());
                                reviewList.add(review);
                            }
                            dialog.dismiss();
                            adapter.notifyDataSetChanged();
                        } else
                            noReviews.setVisibility(View.VISIBLE);
                    }
                });
        return rootView;
    }
}
