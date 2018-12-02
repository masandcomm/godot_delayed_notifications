package org.godotengine.godot;

import android.app.Activity;

public class AndroidDelayedNotifications extends Godot.SingletonBase {

    private Activity activity;
    private int mInstanceId;
    private NotificatorBroker broker;

    public AndroidDelayedNotifications(Activity p_activity) {
        registerClass("AndroidDelayedNotifications", new String[]{
                "init",
                "createNotificationChannel",
                "addNotification",
                "removeNotification"
        });

        activity = p_activity;
        broker = new NotificatorBroker(activity);
    }

    static public Godot.SingletonBase initialize(Activity p_activity) {
        return new AndroidDelayedNotifications(p_activity);
    }

    public void init(final int pInstanceId) {
        mInstanceId = pInstanceId;
    }

    public void createNotificationChannel(String id, String name, String description) {
        broker.createNotificationChannel(id, name, description);
    }

    public void addNotification(String channelId, int id, String title, String message, int delayInSec) {
        broker.addNotification(channelId, id, title, message, delayInSec);
    }

    public void removeNotification(int id) {
        broker.removeNotification(id);
    }

}
