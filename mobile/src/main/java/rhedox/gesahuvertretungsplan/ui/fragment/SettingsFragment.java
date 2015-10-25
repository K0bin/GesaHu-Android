package rhedox.gesahuvertretungsplan.ui.fragment;

import android.os.Bundle;

import rhedox.gesahuvertretungsplan.R;

/**
 * Created by Robin on 18.10.2014.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String PREF_YEAR ="pref_year";
    public static final String PREF_CLASS ="pref_class";
    public static final String PREF_DARK ="pref_dark";
    public static final String PREF_WIDGET_DARK ="pref_widget_dark";
    public static final String PREF_COLOR ="pref_color";
    public static final String PREF_FILTER ="pref_filter";
    public static final String PREF_WHITE_TAB_INDICATOR ="pref_white_tab_indicator";
    public static final String PREF_NOTIFICATION_TIME = "pref_notification_time";
    public static final String PREF_NOTIFICATION = "pref_notification";
    public static final String PREF_PREVIOUSLY_STARTED = "pref_previously_started";

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }
}