package rhedox.gesahuvertretungsplan.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.Theming;
import rhedox.gesahuvertretungsplan.ui.fragment.SettingsFragment;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean darkTheme = prefs.getBoolean("pref_dark", false);
        int color = prefs.getInt(SettingsFragment.PREF_COLOR, getResources().getColor(R.color.colorDefaultAccent));

        //Theming
        this.setTheme(Theming.getTheme(darkTheme, color));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionToolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the activity_main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .commit();
    }
}
