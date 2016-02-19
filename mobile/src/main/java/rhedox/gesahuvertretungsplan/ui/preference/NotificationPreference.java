package rhedox.gesahuvertretungsplan.ui.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.AttributeSet;

import org.joda.time.LocalTime;

//import rhedox.gesahuvertretungsplan.net.SubstituteRequest;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;
import rhedox.gesahuvertretungsplan.util.AlarmReceiver;
import rhedox.gesahuvertretungsplan.util.BootReceiver;

/**
 * Created by Robin on 15.11.2014.
 */
public class NotificationPreference extends SwitchPreferenceCompat {

    public NotificationPreference(Context context) {
        super(context);
    }

    public NotificationPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public NotificationPreference(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NotificationPreference(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onClick() {
        super.onClick();

        if (isChecked()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            LocalTime time = LocalTime.fromMillisOfDay(prefs.getInt(PreferenceFragment.PREF_NOTIFICATION_TIME, 0));
            AlarmReceiver.create(getContext(), time.getHourOfDay(), time.getMinuteOfHour());
            BootReceiver.create(getContext());
        } else {
            AlarmReceiver.cancel(getContext());
            BootReceiver.cancel(getContext());
        }
    }
}