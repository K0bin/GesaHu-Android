package rhedox.gesahuvertretungsplan.Alarm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

/**
 * Created by Robin on 15.11.2014.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean notification = prefs.getBoolean("pref_notification", true);
            String alarmString = prefs.getString("pref_notification_time", "00:00");
            int alarmHour = TimePreference.getHour(alarmString);
            int alarmMinute = TimePreference.getMinute(alarmString);

            if(notification) {
                new AlarmReceiver().create(context, alarmHour, alarmMinute);
            }
        }
    }

    public void create(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public void cancel(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
