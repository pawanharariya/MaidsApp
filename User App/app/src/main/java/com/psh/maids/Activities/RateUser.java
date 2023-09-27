package com.psh.maids.Activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.psh.maids.R;
import com.psh.maids.models.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RateUser extends AppCompatActivity {
    String servantFirebaseId;
    Button submitRating;
    RatingBar taskRating, timeRating, communicationRating, behaviourRating;
    EditText comment;
    Switch isReferredSwitch;
    FirebaseFirestore db;
    double commR, timeR, taskR, behaR, overR, users;
    Review refer1, refer2;
    ProgressDialog dialog;
    private double givenRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_user);
        getElements();
        db = FirebaseFirestore.getInstance();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            servantFirebaseId = extras.getString("servantFirebaseId");
        }
        Log.e("firebaseId at rate", servantFirebaseId);
        dialog = new ProgressDialog(RateUser.this);
        dialog.setMessage("Rating the user");
        dialog.setCancelable(false);
        submitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRating();
            }
        });
    }

    private void getElements() {
        submitRating = findViewById(R.id.submit_rating);
        taskRating = findViewById(R.id.task_rating);
        timeRating = findViewById(R.id.time_rating);
        communicationRating = findViewById(R.id.communication_rating);
        behaviourRating = findViewById(R.id.behaviour_rating);
        comment = findViewById(R.id.comment);
        isReferredSwitch = findViewById(R.id.isReferred);
    }

    private void fetchRating() {
        dialog.show();
        taskR = (double) taskRating.getRating();
        timeR = (double) timeRating.getRating();
        behaR = (double) behaviourRating.getRating();
        commR = (double) communicationRating.getRating();
        overR = (taskR + timeR + behaR + commR) / 4.0;
        givenRating = overR;
        Log.e("inputData", "task " + taskR + "time " + timeR + "behave " + behaR + "comm " + commR + "overall " + overR);
        final DocumentReference docRef = db.collection("partnersPanel/maids/ratings").document(servantFirebaseId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        users = (document.getDouble("number") + 1);
                        taskR = (taskR + (document.getDouble("taskRating")) / users);
                        timeR = (timeR + (document.getDouble("timeRating")) / users);
                        commR = (commR + (document.getDouble("communicationRating")) / users);
                        behaR = (behaR + (document.getDouble("behaviourRating")) / users);
                        overR = (overR + (document.getDouble("overallRating")) / users);
                    } else
                        users = 1.0;
                    DecimalFormat df = new DecimalFormat("#.#");
                    taskR = Double.parseDouble(df.format(taskR));
                    timeR = Double.parseDouble(df.format(timeR));
                    commR = Double.parseDouble(df.format(commR));
                    behaR = Double.parseDouble(df.format(behaR));
                    overR = Double.parseDouble(df.format(overR));
                    Log.e("fetch rating", document.toString());
                    Log.e("outputData", "task " + taskR + "time " + timeR + "behave " + behaR + "comm " + commR + "overall " + overR);
                    postRating(taskR, timeR, behaR, commR, overR, users);

                }
            }
        });
    }

    private void postRating(Double taskR, Double timeR, Double behaR, Double commR, final Double overR, Double users) {
        Map<String, Object> ratings = new HashMap<>();
        ratings.put("taskRating", taskR);
        ratings.put("timeRating", timeR);
        ratings.put("behaviourRating", behaR);
        ratings.put("communicationRating", commR);
        ratings.put("overallRating", overR);
        ratings.put("number", users);
        //post in ratings collection of servants
        final DocumentReference docRef = db.collection("partnersPanel/maids/ratings").document(servantFirebaseId);
        docRef.set(ratings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //post in individual servant's document
                        Map<String, Object> rating = new HashMap<>();
                        rating.put("rating", overR);
                        DocumentReference docRef2 = db.collection("partnersPanel/maids/details").document(servantFirebaseId);
                        docRef2.set(rating, SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        postComment(overR);
                                    }
                                });
                    }
                });

    }

    private void postComment(Double overR) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", null);
        String city = sharedPreferences.getString("city", null);
        String imageUrl = sharedPreferences.getString("imageUrl", null);
        String commentReview = String.valueOf(comment.getText());
        final Review review = new Review(name, givenRating, commentReview, formattedDate, city, servantFirebaseId, imageUrl);
        DocumentReference docRef = db.collection("partnersPanel/maids/reviews").document();
        docRef.set(review)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (isReferredSwitch.isChecked()) {
                            getReferral(review);
                        } else {
                            Toast.makeText(RateUser.this, "Review posted", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            finish();
                        }
                    }
                });
    }

    private void getReferral(final Review refer3) {
        final DocumentReference docRef = db.collection("partnersPanel/maids/references/").document(servantFirebaseId);
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                if (doc.get("refer1") != null)
                                    refer1 = new Review((Map<String, Object>) doc.get("refer1"));
                                if (doc.get("refer2") != null)
                                    refer2 = new Review((Map<String, Object>) doc.get("refer2"));
                                if (refer2 == null) {
                                    postReferral(refer1, refer3);
                                } else if (refer1 == null) {
                                    postReferral(refer3, null);
                                } else postReferral(refer2, refer3);
                            } else
                                postReferral(refer3, refer3);
                        }
                    }
                });

    }

    private void postReferral(Review refer1, Review refer2) {
        final DocumentReference docRef = db.collection("partnersPanel/maids/references/").document(servantFirebaseId);
        Map<String, Object> refer = new HashMap<>();
        refer.put("refer2", refer2);
        refer.put("refer1", refer1);
        docRef.set(refer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        Toast.makeText(RateUser.this, "Review posted", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();
                    }
                });
    }
}
