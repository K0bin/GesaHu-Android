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
            String alarmString = prefs.getString("pref_notification_time", "00:00");
            int alarmHour = TimePreference.getHour(alarmString);
            int alarmMinute = TimePreference.getMinute(alarmString);
            new AlarmReceiver().create(getContext(), alarmHour, alarmMinute);
            new BootReceiver().create(getContext());
        } else {
            new AlarmReceiver().cancel(getContext());
            new BootReceiver().cancel(getContext());
        }
    }
}
