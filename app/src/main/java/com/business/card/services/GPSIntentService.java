package com.business.card.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;


abstract public class GPSIntentService extends IntentService {
    abstract protected void sendGPSCoordinates(Intent intent);

    public static final String NAME = "com.business.card.services.GPSIntentService";
    private static volatile PowerManager.WakeLock lockStatic = null;

    public GPSIntentService(String name) {
        super(name);
    }

    /**
     * Create new partial wake lock for the device
     */
    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            lockStatic = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NAME);
            lockStatic.setReferenceCounted(true);
        }
        return (lockStatic);
    }

    /**
     * Wake the CPU and start a service
     */
    public static void wakeCPUAndStartService(Context context, Class<?> serviceClass) {
        // acquire new partial wake lock for the device
        getLock(context.getApplicationContext()).acquire();

        // start the received service
        context.startService(new Intent(context, serviceClass));
    }

    @Override
    final protected void onHandleIntent(Intent intent) {
        try {
            sendGPSCoordinates(intent);
        } finally {
            PowerManager.WakeLock lock = getLock(this.getApplicationContext());
            if (lock.isHeld()) {
                try {
                    lock.release();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Exception when releasing wakelock", e);
                }
            }
        }
    }
}
