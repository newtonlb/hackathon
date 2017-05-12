package com.beaconinside.demo.beaconscan;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.IBeaconId;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class NearbyIntentService extends IntentService {
    private static final String TAG = "NearbyIntentService";

    public NearbyIntentService() {
        super("NearbyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Nearby.Messages.handleIntent(intent, new MessageListener() {
            @Override
            public void onFound(Message message) {
                if (message.getType().equals(Message.MESSAGE_TYPE_I_BEACON_ID)) {
                    IBeaconId from = IBeaconId.from(message);
                    String id = from.getProximityUuid() + " " + toUint(from.getMajor()) + ":" + toUint(from.getMinor());
                    Log.i(TAG, "Ibeacon: " + id);
                    showNotification("iBeacon found", id);
                }
            }

            @Override
            public void onLost(Message message) {
                if (message.getType().equals(Message.MESSAGE_TYPE_I_BEACON_ID)) {
                    IBeaconId from = IBeaconId.from(message);
                    String id = from.getProximityUuid() + " " + (toUint(from.getMajor())) + ":" + toUint(from.getMinor());
                    Log.i(TAG, "Ibeacon: " + id);
                    showNotification("iBeacon lost", id);
                }
            }
        });
    }

    private int toUint(short value) {
        return (int) value & 0xffff;
    }

    private void showNotification(String namespace, String type) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_message_black_24dp)
                        .setContentTitle(namespace)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentText(type);
        int mNotificationId = 1;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(type, mNotificationId, mBuilder.build());
    }
}
