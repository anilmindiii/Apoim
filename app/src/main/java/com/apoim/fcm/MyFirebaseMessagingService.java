package com.apoim.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.apoim.R;
import com.apoim.activity.MainActivity;
import com.apoim.helper.Constant;
import com.apoim.modal.PayLoadEvent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

/**
 * Created by abc on 1/24/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String userId, reqId;
    String title = "";
    String message = "";
    String reference_id = "";
    String orderId = "";
    String type = "";
    String createrId = "";

    String opponentChatId = "";
    String eventMemId = "";
    String compId = "";
    String payLoadEvent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Log.d("Notification_print", "Message data payload: " + remoteMessage.getData());

            type = remoteMessage.getData().get("type");
            if(type != null){
                title = remoteMessage.getData().get("title");
                message = remoteMessage.getData().get("body");
                orderId = remoteMessage.getData().get("orderId");
                reference_id = remoteMessage.getData().get("reference_id");
                createrId = remoteMessage.getData().get("createrId");
                opponentChatId = remoteMessage.getData().get("opponentChatId");
                payLoadEvent = remoteMessage.getData().get("payLoadEvent");


                if (remoteMessage.getData().get("eventMemId") != null) {
                    if (!remoteMessage.getData().get("eventMemId").equals("null"))
                        eventMemId = remoteMessage.getData().get("eventMemId");
                }

                if (remoteMessage.getData().get("compId") != null) {
                    if (!remoteMessage.getData().get("compId").equals("null"))
                        compId = remoteMessage.getData().get("compId");
                }

                Intent intent = new Intent("com.apoim");
                intent.putExtra("type", type);
                intent.putExtra("title", title);
                intent.putExtra("message", message);
                intent.putExtra("orderId", orderId);
                intent.putExtra("createrId", createrId);
                intent.putExtra("eventMemId", eventMemId);
                intent.putExtra("compId", compId);
                intent.putExtra("opponentChatId", opponentChatId);
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
                manager.sendBroadcast(intent);

                if (type.equals("chat")) {
                    if (!Constant.IsGetNotificationValue.equals(opponentChatId)) {
                        sendNotification(title, message, orderId, reference_id, type, createrId, compId, eventMemId);
                    }
                }else if(type.equals("group_chat")){
                    sendNotificationGroup(title, message, orderId, reference_id, type, createrId, compId, eventMemId);
                }
                else {
                    sendNotification(title, message, orderId, reference_id, type, createrId, compId, eventMemId);
                }
            }


        }
    }

    private void sendNotification(String title, String message, String orderId, String reference_id,
                                  String type, String createrId, String compId, String eventMemId) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("type", type);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("orderId", orderId);
        intent.putExtra("reference_id", reference_id);
        intent.putExtra("createrId", createrId);

        intent.putExtra("eventMemId", eventMemId);
        intent.putExtra("compId", compId);
        intent.putExtra("opponentChatId", opponentChatId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Abc";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(this.title)
                .setContentText(this.message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);

        }
        notificationManager.notify(5, notificationBuilder.build());
    }


    private void sendNotificationGroup(String title, String message, String orderId, String reference_id,
                                  String type, String createrId, String compId, String eventMemId) {

        Gson gson = new Gson();
        PayLoadEvent payLoad = gson.fromJson(payLoadEvent, PayLoadEvent.class);


        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("type", type);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("orderId", orderId);
        intent.putExtra("reference_id", payLoad.eventId);
        intent.putExtra("createrId", payLoad.eventOrganizerId);

        intent.putExtra("eventMemId", payLoad.eventMemId);
        intent.putExtra("payLoadEvent", payLoadEvent);

        if(payLoad.compId == null){
            payLoad.compId = "";
        }

        intent.putExtra("compId", payLoad.compId);
        intent.putExtra("opponentChatId", opponentChatId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Abc";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(this.title)
                .setContentText(this.message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);

        }
        notificationManager.notify(5, notificationBuilder.build());
    }
}
