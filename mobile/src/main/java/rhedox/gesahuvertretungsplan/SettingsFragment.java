package rhedox.gesahuvertretungsplan;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

/**
 * Created by Robin on 18.10.2014.
 */
public class SettingsFragment extends PreferenceFragment {
    private LicensesDialog licensesDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        LicensesDialog.Builder builder = new LicensesDialog.Builder(getActivity());

        Notices notices = new Notices();
        notices.addNotice(new Notice("LicensesDialog", "https://github.com/PSDev/LicensesDialog", "PSDev", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("Android Support Library", "http://developer.android.com/tools/support-library/index.html", "Android", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("Material Design Icons", "https://github.com/google/material-design-icons", "Google", new CreativeCommonsAttribution40()));

        licensesDialog = builder.setTitle("Open Source Lizenzen").setNotices(notices).build();

        findPreference("licenses").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        licensesDialog.show();
                        return true;
                    }
                }
        );
    }
}
