package com.psh.maids.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.psh.maids.Activities.Payment;
import com.psh.maids.R;
import com.psh.maids.adapters.reviewAdapter;
import com.psh.maids.models.Review;
import com.psh.maids.models.Servant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class servantProfile extends Fragment {
    View rootView;
    TextView overallRating, timeRating, taskRating, behaviourRating, communicationRating, cost_info;
    Button previousInfo, feedback, addToCart, reviews, bookNow, proceed;
    Servant currentServant;
    SharedPreferences sharedPrefs;
    ProgressDialog dialog;
    ProgressDialog cartDialog;
    int exists = 0;//0 for doesn't exist and 1 for exist
    reviewAdapter adapter;
    Spinner booking_hours, booking_type;
    CircleImageView servantImage;
    LinearLayout booking_time_layout;
    int hourlyCost;
    boolean addCartItem = false;
    private TextView name, age, sex, rating, cost, availability, distance, eta, workingTime, nameOther, about, servantAddress;
    private RelativeLayout referencesDialog, feedbackDialog;
    private String currentServantId, currentservantName, currentservantMobile, currentservantAddress;
    private String pincode, firebaseId;
    private Context context;

    public servantProfile(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.servant_profile, container, false);
        sharedPrefs = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        pincode = sharedPrefs.getString("pincode", null);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        cartDialog = new ProgressDialog(getContext());
        cartDialog.setCancelable(false);
        getUI();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FloatingActionButton chat = rootView.findViewById(R.id.chat);
        currentServant = (Servant) getArguments().getSerializable("currentServant");
        Boolean instantRequirement = getArguments().getBoolean("instantRequirement");
        firebaseId = sharedPrefs.getString("firebaseId", null);
        String imageUrl = currentServant.getImageUrl();
        Picasso.with(getActivity().getBaseContext()).load(imageUrl).resize(130, 130).placeholder(R.drawable.user).into(servantImage);
        Log.e("imageUrl", currentServant.getImageUrl());
        nameOther.setText(currentServant.getNameOther());
        currentservantName = currentServant.getName();
        currentservantAddress = currentServant.getAddress();
        currentservantMobile = currentServant.getMobile();
        currentServantId = currentServant.getId();
        name.setText(currentServant.getName());
        hourlyCost = Integer.parseInt(currentServant.getCost());
        age.setText("" + currentServant.getAge());
        if (currentServant.getGender().equals("M"))
            sex.setText("Male");

        else if (currentServant.getGender().equals("F"))
            sex.setText("Female");
        cost.setText("Rs. " + currentServant.getCost() + "/hr");
        if (currentServant.isAvailability())
            availability.setText("Available");
        else
            availability.setText("Not Available");
        rating.setText("" + currentServant.getRating());
        about.setText(currentServant.getAbout());
        servantAddress.setText(currentServant.getAddress());
        if (instantRequirement) {
            bookNow.setVisibility(View.GONE);
            addToCart.setVisibility(View.GONE);
        }
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getActivity().getPackageManager();
                try {
                    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    String number = currentServant.getMobile();
                    Uri uri = Uri.parse("smsto:" + number);
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.setPackage("com.whatsapp");
                    if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(Intent.createChooser(i, ""));
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        referencesDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referencesDialog.setVisibility(View.GONE);
            }
        });
        previousInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("loading...");
                dialog.show();
                final ArrayList referencesList = new ArrayList<Review>();
                final DocumentReference docRef = db.collection("partnersPanel/maids/references/").document(currentServantId);
                docRef.get()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "No previous references", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        referencesList.clear();
                                        if (getContext() != null) {
                                            adapter = new reviewAdapter(getContext(), R.layout.review_list_item, referencesList);
                                            ListView list = rootView.findViewById(R.id.references_list);
                                            list.setAdapter(adapter);
                                        }
                                        if (doc.get("refer1") != null) {
                                            Review refer1 = new Review((Map<String, Object>) doc.get("refer1"));
                                            referencesList.add(refer1);
                                        }
                                        if (doc.get("refer2") != null) {
                                            Review refer2 = new Review((Map<String, Object>) doc.get("refer2"));
                                            referencesList.add(refer2);
                                        }
                                        adapter.notifyDataSetChanged();
                                        referencesDialog.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    } else
                                        Toast.makeText(getContext(), "No previous references", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(getContext(), "No previous references", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("loading...");
                dialog.show();
                final String dbPath = "partnersPanel/maids/ratings/";
                DocumentReference docRef = db.collection(dbPath).document(currentServantId);
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
                                        feedbackDialog.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    } else
                                        Toast.makeText(getContext(), "No ratings", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(getContext(), "No ratings", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        feedbackDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackDialog.setVisibility(View.GONE);
            }
        });
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviews reviewFragment = new reviews(currentServantId);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentHolder, reviewFragment)
                        .addToBackStack("tag")
                        .commit();
            }
        });
        booking_time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                booking_time_layout.setVisibility(View.GONE);
            }
        });
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCartItem = true;
                cost_info.setText("Cost: INR " + currentServant.getCost() + "/hour");
                booking_time_layout.setVisibility(View.VISIBLE);
            }
        });

        bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cost_info.setText("Cost: INR " + currentServant.getCost() + "/hour");
                booking_time_layout.setVisibility(View.VISIBLE);
            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalCost;
                int hours = Integer.parseInt(booking_hours.getSelectedItem().toString());
                totalCost = hourlyCost * hours;
                String type = booking_type.getSelectedItem().toString();
                if (type.equals("Weekly"))
                    totalCost = totalCost * 7;
                else if (type.equals("Monthly"))
                    totalCost = totalCost * 30;
                else
                    totalCost = totalCost * 1;
                if (addCartItem) {
                    cartDialog.setMessage("Adding to your cart..");
                    cartDialog.show();
                    addToCartItem(totalCost);
                } else {
                    Intent i = new Intent(getContext(), Payment.class);
                    i.putExtra("totalCost", "" + totalCost);
                    i.putExtra("servantName", currentservantName);
                    i.putExtra("servantMobile", currentservantMobile);
                    i.putExtra("servantAddress", currentservantAddress);
                    i.putExtra("info", hours + " per day on " + type + " basis");
                    i.putExtra("servantId", currentServantId);
                    i.putExtra("instantBooking", false);
                    i.putExtra("cartBooking", false);
                    startActivity(i);
                }
                //TODO changes for previous bookings..............................
            }
        });

        return rootView;
    }

    private void getUI() {
        name = rootView.findViewById(R.id.name);
        age = rootView.findViewById(R.id.age);
        sex = rootView.findViewById(R.id.sex);
        about = rootView.findViewById(R.id.about);
        rating = rootView.findViewById(R.id.rating);
        nameOther = rootView.findViewById(R.id.name_other);
        servantAddress = rootView.findViewById(R.id.address);
        cost = rootView.findViewById(R.id.cost);
        availability = rootView.findViewById(R.id.availability);
        distance = rootView.findViewById(R.id.distance);
        previousInfo = rootView.findViewById(R.id.previous_info);
        referencesDialog = rootView.findViewById(R.id.references_dialog);
        feedbackDialog = rootView.findViewById(R.id.feedback_dialog);
        feedback = rootView.findViewById(R.id.feedback);
        overallRating = rootView.findViewById(R.id.overall_rating);
        timeRating = rootView.findViewById(R.id.time_rating);
        taskRating = rootView.findViewById(R.id.task_rating);
        communicationRating = rootView.findViewById(R.id.communication_rating);
        behaviourRating = rootView.findViewById(R.id.behaviour_rating);
        addToCart = rootView.findViewById(R.id.add_to_cart);
        bookNow = rootView.findViewById(R.id.book_button);
        reviews = rootView.findViewById(R.id.reviews);
        servantImage = rootView.findViewById(R.id.servant_image);
        proceed = rootView.findViewById(R.id.proceed);
        booking_hours = rootView.findViewById(R.id.booking_hours);
        booking_type = rootView.findViewById(R.id.booking_type);
        booking_time_layout = rootView.findViewById(R.id.booking_time_layout);
        cost_info = rootView.findViewById(R.id.cost_info);
    }

    private void addToCartItem(int totalCost) {
        Servant cartItem = new Servant(currentServant.getId(), currentServant.getName(), currentServant.getAge(), currentServant.getGender(), "" + totalCost, true, currentServant.getImageUrl(),currentservantAddress,currentservantMobile);
        String dbPath = "customerPanel/cart/" + firebaseId;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(dbPath).document(currentServantId)
                .set(cartItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Added to your cart", Toast.LENGTH_SHORT).show();
                        booking_time_layout.setVisibility(View.GONE);
                        cartDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
