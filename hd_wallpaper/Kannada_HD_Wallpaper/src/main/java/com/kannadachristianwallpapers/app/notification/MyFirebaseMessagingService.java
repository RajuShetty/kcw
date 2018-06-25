package com.kannadachristianwallpapers.app.notification;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.activity.MainActivity;
import com.kannadachristianwallpapers.app.data.constant.AppConstants;
import com.kannadachristianwallpapers.app.data.preference.AppPreference;
import com.kannadachristianwallpapers.app.data.sqlite.NotDbController;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > AppConstants.VALUE_ZERO) {
            Map<String, String> params = remoteMessage.getData();

            if (AppPreference.getInstance(MyFirebaseMessagingService.this).isNotificationOn()) {
                sendNotification(params.get("type"), params.get("title"), params.get("message"), params.get("id"));
            }
        }
    }

    private void sendNotification(String type, String title, String message, String id) {

        new NotDbController(getApplicationContext()).insertData(title, message, id, type);

        Intent intent = null;
        if (type != null && !type.isEmpty() && type.equals(AppConstants.NOTIFY_TYPE_MESSAGE)) {
            intent = new Intent(this, MainActivity.class);
        }/* else {
            intent = new Intent(this, MainActivity.class);
        }*/

        if(intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, AppConstants.VALUE_ZERO, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_ic_notification)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{1000, 1000})
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(0, notificationBuilder.build());
            }
        }
    }
}
