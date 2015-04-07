package com.business.card.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.business.card.services.GPSIntentService;
import com.business.card.services.ScheduledGPSService;

public class BootCompletedReceiver extends BroadcastReceiver {

    // 15 * 60 * 1000 = 900000 miliseconds (or 15 minutes)
    private static final int PERIOD = 900000; // 15 minutes
    private static final int INITIAL_DELAY = 5000; // 5 seconds

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            // intent's action is null, this means it was not triggered by BOOT_COMPLETED event
            // but by the Alarm Manager
            // wake the device's CPU and start the ScheduledGPSService
            GPSIntentService.wakeCPUAndStartService(context, ScheduledGPSService.class);
        } else {
            // intent's action not null, this means that it was triggered by BOOT_COMPLETED event
            // schedule the alarm
            scheduleAlarms(context);
        }
    }

    /**
     * Schedule alarm for updating the GPS location
     */
    public static void scheduleAlarms(Context context) {
        // get the Alarm Manager system service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // set up a pending intent, that will be fired by the alarm manager
        // this intent will start up this receiver, and it will trigger onReceive()
        // without any action attached to the received intent
        Intent intent = new Intent(context, BootCompletedReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // set a repeating alarm to update the GPS coordinates on server
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + INITIAL_DELAY, PERIOD, pendingIntent);
    }
}
