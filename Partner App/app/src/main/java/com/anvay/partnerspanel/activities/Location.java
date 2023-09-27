package com.anvay.partnerspanel.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anvay.partnerspanel.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Location extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Double lat = null;
    Double lng = null;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Button changePincode = findViewById(R.id.change_pincode);
        Button save = findViewById(R.id.save);
        TextView userLocation = findViewById(R.id.user_location);
        userLocation.setText(sharedPreferences.getString("address", null));
        Button map = findViewById(R.id.map);
        final EditText pincode = findViewById(R.id.pincode);
        pincode.setText(sharedPreferences.getString("pincode", null));
        changePincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pincode.setText(null);
                pincode.setEnabled(true);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("pincode", pincode.getText().toString());
                editor.commit();
                Toast.makeText(Location.this, "Pincode Changed Successfully", Toast.LENGTH_SHORT).show();
                pincode.setEnabled(false);
            }
        });

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        buildGoogleApiClient();

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
//                startActivity(i);
                String currentLocation = lat + "," + lng;
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                        Uri.parse("http://maps.google.com/maps?saddr=" + currentLocation));
////                        Uri.parse("http://maps.google.com/maps?saddr=" + currentLocation + "&daddr=" + destinationLocation));

                //startActivity(intent);
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
                Intent intents = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intents);
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Location.this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                if (location != null) {
                    lat = (location.getLatitude());
                    lng = (location.getLongitude());
                    Log.e("location", lat + "   " + lng);
                    SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String currentLocation = lat + "," + lng;
                    editor.putString("currentLocation", currentLocation);
                    editor.commit();
                    updateOnFirebase();
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("connection", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("failed", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    private void updateOnFirebase() {
        String firebaseId = "firebaseId12";
        GeoPoint gp = new GeoPoint((lat), (lng));
        Map<String, GeoPoint> geopoint = new HashMap<>();
        geopoint.put("location", gp);
        DocumentReference location = db.collection("/partnersPanel/locations/currentLocations/").document(firebaseId);
        location.set(geopoint)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("success", "updated on firebase");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failed", e.toString());
            }
        });

    }
}
