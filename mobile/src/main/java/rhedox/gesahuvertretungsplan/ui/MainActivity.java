package rhedox.gesahuvertretungsplan.ui;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;

import org.joda.time.DateTimeConstants;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;

import java.util.Locale;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private boolean darkTheme;

    //Datepicker double workaround
    private boolean picked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = prefs.getBoolean(SettingsFragment.PREF_DARK, false);
        StudentInformation studentInformation = new StudentInformation(prefs.getString(SettingsFragment.PREF_YEAR, "5"), prefs.getString(SettingsFragment.PREF_CLASS, "a"));

        //Theming
        if(darkTheme) {
            this.setTheme(R.style.GesahuThemeDark);
        } else {
            this.setTheme(R.style.GesahuTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionToolBar);
        setSupportActionBar(toolbar);

        LocalDate now = SchoolWeek.next();
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), now, studentInformation);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(pagerAdapter.getItemPosition(now));

        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent TargetActivity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
                break;

            /*case R.id.action_load: {
                showPicker();
            }
                break;*/

            case R.id.action_about: {
                AboutLibs.start(this, darkTheme);
            }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showPicker() {
        picked = false;
        DatePickerFragment.newInstance(LocalDate.now(), this).show(getSupportFragmentManager(), DatePickerFragment.TAG);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(!picked) {
            LocalDate date = SchoolWeek.next(new LocalDate(year, monthOfYear + 1, dayOfMonth));

            Intent intent = new Intent(this, SingleDayActivity.class);
            intent.putExtra(SingleDayActivity.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        picked = true;
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private StudentInformation studentInformation;

        private LocalDate date;

        public PagerAdapter(FragmentManager manager, LocalDate date, StudentInformation information) {
            super(manager);

            this.date = date;
            this.studentInformation = information;
        }

        public void setDate(LocalDate date) {
            if(date.getDayOfWeek() != DateTimeConstants.SUNDAY && date.getDayOfWeek() != DateTimeConstants.SATURDAY)
                this.date = date;
            else
                throw new IllegalArgumentException("Must not be saturday or sunday!");
        }

        @Override
        public Fragment getItem(int position) {
            LocalDate fragmentDate = date.withFieldAdded(DurationFieldType.days(), (position + 1) - date.getDayOfWeek());

            return MainFragment.newInstance(studentInformation, fragmentDate);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return date.withFieldAdded(DurationFieldType.days(), (position + 1) - date.getDayOfWeek()).toString("EEE dd.MM.yy", Locale.GERMANY);
        }

        public String getFragmentTag(int viewPagerId, int fragmentPosition)
        {
            return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
        }

        public int getItemPosition(LocalDate date) {
            return date.getDayOfWeek() - 1 < 5 ? date.getDayOfWeek() - 1 : 0;
        }
    }
}
