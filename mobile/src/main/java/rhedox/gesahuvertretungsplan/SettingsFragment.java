package rhedox.gesahuvertretungsplan;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by Robin on 18.10.2014.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}
