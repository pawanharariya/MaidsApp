package com.psh.maids.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.psh.maids.R;
import com.psh.maids.models.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileMain extends AppCompatActivity {
    private EditText nameTextView, mobileTextView, cityTextView, localityTextView, pincodeTextView, pincodeEntered;
    private CircleImageView profilePic;
    private ImageView profilePicIcon;
    private Button createProfile, checkPincode;
    private LinearLayout loadWall, pincodeLayout;
    private Bitmap profileImage, bmp, img;
    private Uri photoUri;
    private String name, city, locality, imageUrl, pincode, mobile;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private byte[] byteArray;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);
        nameTextView = findViewById(R.id.name);
        mobileTextView = findViewById(R.id.mobileNumber);
        pincodeEntered = findViewById(R.id.pincode_entered);
        checkPincode = findViewById(R.id.check_pincode);
        pincodeLayout = findViewById(R.id.pincode_layout);
        cityTextView = findViewById(R.id.city);
        localityTextView = findViewById(R.id.locality);
        pincodeTextView = findViewById(R.id.pincode);
        profilePic = findViewById(R.id.profilePicture);
        profilePicIcon = findViewById(R.id.galleryView);
        createProfile = findViewById(R.id.createProfile);
        loadWall = findViewById(R.id.loadwall);
        sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mobileTextView.setText(sharedPreferences.getString("phoneNumber", null));
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePicIcon.performClick();
            }
        });
        profilePicIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (gallery.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(gallery, 007);
                }
            }
        });
        checkPincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(pincodeEntered.getText().toString())) {
                    int pin = Integer.parseInt(pincodeEntered.getText().toString());
                    if (pin < 110001 || pin > 110099) {
                        Toast.makeText(ProfileMain.this, "Sorry, We are not Servicing in your area as of now. We will intimate you once we start working in your area. ", Toast.LENGTH_SHORT).show();
                    } else {
                        pincodeTextView.setText(String.valueOf(pin));
                        pincodeLayout.setVisibility(View.GONE);

                    }
                } else
                    Toast.makeText(ProfileMain.this, "Enter Pincode to continue", Toast.LENGTH_SHORT).show();
            }
        });
        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameTextView.getText().toString();
                mobile = mobileTextView.getText().toString();
                pincode = pincodeTextView.getText().toString();
                city = cityTextView.getText().toString();
                locality = localityTextView.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(city) || TextUtils.isEmpty(locality) || TextUtils.isEmpty(mobile)) {
                    Log.e("details", "N" + name + "M" + mobile + "P" + pincode + "c" + city + "L" + locality);
                    Toast.makeText(ProfileMain.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    loadWall.setVisibility(View.VISIBLE);
                    if (byteArray != null)
                        postPicture();
                    else createProfile();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            photoUri = data.getData();
            try {
                profileImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                img = getResizedBitmap(profileImage, 150);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 70, bs);
                byteArray = bs.toByteArray();
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                profilePic.setImageBitmap(bmp);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ProfileMain.this, "Cannot load this image", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void postPicture() {
        String firebaseId = sharedPreferences.getString("firebaseId", null);
        final StorageReference profileImages = FirebaseStorage.getInstance().getReference().child("profileImages/" + firebaseId);
        UploadTask uploadTask = profileImages.putBytes(byteArray);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileImages.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();
                        createProfile();
                    }
                });
            }
        });

    }

    private void createProfile() {
        Customer customer = new Customer(name, mobile, city, locality, imageUrl);
        String firebaseId = sharedPreferences.getString("firebaseId", null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("/customerPanel/authUsers/users/").document(firebaseId);
        docRef.set(customer, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editor.putBoolean("profileCreated", true);
                        editor.putString("name", name);
                        editor.putString("mobile", mobile);
                        editor.putString("city", city);
                        editor.putString("locality", locality);
                        editor.putString("pincode", pincode);
                        editor.putString("imageUrl", imageUrl);
                        editor.commit();
                        Intent i = new Intent(ProfileMain.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });

    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
