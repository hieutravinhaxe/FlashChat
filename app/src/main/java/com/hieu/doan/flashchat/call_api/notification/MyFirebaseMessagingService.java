package com.hieu.doan.flashchat.call_api.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hieu.doan.flashchat.R;
import com.hieu.doan.flashchat.call_api.calling.Common;
import com.hieu.doan.flashchat.Activities.MainActivity;
import com.stringee.listener.StatusListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String title,message;
    private final String TAG = "FLASH CHAT";

    @Override
    public void onNewToken(@NonNull String s) {
        if (MainActivity.client != null && MainActivity.client.isConnected()) {
            MainActivity.client.registerPushToken(s, new StatusListener() {
                @Override
                public void onSuccess() {
                    FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid()).child("token").setValue(s);
                }
            });
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
           try{
               if (!remoteMessage.getData().get("title").isEmpty()){
                   title=remoteMessage.getData().get("title");
                   message=remoteMessage.getData().get("body");
                   sendNotification(title, message);
               }
           }
           catch (Exception e){

           }
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String pushFromStringee = remoteMessage.getData().get("stringeePushNotification");
            if (pushFromStringee != null) {
                if (MainActivity.client == null || Common.isAppInBackground) {
                    try {
                        JSONObject jsonObject = new JSONObject(remoteMessage.getData().get("data"));
                        String callStatus = jsonObject.optString("callStatus", null);
                        String callId = jsonObject.optString("callId", null);
                        String from = jsonObject.getJSONObject("from").getString("alias");
                        if (callId != null && callStatus != null) {
                            switch (callStatus) {
                                case "started":
                                    //make a notification when app in background or killed
                                    Notification.notifyIncomingCall(getApplicationContext(), from);
                                    break;
                                case "ended":
                                    //remove notification
                                    NotificationManager nm = (NotificationManager) getSystemService
                                            (NOTIFICATION_SERVICE);
                                    if (nm != null) {
                                        nm.cancel(44448888);
                                    }
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "thongbao");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Log.d("send", "Da gui thong bao: "+message);
        String channelId = getString(R.string.channelId);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setDefaults(android.app.Notification.DEFAULT_ALL)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .addAction(new NotificationCompat.Action(
                                android.R.drawable.sym_call_missed,
                                "Cancel",
                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)))
                        .addAction(new NotificationCompat.Action(
                                android.R.drawable.sym_call_outgoing,
                                "OK",
                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

}
