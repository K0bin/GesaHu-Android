package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.leakcanary.RefWatcher;
import com.takisoft.preferencex.PreferenceFragmentCompat;

import org.joda.time.LocalTime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.Crash;
import rhedox.gesahuvertretungsplan.App;
import rhedox.gesahuvertretungsplan.BuildConfig;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.broadcast_receiver.BootReceiver;
import rhedox.gesahuvertretungsplan.broadcast_receiver.SubstitutesAlarmReceiver;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.preference.TimePreference;

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
    public static final String PREF_ANALYTICS = "pref_analytics";
    public static final String PREF_CRASH_REPORTS = "pref_crash_reports";

    private static final String FRAGMENT_DIALOG_TAG = "androidx.preference.PreferenceFragment.DIALOG";

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
                SubstitutesAlarmReceiver.cancelDaily(requireContext());
                notificationFrequency = SubstitutesAlarmReceiver.NotificationFrequencyValues.perLesson;
            } else if ("both".equals(mode))
                notificationFrequency = SubstitutesAlarmReceiver.NotificationFrequencyValues.both;
            else {
                SubstitutesAlarmReceiver.cancelLesson(requireContext());
                notificationFrequency = SubstitutesAlarmReceiver.NotificationFrequencyValues.daily;
            }

            SubstitutesAlarmReceiver.create(requireContext(), time.getHourOfDay(), time.getMinuteOfHour(), notificationFrequency);
            BootReceiver.create(requireContext());
        } else {
            SubstitutesAlarmReceiver.cancelDaily(requireContext());
            SubstitutesAlarmReceiver.cancelLesson(requireContext());
            BootReceiver.cancel(requireContext());
        }

        PreferenceFragment.applyDarkTheme(prefs);

        PreferenceFragment.applyPrivacy(requireContext(), prefs);
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

    public static void applyPrivacy(@NonNull Context context, @Nullable SharedPreferences prefs) {
        boolean areAnalyticsEnabled = false;
        boolean isCrashReportingEnabled = false;
        if (prefs != null) {
            areAnalyticsEnabled = prefs.getBoolean(PREF_ANALYTICS, false);
            isCrashReportingEnabled = prefs.getBoolean(PREF_CRASH_REPORTS, false);
        }

        final FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        analytics.setAnalyticsCollectionEnabled(areAnalyticsEnabled);
        if (!areAnalyticsEnabled && prefs != null) {
            analytics.resetAnalyticsData();
        }

        final Fabric.Builder fabricBuilder = new Fabric.Builder(context)
                .debuggable(BuildConfig.DEBUG);
        if (isCrashReportingEnabled) {
            fabricBuilder.kits(new Crashlytics());
        }
        Fabric.with(fabricBuilder.build());

    }


    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (this.getFragmentManager() == null) {
            return;
        }

        if (this.getFragmentManager().findFragmentByTag(FRAGMENT_DIALOG_TAG) == null) {
            if (preference instanceof TimePreference) {
                displayPreferenceDialog(new TimePreferenceDialogFragment(), preference.getKey());
            } else {
                super.onDisplayPreferenceDialog(preference);
            }
        }
    }

    public static PreferenceFragment newInstance() {
        return new PreferenceFragment();
    }
}
