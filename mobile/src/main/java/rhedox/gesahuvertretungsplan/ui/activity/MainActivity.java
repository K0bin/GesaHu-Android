package rhedox.gesahuvertretungsplan.ui.activity;

import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;

import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;

import java.util.Locale;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.ui.Theming;
import rhedox.gesahuvertretungsplan.ui.fragment.DatePickerFragment;
import rhedox.gesahuvertretungsplan.ui.fragment.MainFragment;
import rhedox.gesahuvertretungsplan.ui.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, ViewPager.OnPageChangeListener {
    private boolean darkTheme;

    //For Floating Action Button
    private PagerAdapter pagerAdapter;
    private FloatingActionButton floatingActionButton;

    //Datepicker double workaround
    private boolean picked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = prefs.getBoolean(SettingsFragment.PREF_DARK, false);
        int color = prefs.getInt(SettingsFragment.PREF_COLOR, getResources().getColor(R.color.colorDefaultAccent));
        boolean whiteIndicator = prefs.getBoolean(SettingsFragment.PREF_WHITE_TAB_INDICATOR, false);
        StudentInformation studentInformation = new StudentInformation(prefs.getString(SettingsFragment.PREF_YEAR, "5"), prefs.getString(SettingsFragment.PREF_CLASS, "a"));

        //Theming
        this.setTheme(Theming.getTheme(darkTheme, color));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionToolBar);
        setSupportActionBar(toolbar);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        LocalDate now = SchoolWeek.next();
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), now, studentInformation);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        Theming.setTabSelectorColor(tabLayout, whiteIndicator ? 0xFFFFFFFF : color);

        final int position = pagerAdapter.getItemPosition(now);
        viewPager.setCurrentItem(position);
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.onPageSelected(position);
            }
        });

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

            case R.id.action_load:
                showPicker();
                break;

            case R.id.action_about:
                AboutLibs.start(this, darkTheme);
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
        if (!picked) {
            LocalDate date = SchoolWeek.next(new LocalDate(year, monthOfYear + 1, dayOfMonth));

            Intent intent = new Intent(this, SingleDayActivity.class);
            intent.putExtra(SingleDayActivity.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        picked = true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(pagerAdapter == null || floatingActionButton == null)
            return;

        //Set
        String tag = pagerAdapter.getFragmentTag(R.id.viewPager, position);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment != null && fragment instanceof MainFragment) {
            MainFragment mainFragment = (MainFragment)fragment;
            floatingActionButton.setOnClickListener((MainFragment) fragment);
            if(mainFragment.getHasAnnouncement()) {
                floatingActionButton.setEnabled(true);
                floatingActionButton.show();
            } else {
                floatingActionButton.hide();
                floatingActionButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private StudentInformation studentInformation;

        private LocalDate date;

        public PagerAdapter(FragmentManager manager, LocalDate date, StudentInformation information) {
            super(manager);

            this.date = date;
            this.studentInformation = information;
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

//https://code.google.com/p/android/issues/detail?id=78062
