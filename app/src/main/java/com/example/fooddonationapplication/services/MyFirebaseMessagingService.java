package com.example.fooddonationapplication.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.ui.social_community.MainSocialCommunityActivity;
import com.example.fooddonationapplication.ui.social_community.history.UpdateEventActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import static android.app.Notification.DEFAULT_ALL;
import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String CHANNEL_ID = "admin_channel";
    private static final String SUBSCRIBE_TO = "FoodDonation";
    private static final String TAG = "FbMessagingService";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i(TAG, "onTokenRefresh completed with token: " + token);
        FirebaseMessaging FCM = FirebaseMessaging.getInstance();

        // Once the token is generated, subscribe to topic with the userId
        FCM.subscribeToTopic(SUBSCRIBE_TO);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);

        createNotificationChannel(notificationManager);

        Intent intent = new Intent(this, UpdateEventActivity.class); // the activity you want the notification to open
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.donation_logo);

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_error_black_24dp)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("message")))
                .setAutoCancel(true)
                .setSound(notificationSoundUri)
                .setPriority(PRIORITY_DEFAULT)
                .setDefaults(DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        //Set notification color to match your app color template
        notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {
        final CharSequence CHANNEL_NAME = "New notification";
        final String CHANNEL_DESCRIPTION = "Notifications for app xyz";

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(CHANNEL_DESCRIPTION);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);
    }
}