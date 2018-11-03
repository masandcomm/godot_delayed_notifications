package org.godotengine.godot;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.godot.game.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class GodotDelayedNotifications extends Godot.SingletonBase {
    private final int TO_MILLIS = 1000;
    private final int MINIMAL_DELAY = 15; // Minimal delay in seconds
    private Context mContext;
    private Activity mActivity;
    private int mInstanceId;

    public GodotDelayedNotifications(Activity p_activity) {
        registerClass("GodotDelayedNotifications", new String[]{
                "init",
                "addNotification",
                "removeNotification"
        });

        mActivity = p_activity;
        mContext = mActivity.getApplicationContext();
    }

    static public Godot.SingletonBase initialize(Activity p_activity) {
        return new GodotDelayedNotifications(p_activity);
    }

    public void init(final String pChannelId, final int pInstanceId) {
        mInstanceId = pInstanceId;
        createNotificationChannel(pChannelId);
    }

    public void createNotificationChannel(final String pChannelId) {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    CharSequence name = pChannelId;
                    String description = pChannelId;
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(pChannelId, name, importance);
                    channel.setDescription(description);
                    NotificationManager notificationManager = mActivity.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
            }
        });

    }

    public void addNotification(final String pTitle, final String pMessage, int pScheduleInSec, final String pChannelId, final int pNotificationId) {
        if (pScheduleInSec <= MINIMAL_DELAY) {
            return;
        }
        final long dellayInMillis = pScheduleInSec * TO_MILLIS;
        mActivity.runOnUiThread(new Runnable() {
            public void run() {

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, pChannelId)
                        .setContentTitle(pTitle)
                        .setContentText(pMessage)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.icon)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                Intent tapIntent = new Intent(mContext, Godot.class);
                PendingIntent openGameIntent = PendingIntent.getActivity(mContext, pNotificationId, tapIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(openGameIntent);

                Notification notification = builder.build();

                Intent notificationIntent = new Intent(mContext, GodotDelayedNotificationReceiver.class);
                notificationIntent.putExtra(GodotDelayedNotificationReceiver.NOTIFICATION_ID, pNotificationId);
                notificationIntent.putExtra(GodotDelayedNotificationReceiver.NOTIFICATION, notification);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, pNotificationId, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + dellayInMillis, pendingIntent);

            }
        });

    }

    public void removeNotification(final int pNotificationId) {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent myIntent = new Intent(mContext, GodotDelayedNotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        mContext, pNotificationId, myIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                alarmManager.cancel(pendingIntent);
            }
        });
    }

}
