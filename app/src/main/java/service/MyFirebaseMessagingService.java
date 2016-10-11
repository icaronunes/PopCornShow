package service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import activity.FilmeActivity;
import activity.MainActivity;
import activity.PersonPopularActivity;
import activity.TvShowActivity;
import br.com.icaro.filme.R;
import utils.Constantes;

/**
 * Created by icaro on 09/10/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingSer";
    private Map<String, String> data;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            data = remoteMessage.getData();
            sendNotification("Send");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String messageBody) {
        Intent intent = null;


        switch (data.get("media")){

            case "filme":{
               intent = new Intent(this, FilmeActivity.class);
                intent.putExtra(Constantes.FILME_ID, Integer.valueOf(data.get("id")));
                intent.putExtra(Constantes.COLOR_TOP, Integer.valueOf(data.get("colorfab")));
                break;
            }

            case "tv":{
                intent = new Intent(this, TvShowActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID, Integer.valueOf(data.get("id")));
                intent.putExtra(Constantes.COLOR_TOP, Integer.valueOf(data.get("colorfab")));
                break;
            }

        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher3)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
