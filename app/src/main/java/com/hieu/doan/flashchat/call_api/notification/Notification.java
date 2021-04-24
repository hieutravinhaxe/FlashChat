package com.hieu.doan.flashchat.call_api.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.hieu.doan.flashchat.Activities.MainActivity;
import com.hieu.doan.flashchat.call_api.calling.Common;
import com.hieu.doan.flashchat.call_api.calling.StringeeAudioManager;
import com.hieu.doan.flashchat.call_api.calling.Utils;
import com.hieu.doan.flashchat.R;

import java.util.Set;

public class Notification {
    //replace your channel id, channel name, channel desc here
    private static final String CALL_CHANNEL_ID = "com.stringee.sample.call.notification";
    private static final String CALL_CHANNEL_NAME = "Notification Call Channel Sample";
    private static final String CALL_CHANNEL_DESC = "Channel sample for call notification";

    public static void notifyIncomingCall(final Context context, String from) {
        NotificationManager mNotificationManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CALL_CHANNEL_ID, CALL_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CALL_CHANNEL_DESC);
            channel.setSound(null, null);
            mNotificationManager = context.getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(channel);
        } else {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) (System.currentTimeMillis() & 0xfffffff),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CALL_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(null)
                        .setContentTitle("Đang có cuộc gọi đến")
                        .setContentText("từ: " + from)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setContentIntent(pendingIntent);

        android.app.Notification incomingCallNotification = notificationBuilder.build();

        mNotificationManager.notify(44448888, incomingCallNotification);

        Utils.runOnUithread(new Runnable() {
            @Override
            public void run() {
                if (Common.audioManager == null) {
                    Common.audioManager = StringeeAudioManager.create(context);
                    Common.audioManager.start(new StringeeAudioManager.AudioManagerEvents() {
                        @Override
                        public void onAudioDeviceChanged(StringeeAudioManager.AudioDevice selectedAudioDevice, Set<StringeeAudioManager.AudioDevice> availableAudioDevices) {
                        }
                    });
                }

                if (Common.ringtone == null) {
                    Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    Common.ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
                    Common.ringtone.play();
                }
            }
        });
    }
}
