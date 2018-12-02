package org.godotengine.godot;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.godot.game.R;

public class Notificator {

    private final String TAG = "Notificator";


    private final int TO_MILLIS = 1000;
    private final int MINIMAL_DELAY = 15; // Minimal delay in seconds

    private Activity activity;

    public Notificator(Activity activity) {
        this.activity = activity;
    }

    public void createNotificationChannel(String id, String name, String description) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
            for (NotificationChannel channel : notificationManager.getNotificationChannels()) {
                if (channel.getId().equals(id)) {
                    Log.i(TAG, "Already have a channel with id: " + id);
                    return;
                }
            }
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
            Log.i(TAG, "*** channel - id: " + id + " name: " + name  + " description: " + description);
        }

    }

    public void addNotification(String channelId, int id, String title, String message, int delayInSec) {
        if (delayInSec <= MINIMAL_DELAY) {
            Log.i(TAG, "*** channel is not added because time is too short");
            return;
        }
        final long dellayInMillis = delayInSec * TO_MILLIS;

                NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, channelId)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.icon)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                Intent tapIntent = new Intent(activity, Godot.class);
                PendingIntent openGameIntent = PendingIntent.getActivity(activity, id, tapIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(openGameIntent);

                Notification notification = builder.build();

                Intent notificationIntent = new Intent(activity, NotificationBroadcastReceiver.class);
                notificationIntent.putExtra(NotificationBroadcastReceiver.NOTIFICATION_ID, id);
                notificationIntent.putExtra(NotificationBroadcastReceiver.NOTIFICATION, notification);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, id, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + dellayInMillis, pendingIntent);

                Log.i(TAG, "*** Added notification: channelId: " + channelId + " id: " + id + " title: " + title + " message: " + message + " delay time: " + delayInSec);

    }

    public void removeNotification(int id) {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(activity, NotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                activity, id, myIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pendingIntent);
        Log.i(TAG, "Removed notification id: " + id);
    }

}
