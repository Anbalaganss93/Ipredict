package com.ipredictfantasy.fbc;

/**
 * Created by UMMWDC001 on 6/28/2016.
 */

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ipredictfantasy.activity.IpredictApplication;
import com.ipredictfantasy.activity.SplashScreen;
import com.ipredictfantasy.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String messagetitle, messagebody;
    private PendingIntent pendingIntent;
    private Uri defaultSoundUri;
    private NotificationCompat.BigPictureStyle notiStyle;
    private Bitmap bitmap;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        String key = remoteMessage.getCollapseKey();

        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        messagetitle = remoteMessage.getNotification().getTitle();
        sendNotification(remoteMessage);


    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage FCM message body received.
     */
    private void sendNotification(RemoteMessage remoteMessage) {
        String messageBody = remoteMessage.getNotification().getBody();
        String imageUrl = null;
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().containsKey("image")) {
                imageUrl = remoteMessage.getData().get("image");
                bitmap = getBitmap(imageUrl);
            }
        }

        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        // image showing in notification
        notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.setSummaryText(messageBody);
        if (remoteMessage.getData().containsKey("image")) {
            notiStyle.bigPicture(bitmap);
        }
       /* if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            if (remoteMessage.getData().containsKey("image")) {
                *//* notificationBuilder = new NotificationCompat.Builder(IpredictApplication.context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(messagetitle)
                        .setContentText(messageBody)
                        .setLargeIcon(getBitmap("http://66.media.tumblr.com/d1dd973d8899c45a5e4cd5d5aca815aa/tumblr_nnshumSpOL1s36w6uo1_500.jpg"))
                        .setStyle(notiStyle)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);*//*

                new generatePictureStyleNotification(this, messagetitle, messageBody, imageUrl).execute();
            } else {*/
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messagetitle)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

//            }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(Context context, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.dhoni);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(IpredictApplication.context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(result)
                    .setStyle(notiStyle)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        }
    }

    public static Bitmap getBitmap(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}