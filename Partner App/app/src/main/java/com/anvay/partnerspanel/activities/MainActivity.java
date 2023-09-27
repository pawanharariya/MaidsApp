package com.anvay.partnerspanel.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.anvay.partnerspanel.R;
import com.anvay.partnerspanel.fragments.dashboard;
import com.anvay.partnerspanel.fragments.home;
import com.anvay.partnerspanel.fragments.profile;
import com.anvay.partnerspanel.services.NotifyService;

public class MainActivity extends AppCompatActivity {
    int color = 0xffD50000;
    int defaultColor = 0xff000000;
    LinearLayout exitDialog;
    Drawable home_image, profile_image, dashboard_image;
    ImageView home_button, profile_button, cart_button, dashboard_button, notification;
    TextView confirmExit, ignoreExit, profile, home, dashboard;

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            exitDialog.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getUI();
        SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("login", false)) {
            Intent i = new Intent(MainActivity.this, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        if (!sharedPreferences.getBoolean("profileCreated", false)) {
            Intent i = new Intent(MainActivity.this, ProfileMain.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        Intent i = new Intent(getApplicationContext(), NotifyService.class);
        startService(i);
        ignoreExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.setVisibility(View.GONE);
            }
        });
        confirmExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_image.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                dashboard_image.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.SRC_IN));
                home_image.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.SRC_IN));
                profile_button.setImageDrawable(profile_image);
                home_button.setImageDrawable(home_image);
                dashboard_button.setImageDrawable(dashboard_image);
                profile.setTextColor(color);
                home.setTextColor(defaultColor);
                dashboard.setTextColor(defaultColor);

                profile profileFragment = new profile();
                FragmentManager fragmentManager = getSupportFragmentManager();
                for (int i = 0; i <= fragmentManager.getBackStackEntryCount(); i++)
                    fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_holder, profileFragment)
                        .commit();

            }
        });
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_image.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.SRC_IN));
                dashboard_image.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.SRC_IN));
                home_image.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                profile_button.setImageDrawable(profile_image);
                home_button.setImageDrawable(home_image);
                dashboard_button.setImageDrawable(dashboard_image);
                profile.setTextColor(defaultColor);
                home.setTextColor(color);
                dashboard.setTextColor(defaultColor);


                home homeFragment = new home();
                FragmentManager fragmentManager = getSupportFragmentManager();
                for (int i = 0; i <= fragmentManager.getBackStackEntryCount(); i++)
                    fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_holder, homeFragment)
                        .commit();
            }
        });
        dashboard_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_image.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.SRC_IN));
                dashboard_image.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                home_image.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.SRC_IN));
                profile_button.setImageDrawable(profile_image);
                home_button.setImageDrawable(home_image);
                dashboard_button.setImageDrawable(dashboard_image);
                profile.setTextColor(defaultColor);
                home.setTextColor(defaultColor);
                dashboard.setTextColor(color);
                dashboard dashboardFragment = new dashboard();
                FragmentManager fragmentManager = getSupportFragmentManager();
                for (int i = 0; i <= fragmentManager.getBackStackEntryCount(); i++)
                    fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_holder, dashboardFragment)
                        .commit();
            }
        });
        home_button.performClick();
    }

    private void getUI() {
        confirmExit = findViewById(R.id.confirm_exit);
        exitDialog = findViewById(R.id.exit_dialog);
        ignoreExit = findViewById(R.id.ignore_exit);
        profile_image = ContextCompat.getDrawable(MainActivity.this, R.drawable.user);
        home_image = ContextCompat.getDrawable(MainActivity.this, R.drawable.home);
        dashboard_image = ContextCompat.getDrawable(MainActivity.this, R.drawable.dashboard);
        profile = findViewById(R.id.profileText);
        dashboard = findViewById(R.id.dashboardText);
        home_button = findViewById(R.id.home_image);
        dashboard_button = findViewById(R.id.dashboard_image);
        profile_button = findViewById(R.id.profile_image);
        home = findViewById(R.id.homeText);
        notification = findViewById(R.id.notification_button);

    }
}
