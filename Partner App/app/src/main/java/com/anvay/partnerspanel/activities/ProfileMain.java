package com.anvay.partnerspanel.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anvay.partnerspanel.R;
import com.anvay.partnerspanel.models.Servant;
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
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileMain extends AppCompatActivity {
    private EditText nameTextView, mobileTextView, cityTextView, localityTextView, pincodeTextView, aboutTextView, nameOtherTextView, costTextView, ageTextView;
    private RadioButton male, female;
    private CheckBox maid, japaMaid, officeBoy;
    private CircleImageView profilePic;
    private ImageView profilePicIcon;
    private Button createProfile;
    private LinearLayout loadWall;
    private Bitmap profileImage, bmp, img;
    private Uri photoUri;
    private List<String> category;
    private String name, city, locality, imageUrl, pincode, mobile, gender, about, address, nameOther, firebaseId, age, cost;
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
        getElements();
        category = new ArrayList<>();
        sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        firebaseId = sharedPreferences.getString("firebaseId", null);
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
        mobileTextView.setText(sharedPreferences.getString("phoneNumber", null));
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "M";
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "F";
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
                age = ageTextView.getText().toString();
                cost = costTextView.getText().toString();
                about = aboutTextView.getText().toString();
                nameOther = nameOtherTextView.getText().toString();
                address = locality + ", " + city;
                //TODO checkboxes error
                if (maid.isChecked())
                    category.add("maids");
                if (japaMaid.isChecked())
                    category.add("japaMaids");
                if (officeBoy.isChecked())
                    category.add("officeBoys");
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(pincode) || TextUtils.isEmpty(city) || TextUtils.isEmpty(locality) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(cost) ||
                        TextUtils.isEmpty(nameOther) || TextUtils.isEmpty(age) || TextUtils.isEmpty(about) || category.isEmpty()) {
                    Toast.makeText(ProfileMain.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } else if (byteArray == null) {
                    Toast.makeText(ProfileMain.this, "Please upload your image", Toast.LENGTH_SHORT).show();
                } else {
                    loadWall.setVisibility(View.VISIBLE);
                    postPicture();
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
        final StorageReference profileImages = FirebaseStorage.getInstance().getReference().child("ServantProfileImages/" + firebaseId);
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
        Servant servant = new Servant(firebaseId, name, nameOther, age, gender, about, category, cost, imageUrl, address, mobile, pincode, true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("/partnersPanel/maids/details/").document(firebaseId);
        docRef.set(servant, SetOptions.merge())
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
                        editor.putString("nameOther", nameOther);
                        editor.putString("age", "" + age);
                        editor.putString("cost", "" + cost);
                        editor.putString("about", about);
                        editor.putBoolean("availability", true);
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

    private void getElements() {
        nameTextView = findViewById(R.id.name);
        mobileTextView = findViewById(R.id.mobileNumber);
        cityTextView = findViewById(R.id.city);
        localityTextView = findViewById(R.id.locality);
        pincodeTextView = findViewById(R.id.pincode);
        ageTextView = findViewById(R.id.age);
        aboutTextView = findViewById(R.id.about);
        nameOtherTextView = findViewById(R.id.nameOther);
        costTextView = findViewById(R.id.workRate);
        male = findViewById(R.id.gender_male);
        female = findViewById(R.id.gender_female);
        maid = findViewById(R.id.category_maid);
        japaMaid = findViewById(R.id.category_maid);
        officeBoy = findViewById(R.id.category_officeBoy);
        profilePic = findViewById(R.id.profilePicture);
        profilePicIcon = findViewById(R.id.galleryView);
        createProfile = findViewById(R.id.createProfile);
        loadWall = findViewById(R.id.loadwall);
    }
}
