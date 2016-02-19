package rhedox.gesahuvertretungsplan.util;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm", "Alarm received!");

        NotificationChecker checker = new NotificationChecker(context);
        checker.load();
    }

    @RequiresPermission(Manifest.permission.SET_ALARM)
    public static void create(Context context, int hour, int minute) {

        DateTime time = DateTime.now();

        //DEBUG until JodaTime is fixed
        //TODO Remove
        time = time.withFieldAdded(DurationFieldType.hours(), 1);

        Log.d("time",time.getZone().toString());

        if(hour < time.getHourOfDay() || (hour == time.getHourOfDay() && minute < time.getMinuteOfHour()))
            time = time.withFieldAdded(DurationFieldType.days(), 1);

        DateTime alarm = new DateTime(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), hour, minute, 0, 0);
        long millis = alarm.getMillis();

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(manager != null) {
            manager.setInexactRepeating(AlarmManager.RTC, millis, AlarmManager.INTERVAL_DAY, pending);
        }
    }

    public static void cancel(Context context) {
        PendingIntent intent = PendingIntent.getBroadcast(context, 0 , new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(manager!=null) {
            manager.cancel(intent);
        }
    }
}
