package rhedox.gesahuvertretungsplan.ui;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}