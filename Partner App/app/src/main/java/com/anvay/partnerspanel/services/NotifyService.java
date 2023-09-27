package com.anvay.partnerspanel.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.anvay.partnerspanel.R;
import com.anvay.partnerspanel.activities.MainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class NotifyService extends Service {
    final String ACTION = "NotifyServiceAction";
    BroadcastReceiver receiver;
    Intent intents;
    int type; //0-for requirement notifications  1-for booked notifications;
    int notificationNumber = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                intents = intent;
            }
        };
        super.onCreate();
        final SharedPreferences sharedPreferences = getSharedPreferences("app", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String pincode = sharedPreferences.getString("pincode", null);
        String firebaseId = sharedPreferences.getString("firebaseId", null);
        String dbPath = "partnersPanel/maids/requirements";
        CollectionReference colRef = db.collection(dbPath);
        colRef.whereEqualTo("pincode", pincode)
                .whereEqualTo("confirmStatus", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        int firebaseChanges = 0;
                        for (QueryDocumentSnapshot doc : value) {
                            ++firebaseChanges;
                        }
                        int changes = sharedPreferences.getInt("changes", 0);
                        if (firebaseChanges > changes) {
                            notificationNumber += firebaseChanges - changes;
                            editor.putInt("changes", firebaseChanges);
                            editor.commit();
                            type = 0;
                            onStart(intents, 0, 0);
                        }
                        if (firebaseChanges < changes) {
                            editor.putInt("changes", firebaseChanges);
                            editor.commit();
                        }
//                        Toast.makeText(getApplicationContext(), "firebase:" + firebaseChanges + " changes " + changes, Toast.LENGTH_SHORT).show();
                    }
                });
        String dbPath3 = "partnersPanel/bookings/" + firebaseId;
        CollectionReference colRef3 = db.collection(dbPath3);
        final int seen = sharedPreferences.getInt("seen", 0);
        colRef3.whereEqualTo("activeStatus", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null)
                            return;
                        int changes = 0;
                        for (QueryDocumentSnapshot doc : value) {
                            ++changes;
                        }
                        Log.e("notifications", "firebase" + changes);
                        if (changes > seen) {
                            editor.putInt("seen", changes);
                            editor.commit();
                            type = 1;
                            Log.e("notifications", "seen " + seen);
                            onStart(intents, 0, 0);
                        } else {
                            editor.putInt("seen", changes);
                            editor.commit();
                        }
                    }
                });
        editor.putInt("notificationNumber", notificationNumber);
        editor.commit();
    }

    public void onStart(Intent intent, int flags, int startId) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(receiver, intentFilter);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "instantMaidNotification");
        if (type == 0)
            intent = new Intent(this, MainActivity.class);
        else if (type == 1)
            intent = new Intent(this, MainActivity.class);
        intent.putExtra("notification", "true");
        int importance = NotificationManager.IMPORTANCE_LOW;
//        NotificationChannel channel = new NotificationChannel("channel_1", "instantMaidNotification", importance);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.info);
        mBuilder.setContentTitle("Maids App");
//        channel.setShowBadge(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        if (type == 0)
            mBuilder.setContentText("You have a new work request.");
        else if (type == 1)
            mBuilder.setContentText("You have a new booking.");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());

        //  return super.onStartCommand(intent, flags, startId);
    }
}
