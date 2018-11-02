package org.godotengine.godot;

import android.app.Activity;
import android.app.Context;

public class Notifications extends Godot.SingletonBase {

    static public Godot.SingletonBase initialize (Activity p_activity) {
        return new Notifications(p_activity);
    }

    private Context mContext;
    private Activity mActivity;
    private int mInstanceId;
    private final int TO_MILLIS = 1000;

    public Notifications(Activity p_activity) {
        registerClass("Notifications", new String[] {
                "init",
                "addNotification",
                "removeNotification"
            });

        mActivity = p_activity;
        mContext = mActivity.getApplicationContext();
    }

    public void init(final String pChannelId, final int pInstanceId) {
        mInstanceId = pInstanceId;
        createNotificationChannel(pChannelId);
    }

    public void createNotificationChannel(String pChannelId) {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    CharSequence name = mActivity.getString(R.string.channel_upgrades);
                    String description = getString(R.string.channel_description);
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(pChannelId, name, importance);
                    channel.setDescription(description);
                    NotificationManager notificationManager = mActivity.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
            }
        });
        
    }

    public void addNotification(String pTitle, String pMessage, int pScheduleInSec, String pChannelId, String pNotificationId) {
        long dellayInMillis = pScheduleInSec * TO_MILLIS;
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (dellayInMillis <= SystemClock.elapsedRealtime()) {
                    return;
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, pChannelId)
                        .setContentTitle(pTitle)
                        .setContentText(pMessage)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.icon)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                Notification notification = builder.build();

                Intent notificationIntent = new Intent(mContext, AndroidNotificationReceiver.class);
                notificationIntent.putExtra(AndroidNotificationReceiver.NOTIFICATION_ID, pNotificationId);
                notificationIntent.putExtra(AndroidNotificationReceiver.NOTIFICATION, notification);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, delayInMillis, pendingIntent);
            }
        });

    }

    public void removeNotification(String pChannelId) {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
               
            }
        });
    }
}


