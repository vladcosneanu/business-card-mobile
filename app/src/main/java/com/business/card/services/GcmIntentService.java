package com.business.card.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.business.card.R;
import com.business.card.activities.MainActivity;
import com.business.card.receivers.GcmBroadcastReceiver;
import com.business.card.util.Util;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    public static final int REQUEST_CARD_NOTIFICATION_ID = 1;
    public static final int REQUEST_CARD_GRANTED_NOTIFICATION_ID = 2;
    public static final int REQUEST_CARD_DECLINED_NOTIFICATION_ID = 3;
    public static final int SHARE_CARD_NOTIFICATION_ID = 4;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(extras);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(extras);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                sendNotification(extras);
                Log.i("GCM", "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle bundle) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationTitle = bundle.getString("title");
        if (notificationTitle.equals("Business Card access request")) {
            displayRequestCardNotification(bundle);
        } else if (notificationTitle.equals("Business Card access granted")) {
            displayRequestCardGrantedNotification(bundle);
        } else if (notificationTitle.equals("Business Card access declined")) {
            displayRequestCardDeclinedNotification(bundle);
        } else if (notificationTitle.equals("Business Card share request")) {
            displayShareRequestNotification(bundle);
        }
    }

    private void displayRequestCardNotification(Bundle bundle) {
        Intent acceptIntent = new Intent(this, MainActivity.class);
        acceptIntent.setAction(Util.ACCEPT_REQUEST_CARD_ACTION);
        acceptIntent.putExtra(Util.REQUEST_CARD_RESPONSE_EXTRA, Util.REQUEST_CARD_RESPONSE_ACCEPT);
        acceptIntent.putExtra(Util.REQUEST_CARD_RESPONSE_USER_ID_EXTRA, bundle.getString("user_id"));
        acceptIntent.putExtra(Util.REQUEST_CARD_RESPONSE_CARD_ID_EXTRA, bundle.getString("card_id"));
        acceptIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent acceptPendingIntent = PendingIntent.getActivity(this, 0,
                acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent denyIntent = new Intent(this, MainActivity.class);
        denyIntent.setAction(Util.DENY_REQUEST_CARD_ACTION);
        denyIntent.putExtra(Util.REQUEST_CARD_RESPONSE_EXTRA, Util.REQUEST_CARD_RESPONSE_DENY);
        denyIntent.putExtra(Util.REQUEST_CARD_RESPONSE_USER_ID_EXTRA, bundle.getString("user_id"));
        denyIntent.putExtra(Util.REQUEST_CARD_RESPONSE_CARD_ID_EXTRA, bundle.getString("card_id"));
        denyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent denyPendingIntent = PendingIntent.getActivity(this, 1,
                denyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_new)
                        .setContentTitle(bundle.getString("title"))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(bundle.getString("message")))
                        .setContentText(bundle.getString("message"))
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .addAction(R.drawable.ic_action_accept, getString(R.string.allow), acceptPendingIntent)
                        .addAction(R.drawable.ic_action_cancel, getString(R.string.deny), denyPendingIntent);

        mNotificationManager.notify(REQUEST_CARD_NOTIFICATION_ID, mBuilder.build());
    }

    private void displayRequestCardGrantedNotification(Bundle bundle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Util.ACCEPT_REQUEST_CARD_GRANTED_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent acceptPendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_new)
                        .setContentTitle(bundle.getString("title"))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(bundle.getString("message")))
                        .setContentText(bundle.getString("message"))
                        .setContentIntent(acceptPendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL);

        mNotificationManager.notify(REQUEST_CARD_GRANTED_NOTIFICATION_ID, mBuilder.build());
    }

    private void displayRequestCardDeclinedNotification(Bundle bundle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Util.ACCEPT_REQUEST_CARD_GRANTED_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent acceptPendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_new)
                        .setContentTitle(bundle.getString("title"))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(bundle.getString("message")))
                        .setContentText(bundle.getString("message"))
                        .setContentIntent(acceptPendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL);

        mNotificationManager.notify(REQUEST_CARD_DECLINED_NOTIFICATION_ID, mBuilder.build());
    }

    private void displayShareRequestNotification(Bundle bundle) {
        Intent saveIntent = new Intent(this, MainActivity.class);
        saveIntent.setAction(Util.SAVE_SHARE_CARD_ACTION);
        saveIntent.putExtra(Util.SHARE_CARD_RESPONSE_EXTRA, Util.SHARE_CARD_RESPONSE_SAVE);
        saveIntent.putExtra(Util.SHARE_CARD_RESPONSE_USER_ID_EXTRA, bundle.getString("user_id"));
        saveIntent.putExtra(Util.SHARE_CARD_RESPONSE_CARD_ID_EXTRA, bundle.getString("card_id"));
        saveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent savePendingIntent = PendingIntent.getActivity(this, 0,
                saveIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent cancelIntent = new Intent(this, MainActivity.class);
        cancelIntent.setAction(Util.CANCEL_SHARE_CARD_ACTION);
        cancelIntent.putExtra(Util.SHARE_CARD_RESPONSE_EXTRA, Util.SHARE_CARD_RESPONSE_CANCEL);
        cancelIntent.putExtra(Util.SHARE_CARD_RESPONSE_USER_ID_EXTRA, bundle.getString("user_id"));
        cancelIntent.putExtra(Util.SHARE_CARD_RESPONSE_CARD_ID_EXTRA, bundle.getString("card_id"));
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent cancelPendingIntent = PendingIntent.getActivity(this, 1,
                cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_new)
                        .setContentTitle(bundle.getString("title"))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(bundle.getString("message")))
                        .setContentText(bundle.getString("message"))
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .addAction(R.drawable.ic_action_accept, getString(R.string.save), savePendingIntent)
                        .addAction(R.drawable.ic_action_cancel, getString(R.string.cancel), cancelPendingIntent);

        mNotificationManager.notify(SHARE_CARD_NOTIFICATION_ID, mBuilder.build());
    }
}
