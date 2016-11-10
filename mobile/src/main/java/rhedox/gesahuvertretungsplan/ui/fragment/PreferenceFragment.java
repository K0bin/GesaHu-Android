package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;

import org.joda.time.LocalTime;

import rhedox.gesahuvertretungsplan.App;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.broadcastReceiver.SubstitutesAlarmReceiver;
import rhedox.gesahuvertretungsplan.ui.activity.WelcomeActivity;
import rhedox.gesahuvertretungsplan.broadcastReceiver.BootReceiver;

/**
 * Created by Robin on 18.10.2014.
 */
public class PreferenceFragment extends PreferenceFragmentCompat {
    public static final String PREF_YEAR ="pref_year";
    public static final String PREF_CLASS ="pref_class";
    public static final String PREF_DARK_TYPE ="pref_dark_type";
    public static final String PREF_WIDGET_DARK ="pref_widget_dark";
    public static final String PREF_NOTIFICATION_TIME = "pref_notification_time_long";
    public static final String PREF_NOTIFICATION_MODE = "pref_notification_mode";
    public static final String PREF_PREVIOUSLY_STARTED = "pref_previously_started";
    public static final String PREF_AMOLED = "pref_amoled";

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int margin = (int)getContext().getResources().getDimension(R.dimen.small_margin);
        int marginBottom = (int)getContext().getResources().getDimension(R.dimen.list_fab_bottom);
        getListView().setPadding(margin, 0, margin, getActivity() instanceof WelcomeActivity ? marginBottom : margin);
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
}
