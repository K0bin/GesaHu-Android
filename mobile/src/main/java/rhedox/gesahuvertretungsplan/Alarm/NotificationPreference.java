package rhedox.gesahuvertretungsplan.Alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

/**
 * Created by Robin on 15.11.2014.
 */
public class NotificationPreference extends CheckBoxPreference {
    public static final String PREF_NOTIFICATION_TIME="pref_notification_time";
    public static final String PREF_NOTIFICATION="pref_notification";

    public NotificationPreference(Context context) {
        super(context);
    }
    public NotificationPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    public NotificationPreference(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onClick() {
        super.onClick();

        if(isChecked()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String alarmString = prefs.getString(NotificationPreference.PREF_NOTIFICATION_TIME, "00:00");
            int alarmHour = TimePreference.getHour(alarmString);
            int alarmMinute = TimePreference.getMinute(alarmString);
            AlarmReceiver.create(getContext(), alarmHour, alarmMinute);
            BootReceiver.create(getContext());
        } else {
            AlarmReceiver.cancel(getContext());
            BootReceiver.cancel(getContext());
        }
    }
}
