package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.squareup.leakcanary.RefWatcher;
import com.takisoft.preferencex.PreferenceFragmentCompat;

import org.joda.time.LocalTime;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import rhedox.gesahuvertretungsplan.App;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.broadcastReceiver.BootReceiver;
import rhedox.gesahuvertretungsplan.broadcastReceiver.SubstitutesAlarmReceiver;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;

/**
 * Created by Robin on 18.10.2014.
 */
public class PreferenceFragment extends PreferenceFragmentCompat {
    public static final String PREF_DARK_TYPE ="pref_dark_type";
    public static final String PREF_WIDGET_DARK ="pref_widget_dark";
    public static final String PREF_NOTIFICATION_TIME = "pref_notification_time_long";
    public static final String PREF_NOTIFICATION_MODE = "pref_notification_mode";
    public static final String PREF_PREVIOUSLY_STARTED = "pref_previously_started";
    public static final String PREF_VERSION = "pref_version";
    public static final String PREF_AMOLED = "pref_amoled";

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (context instanceof MainActivity) {
			((MainActivity)context).setTitle(getString(R.string.action_settings));
		}
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (getContext() == null) {
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        LocalTime time = LocalTime.fromMillisOfDay(prefs.getInt(PreferenceFragment.PREF_NOTIFICATION_TIME, 0));
        String mode = prefs.getString(PREF_NOTIFICATION_MODE, null);

        if (!"NONE".equals(mode)) {
            @SubstitutesAlarmReceiver.NotificationFrequency int notificationFrequency;
            if ("per_lesson".equals(mode)) {
                SubstitutesAlarmReceiver.cancelDaily(getContext());
                notificationFrequency = SubstitutesAlarmReceiver.NotificationFrequencyValues.perLesson;
            } else if ("both".equals(mode))
                notificationFrequency = SubstitutesAlarmReceiver.NotificationFrequencyValues.both;
            else {
                SubstitutesAlarmReceiver.cancelLesson(getContext());
                notificationFrequency = SubstitutesAlarmReceiver.NotificationFrequencyValues.daily;
            }

            SubstitutesAlarmReceiver.create(getContext(), time.getHourOfDay(), time.getMinuteOfHour(), notificationFrequency);
            BootReceiver.create(getContext());
        } else {
            SubstitutesAlarmReceiver.cancelDaily(getContext());
            SubstitutesAlarmReceiver.cancelLesson(getContext());
            BootReceiver.cancel(getContext());
        }

        PreferenceFragment.applyDarkTheme(prefs);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //LeakCanary
        RefWatcher refWatcher = getActivity() != null ? App.getRefWatcher(getActivity()) : null;
        if(refWatcher != null)
            refWatcher.watch(this);
    }

    public static void applyDarkTheme(SharedPreferences prefs) {
        if(prefs == null)
            return;

        String darkTheme = prefs.getString(PREF_DARK_TYPE, null);
        if("always".equals(darkTheme))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else if("auto".equals(darkTheme))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        else if("never".equals(darkTheme))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static PreferenceFragment newInstance() {
        return new PreferenceFragment();
    }
}
