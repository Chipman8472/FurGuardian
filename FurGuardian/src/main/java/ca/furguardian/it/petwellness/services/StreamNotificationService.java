package ca.furguardian.it.petwellness.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ca.furguardian.it.petwellness.MainActivity;
import ca.furguardian.it.petwellness.R;

public class StreamNotificationService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "stream_ready_channel";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // 1. Extract the notification title and body
        String title = "Your Pet Wants to Talk!";
        String body = "Your live stream is now active! Tap to view it.";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // 2. Create an Intent to open your MainActivity (or a specific activity)
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // 3. Create a PendingIntent so that tapping the notification opens your app
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE // or FLAG_UPDATE_CURRENT, depending on your needs
        );

        // 4. Build the notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.healthguardian) // Replace with your own icon
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // 5. Create the notification channel (for Android O and above)
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Stream Ready Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(channel);

        // 6. Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        Intent intent1 = new Intent("STREAM_READY_NOTIFICATION");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
    }

    @Override
    public void onNewToken(String token) {
        // This method is called whenever the FCM registration token is refreshed.
        // If you want to send the token to your server or store it in Firebase,
        // you can do so here.
        super.onNewToken(token);
    }
}
