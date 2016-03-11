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
import org.joda.time.DateTimeConstants;
import org.joda.time.DurationFieldType;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 0;
    public static final int REQUEST_CODE_BASE = 14;

    private static final int[] hours = new int[]{8,9,10,11,12,13,14,15,16};
    private static final int[] minutes = new int[]{45,35,25,25,30,15,45,30,30};

    public static final String EXTRA_LESSON = "extra_lesson";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm", "Alarm received!");

        int lesson = intent.getIntExtra(EXTRA_LESSON, -1);

        if(DateTime.now().getDayOfWeek() == DateTimeConstants.SATURDAY || DateTime.now().getDayOfWeek() == DateTimeConstants.SUNDAY)
            lesson = -1;

        NotificationChecker checker = new NotificationChecker(context, lesson);
        checker.load();
    }

    @RequiresPermission(Manifest.permission.SET_ALARM)
    public static void create(Context context, int hour, int minute) {

        DateTime time = DateTime.now();

        if(hour < time.getHourOfDay() || (hour == time.getHourOfDay() && minute < time.getMinuteOfHour()))
            time = time.withFieldAdded(DurationFieldType.days(), 1);

        DateTime alarm = new DateTime(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), hour, minute, 0, 0);
        scheduleAlarm(context, alarm, REQUEST_CODE, -1);
    }

    @RequiresPermission(Manifest.permission.SET_ALARM)
    public static void create(Context context, int firstHour, int firstMinute, boolean daily) {

        if(daily)
        create(context, firstHour, firstMinute);
        else {
            DateTime time = DateTime.now();

            if(firstHour < time.getHourOfDay() || (firstHour == time.getHourOfDay() && firstMinute < time.getMinuteOfHour()))
                time = time.withFieldAdded(DurationFieldType.days(), 1);

            DateTime alarm = new DateTime(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), firstHour, firstMinute, 0, 0);
            scheduleAlarm(context, alarm, REQUEST_CODE, 1);

            for(int i = 0; i < hours.length; i++) {
                time = DateTime.now();

                if(hours[i] < time.getHourOfDay() || (hours[i] == time.getHourOfDay() && minutes[i] < time.getMinuteOfHour()))
                    time = time.withFieldAdded(DurationFieldType.days(), 1);

                alarm = new DateTime(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), hours[i], minutes[i], 0, 0);
                scheduleAlarm(context, alarm, REQUEST_CODE_BASE + i, i + 2);
            }
        }
    }

    private static void scheduleAlarm(Context context, DateTime time, int requestCode, int lesson) {
        long millis = time.getMillis();

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_LESSON, lesson);
        PendingIntent pending = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (manager != null) {
            manager.setInexactRepeating(AlarmManager.RTC, millis, AlarmManager.INTERVAL_DAY, pending);
        }
    }

    public static void cancel(Context context) {
        PendingIntent[] intents = new PendingIntent[10];
        intents[0] = PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        for(int i = 0; i < hours.length; i++) {
            intents[i+1] = PendingIntent.getBroadcast(context, REQUEST_CODE_BASE + i, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        }

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(manager!=null) {
            for(PendingIntent intent : intents)
                manager.cancel(intent);
        }
    }
}
