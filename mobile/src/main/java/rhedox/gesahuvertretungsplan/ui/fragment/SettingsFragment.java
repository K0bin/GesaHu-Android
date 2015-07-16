package rhedox.gesahuvertretungsplan.ui.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import rhedox.gesahuvertretungsplan.R;

/**
 * Created by Robin on 18.10.2014.
 */
public class SettingsFragment extends PreferenceFragment {
    public static final String PREF_YEAR ="pref_year";
    public static final String PREF_CLASS ="pref_class";
    public static final String PREF_DARK ="pref_dark";
    public static final String PREF_COLOR ="pref_color";
    public static final String PREF_WHITE_TAB_INDICATOR ="pref_white_tab_indicator";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}
