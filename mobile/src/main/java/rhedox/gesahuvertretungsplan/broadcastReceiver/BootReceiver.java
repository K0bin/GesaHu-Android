package rhedox.gesahuvertretungsplan.broadcastReceiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import org.joda.time.LocalTime;

import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

/**
 * Created by Robin on 15.11.2014.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String mode = prefs.getString(PreferenceFragment.PREF_NOTIFICATION_MODE, null);
            LocalTime time = LocalTime.fromMillisOfDay(prefs.getInt(PreferenceFragment.PREF_NOTIFICATION_TIME, 0));

            if(!"NONE".equals(mode)) {
                @SubstitutesAlarmReceiver.NotificationFrequency long notificationFrequency;
                if("per_lesson".equals(mode))
                    notificationFrequency = SubstitutesAlarmReceiver.NotificationFrequencyValues.perLesson;
                else if("both".equals(mode))
                    notificationFrequency = SubstitutesAlarmReceiver.NotificationFrequencyValues.both;
                else
                    notificationFrequency = SubstitutesAlarmReceiver.NotificationFrequencyValues.daily;

                SubstitutesAlarmReceiver.create(context, time.getHourOfDay(), time.getMinuteOfHour(), notificationFrequency);
            }
        }
    }

    @RequiresPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)
    public static void create(@NonNull Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void cancel(@NonNull Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
