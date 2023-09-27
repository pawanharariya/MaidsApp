package com.psh.maids.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.psh.maids.R;
import com.psh.maids.models.PaymentDetails;
import com.psh.maids.models.Servant;
import com.psh.maids.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Payment extends AppCompatActivity implements PaymentResultListener {
    private String totalCost;
    private String servantId, info, servantName, servantAddress, servantMobile, paymentId, firebaseId, documentId;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private ProgressDialog dialog, dialog2;
    private TextView total_cost;
    private Boolean instantBooking = false, cartBooking = false;
    private Servant servant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Button pay = findViewById(R.id.pay);
        total_cost = findViewById(R.id.total_cost);
        db = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog2 = new ProgressDialog(this);
        dialog2.setCancelable(false);
        sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        firebaseId = sharedPreferences.getString("firebaseId", null);
        totalCost = getIntent().getStringExtra("totalCost");
        if (getIntent().getBooleanExtra("cartBooking", false)) {
            cartBooking = true;
        } else {
            servantName = getIntent().getStringExtra("servantName");
            servantAddress = getIntent().getStringExtra("servantAddress");
            servantMobile = getIntent().getStringExtra("servantMobile");
            info = getIntent().getStringExtra("info");
            instantBooking = getIntent().getBooleanExtra("instantBooking", false);
            servantId = getIntent().getStringExtra("servantId");
        }
        if (instantBooking)
            documentId = getIntent().getStringExtra("documentId");
        Log.e("extras found", totalCost + info + servantId);
        total_cost.setText("Total Cost INR " + totalCost);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });
    }

    public void startPayment() {
        Checkout checkout = new Checkout();
//        checkout.setKeyID("rzp_test_sVrW5vtI3Nd2pZ\t");
        checkout.setImage(R.drawable.maidicon);
//        try {
//            JSONObject orderRequest = new JSONObject();
//            orderRequest.put("amount", 50000); // amount in the smallest currency unit
//            orderRequest.put("currency", "INR");
//            orderRequest.put("receipt", "order_rcptid_11");
//            orderRequest.put("payment_capture", false);
//
//            StructuredQuery.Order order = razorpay.Orders.create(orderRequest);
//        } catch (RazorpayException e) {
//            // Handle Exception
//            System.out.println(e.getMessage());
//        }
        /**
         * Reference to current activity
         */
        final Activity activity = Payment.this;
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "Merchant Name");
            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//            options.put("order_id", "1");
            options.put("currency", "INR");
            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", totalCost + "00");
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("error", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.e("Payment Success", s);
        Toast.makeText(Payment.this, "Payment Successful", Toast.LENGTH_SHORT).show();
        Checkout.clearUserData(this);
        paymentId = s;
        if (cartBooking) cartBooking();
        else postData();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e("payment error", s);
        if (i == Checkout.NETWORK_ERROR) {
            Log.e("error payment failed", "Checkout.NETWORK_ERROR");
            Toast.makeText(this, "Poor Network, Payment Failed", Toast.LENGTH_SHORT).show();
        }
        if (i == Checkout.INVALID_OPTIONS) {
            Log.e("error payment failed", "Checkout.INVALID_OPTIONS");
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();

        }
        if (i == Checkout.PAYMENT_CANCELED) {
            Log.e("error payment failed", "Checkout.PAYMENT_CANCELED");
            Toast.makeText(this, "Payment Canceled by user", Toast.LENGTH_SHORT).show();

        }
        if (i == Checkout.TLS_ERROR) {
            Log.e("error payment failed", "Checkout.TLS_ERROR");
            Toast.makeText(this, "Payment Not supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void postData() {
        //post in Partners Panel
        dialog.setMessage("Booking..");
        dialog.setCancelable(false);
        dialog.show();
        String customerName = sharedPreferences.getString("name", null);
        String address = sharedPreferences.getString("address", null);
        String contact = sharedPreferences.getString("mobile", null);
        DocumentReference docRef = db.collection("partnersPanel/bookings/" + servantId).document();
        User customer = new User(firebaseId, customerName, contact, address, true);
        docRef.set(customer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        postCustomerPanel();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Payment.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    private void postCustomerPanel() {
        //post in Customer Panel
        DocumentReference docRef2 = db.collection("customerPanel/bookings/" + firebaseId).document();
        User servant = new User(servantId, servantName, servantMobile, servantAddress, true);
        docRef2.set(servant)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        postPaymentDetails();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Payment.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    private void postPaymentDetails() {
        //post PaymentDetails
        DocumentReference docRef = db.collection("customerPanel/payments/payment").document();
        PaymentDetails paymentDetails = new PaymentDetails(firebaseId, servantId, paymentId, String.valueOf(totalCost), info);
        docRef.set(paymentDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteInstantRequirement();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Payment.this, "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteInstantRequirement() {
        if (instantBooking) {
            DocumentReference docRef3 = db.collection("partnersPanel/maids/requirements").document(documentId);
            if (docRef3 != null) {
                docRef3.delete()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Payment.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                dialog2.dismiss();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Payment.this, "Booking Successful", Toast.LENGTH_SHORT).show();
                                Log.e("post in customerPanel", "posted");
                                dialog.dismiss();
                                dialog2.dismiss();
                                Toast.makeText(Payment.this, "Booking Successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(Payment.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        });
            }
        } else {
            Toast.makeText(Payment.this, "Booking Successful", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Payment.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    private void cartBooking() {
        dialog2.setMessage("Making your Bookings..");
        dialog2.show();
        String dbPath = "customerPanel/cart/" + firebaseId;
        CollectionReference colRef = db.collection(dbPath);
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Servant servant = document.toObject(Servant.class);
                                servantId = servantId + ";" + servant.getId();
                                makeBooking(servant);
                            }
                            info = "cart";
                            postPaymentDetails();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void makeBooking(final Servant servant) {
        //post in Partners Panel
        String customerName = sharedPreferences.getString("name", null);
        String contact = sharedPreferences.getString("mobile", null);
        String address = sharedPreferences.getString("address", null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("partnersPanel/bookings/" + servant.getId()).document();
        User customer = new User(firebaseId, customerName, contact, address, true);
        docRef.set(customer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        postCustomerPanelCart(servant);
//                        Toast.makeText(Payment.this, "Booking Confirmed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Payment.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void postCustomerPanelCart(final Servant servant) {
        //post in Customer Panel
        DocumentReference docRef2 = db.collection("customerPanel/bookings/" + firebaseId).document();
        User servantBooked = new User(servant.getId(), servant.getName(), servant.getMobile(), servant.getAddress(), true);
        docRef2.set(servantBooked)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                                Toast.makeText(Payment.this, "Booking Confirmed", Toast.LENGTH_SHORT).show();
                        deleteFromCart(servant);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Payment.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteFromCart(Servant servant) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("customerPanel/cart/" + firebaseId).document(servant.getId());
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }
}

