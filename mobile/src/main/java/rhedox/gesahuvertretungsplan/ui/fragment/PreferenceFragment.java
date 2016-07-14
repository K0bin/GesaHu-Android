package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;

import org.joda.time.LocalTime;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import rhedox.gesahuvertretungsplan.App;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.DividerItemDecoration;
import rhedox.gesahuvertretungsplan.ui.PreferencesDividerItemDecoration;
import rhedox.gesahuvertretungsplan.ui.activity.WelcomeActivity;
import rhedox.gesahuvertretungsplan.util.AlarmReceiver;
import rhedox.gesahuvertretungsplan.util.BootReceiver;

/**
 * Created by Robin on 18.10.2014.
 */
public class PreferenceFragment extends PreferenceFragmentCompat {
    public static final String PREF_YEAR ="pref_year";
    public static final String PREF_CLASS ="pref_class";
    public static final String PREF_DARK_TYPE ="pref_dark_type";
    public static final String PREF_WIDGET_DARK ="pref_widget_dark";
    public static final String PREF_COLOR ="pref_color";
    public static final String PREF_FILTER ="pref_filter";
    public static final String PREF_SORT ="pref_sort";
    public static final String PREF_WHITE_TAB_INDICATOR ="pref_white_tab_indicator";
    public static final String PREF_NOTIFICATION_TIME = "pref_notification_time_long";
    public static final String PREF_NOTIFICATION_MODE = "pref_notification_mode";
    public static final String PREF_PREVIOUSLY_STARTED = "pref_previously_started";
    public static final String PREF_SPECIAL_MODE = "pref_special_mode";
    public static final String PREF_AMOLED = "pref_amoled";
    public static final String PREF_NOTIFICATION_SUMMARY = "pref_notification_summary";

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
        LocalTime time = LocalTime.fromMillisOfDay(prefs.getLong(PreferenceFragment.PREF_NOTIFICATION_TIME, 0));
        String mode = prefs.getString(PREF_NOTIFICATION_MODE, null);

        if(!"none".equals(mode)) {
            @AlarmReceiver.NotificationFrequency int notificationFrequency;
            if("per_lesson".equals(mode)) {
	            AlarmReceiver.cancelDaily(getContext());
	            notificationFrequency = AlarmReceiver.PER_LESSON;
            } else if("both".equals(mode))
                notificationFrequency = AlarmReceiver.BOTH;
            else {
	            AlarmReceiver.cancelLesson(getContext());
	            notificationFrequency = AlarmReceiver.DAILY;
            }

            AlarmReceiver.create(getContext(), time.getHourOfDay(), time.getMinuteOfHour(), notificationFrequency);
            BootReceiver.create(getContext());
        } else {
	        AlarmReceiver.cancelDaily(getContext());
	        AlarmReceiver.cancelLesson(getContext());
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
