package rhedox.gesahuvertretungsplan.util;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresPermission;

import org.joda.time.LocalTime;

import rhedox.gesahuvertretungsplan.ui.fragment.SettingsFragment;
import rhedox.gesahuvertretungsplan.ui.preference.NotificationPreference;
import rhedox.gesahuvertretungsplan.ui.preference.TimePreference;

/**
 * Created by Robin on 15.11.2014.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean notification = prefs.getBoolean(SettingsFragment.PREF_NOTIFICATION, true);
            LocalTime time = LocalTime.fromMillisOfDay(prefs.getInt(SettingsFragment.PREF_NOTIFICATION_TIME, 0));

            if(notification) {
                AlarmReceiver.create(context, time.getHourOfDay(), time.getMinuteOfHour());
            }
        }
    }

    @RequiresPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)
    public static void create(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void cancel(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
