package rhedox.gesahuvertretungsplan.ui.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Locale;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.ui.Theming;
import rhedox.gesahuvertretungsplan.ui.fragment.MainFragment;
import rhedox.gesahuvertretungsplan.ui.fragment.SettingsFragment;


public class SingleDayActivity extends AppCompatActivity {
    private boolean darkTheme;
    private StudentInformation studentInformation;

    public static final String EXTRA_DATE ="date";

    private MainFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = prefs.getBoolean(SettingsFragment.PREF_DARK, false);
        boolean filterImportant = prefs.getBoolean(SettingsFragment.PREF_FILTER, false);
        studentInformation = new StudentInformation(prefs.getString(SettingsFragment.PREF_YEAR,"5"), prefs.getString(SettingsFragment.PREF_CLASS, "a"));
        int color = prefs.getInt(SettingsFragment.PREF_COLOR, getResources().getColor(R.color.colorDefaultAccent));

        //Theming
        this.setTheme(Theming.getTheme(darkTheme, color));

        LocalDate date = LocalDate.now();
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_DATE))
            date = new DateTime(getIntent().getExtras().getLong(EXTRA_DATE)).toLocalDate();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_day);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionToolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(date.toString("EEE dd.MM.yy", Locale.GERMANY));

        fragment = (MainFragment)getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        if(fragment == null) {
            fragment = MainFragment.newInstance(studentInformation, date, filterImportant);
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment, MainFragment.TAG).commit();
        }

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(fragment);
        floatingActionButton.hide();
        floatingActionButton.setEnabled(false);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            TypedArray a = obtainStyledAttributes(new int[] { R.attr.colorPrimary });
            int primaryColor = a.getColor(0, 0);
            a.recycle();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_task);
            ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(getString(R.string.app_name), bitmap, primaryColor);
            this.setTaskDescription(description);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_day, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent TargetActivity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            break;

            case R.id.action_about:
                AboutLibs.start(this, darkTheme);
            break;
        }

        return super.onOptionsItemSelected(item);
    }


}
