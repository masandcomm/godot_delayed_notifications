package org.godotengine.godot;

import android.app.Activity;

public class NotificatorBroker {

    private Activity activity;
    private Notificator notificator;

    public NotificatorBroker(Activity activity) {
        this.activity = activity;
        notificator = new Notificator(activity);
    }

    public void createNotificationChannel(final String id, final String name, final String description) {

        activity.runOnUiThread(new Runnable() {
            public void run() {
                notificator.createNotificationChannel(id, name, description);
            }
        });

    }

    public void addNotification(final String channelId, final int id, final String title, final String message, final int delayInSec) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notificator.addNotification(channelId, id, title, message, delayInSec);
            }
        });
    }

    public void removeNotification(final int id) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notificator.removeNotification(id);
            }
        });
    }
}
