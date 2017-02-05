package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.EditTextPreferenceDialogFragmentCompat;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.ListPreferenceDialogFragmentCompat;
import android.support.v7.preference.Preference;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

import org.joda.time.LocalTime;

import rhedox.gesahuvertretungsplan.App;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.broadcastReceiver.SubstitutesAlarmReceiver;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.activity.WelcomeActivity;
import rhedox.gesahuvertretungsplan.broadcastReceiver.BootReceiver;
import rhedox.gesahuvertretungsplan.ui.preference.TimePreference;

/**
 * Created by Robin on 18.10.2014.
 */
public class PreferenceFragment extends PreferenceFragmentCompatDividers {
    private static final String DIALOG_FRAGMENT_TAG =
            "android.support.v7.preference.PreferenceFragment.DIALOG";

    public static final String PREF_YEAR ="pref_year";
    public static final String PREF_CLASS ="pref_class";
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        LocalTime time = LocalTime.fromMillisOfDay(prefs.getInt(PreferenceFragment.PREF_NOTIFICATION_TIME, 0));
        String mode = prefs.getString(PREF_NOTIFICATION_MODE, null);

        if(!"NONE".equals(mode)) {
            @SubstitutesAlarmReceiver.NotificationFrequency long notificationFrequency;
            if("per_lesson".equals(mode)) {
                SubstitutesAlarmReceiver.cancelDaily(getContext());
	            notificationFrequency = SubstitutesAlarmReceiver.PER_LESSON;
            } else if("both".equals(mode))
                notificationFrequency = SubstitutesAlarmReceiver.BOTH;
            else {
                SubstitutesAlarmReceiver.cancelLesson(getContext());
	            notificationFrequency = SubstitutesAlarmReceiver.DAILY;
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
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
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

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {

        boolean handled = false;
        if (getCallbackFragment() instanceof OnPreferenceDisplayDialogCallback) {
            handled = ((OnPreferenceDisplayDialogCallback) getCallbackFragment())
                    .onPreferenceDisplayDialog(this, preference);
        }
        if (!handled && getActivity() instanceof OnPreferenceDisplayDialogCallback) {
            handled = ((OnPreferenceDisplayDialogCallback) getActivity())
                    .onPreferenceDisplayDialog(this, preference);
        }

        if (handled) {
            return;
        }

        // check if dialog is already showing
        if (getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            return;
        }

        final DialogFragment f;
        if (preference instanceof EditTextPreference) {
            f = EditTextPreferenceDialogFragmentCompat.newInstance(preference.getKey());
        } else if (preference instanceof ListPreference) {
            f = ListPreferenceDialogFragmentCompat.newInstance(preference.getKey());
        } else if(preference instanceof TimePreference) {
            f = TimePreferenceDialogFragment.newInstance(preference.getKey());
        } else {
            throw new IllegalArgumentException("Tried to display dialog for unknown " +
                    "preference type. Did you forget to override onDisplayPreferenceDialog()?");
        }
        f.setTargetFragment(this, 0);
        f.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }
}
