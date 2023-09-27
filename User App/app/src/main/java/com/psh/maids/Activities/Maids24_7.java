package com.psh.maids.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.psh.maids.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Maids24_7 extends AppCompatActivity {

    Button postRequirement, elderlyCare, cooking, cleaning, babySitting;
    String category = null, comment;
    int salary = 0;
    EditText salaryText, commentText;
    Drawable selectedBg, unSelectedBg;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maids24_7);
        getUI();
        sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        selectedBg = ContextCompat.getDrawable(Maids24_7.this, R.drawable.green_corner);
        unSelectedBg = ContextCompat.getDrawable(Maids24_7.this, R.drawable.default_button);
        elderlyCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "Elderly Care";
                elderlyCare.setBackground(selectedBg);
                cooking.setBackground(unSelectedBg);
                babySitting.setBackground(unSelectedBg);
                cleaning.setBackground(unSelectedBg);

            }
        });
        cooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "Cooking";
                elderlyCare.setBackground(unSelectedBg);
                cooking.setBackground(selectedBg);
                babySitting.setBackground(unSelectedBg);
                cleaning.setBackground(unSelectedBg);
            }
        });
        cleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "Cleaning";
                elderlyCare.setBackground(unSelectedBg);
                cooking.setBackground(unSelectedBg);
                babySitting.setBackground(unSelectedBg);
                cleaning.setBackground(selectedBg);
            }
        });
        babySitting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "Baby Sitting";
                elderlyCare.setBackground(unSelectedBg);
                cooking.setBackground(unSelectedBg);
                babySitting.setBackground(selectedBg);
                cleaning.setBackground(unSelectedBg);
            }
        });
        postRequirement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment = commentText.getText().toString();
                if (category == null) {
                    Toast.makeText(Maids24_7.this, "Please select required category.", Toast.LENGTH_SHORT).show();
                } else if (salaryText.getText().toString() == null) {
                    Toast.makeText(Maids24_7.this, "Please enter expected salary you can pay.", Toast.LENGTH_SHORT).show();
                } else {
                    final ProgressDialog dialog = new ProgressDialog(Maids24_7.this);
                    dialog.setMessage("Posting requirement");
                    dialog.setCancelable(false);
                    dialog.show();
                    salary = Integer.parseInt(salaryText.getText().toString());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String firebaseId = sharedPreferences.getString("firebaseId", null);
                    String name = sharedPreferences.getString("name", null);
                    String address = sharedPreferences.getString("locality", null) + ", " + sharedPreferences.getString("city", null);
                    String pincode = sharedPreferences.getString("pincode", null);
                    Map<String, Object> details = new HashMap<>();
                    details.put("customerId", firebaseId);
                    details.put("pincode", pincode);
                    details.put("category", category);
                    details.put("confirmedId", null);
                    details.put("customerName", name);
                    details.put("requirementType", "24X7 maid");
                    details.put("address", address);
                    details.put("confirmStatus", false);
                    details.put("salary", salary);
                    details.put("comment", comment);
                    db.collection("/partnersPanel/maids/requirements/").document()
                            .set(details)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    editor.putBoolean("seen", false);
                                    editor.commit();
                                    dialog.dismiss();
                                    Toast.makeText(Maids24_7.this, "Requirement posted successfully", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(Maids24_7.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Maids24_7.this, "Unknown Error!", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                }
            }
        });
    }

    private void getUI() {
        postRequirement = findViewById(R.id.post_requirement_button);
        elderlyCare = findViewById(R.id.elderly_care);
        cooking = findViewById(R.id.cooking);
        cleaning = findViewById(R.id.cleaning);
        babySitting = findViewById(R.id.baby_sitting);
        salaryText = findViewById(R.id.salary);
        commentText = findViewById(R.id.comment);
    }
}
