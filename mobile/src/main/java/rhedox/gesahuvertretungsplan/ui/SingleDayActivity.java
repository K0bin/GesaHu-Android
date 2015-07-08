package rhedox.gesahuvertretungsplan.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Locale;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.StudentInformation;


public class SingleDayActivity extends AppCompatActivity {
    private boolean darkTheme;
    private StudentInformation studentInformation;

    public static final String OPTION_DATE ="date";

    private MainFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = prefs.getBoolean(SettingsFragment.PREF_DARK, false);
        studentInformation = new StudentInformation(prefs.getString(SettingsFragment.PREF_YEAR,"5"), prefs.getString(SettingsFragment.PREF_CLASS, "a"));

        //Theming
        if(darkTheme) {
            this.setTheme(R.style.GesahuThemeDark);
        } else {
            this.setTheme(R.style.GesahuTheme);
        }

        LocalDate date = LocalDate.now();
        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey(OPTION_DATE))
            date = new DateTime(getIntent().getExtras().getLong(OPTION_DATE)).toLocalDate();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_day);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionToolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(date.toString("EEE dd.MM.yy", Locale.GERMANY));

        fragment = (MainFragment)getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        if(fragment == null) {
            fragment = MainFragment.newInstance(studentInformation, date);
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment, MainFragment.TAG).commit();
        }
    }
}
