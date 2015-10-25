package rhedox.gesahuvertretungsplan.ui.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.ui.Theming;
import rhedox.gesahuvertretungsplan.ui.adapters.PagerAdapter;
import rhedox.gesahuvertretungsplan.ui.fragment.AnnouncementFragment;
import rhedox.gesahuvertretungsplan.ui.fragment.DatePickerFragment;
import rhedox.gesahuvertretungsplan.ui.fragment.MainFragment;
import rhedox.gesahuvertretungsplan.ui.fragment.SettingsFragment;
import rhedox.gesahuvertretungsplan.util.SubstituteShareHelper;
import rhedox.gesahuvertretungsplan.util.TabLayoutHelper;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, MaterialCab.Callback, MainFragment.MaterialActivity {
    private boolean darkTheme;
    private boolean filterImportant;

    private boolean canGoBack = false;

    private PagerAdapter pagerAdapter;
    private FloatingActionButton floatingActionButton;
    private AppBarLayout appBarLayout;
    private AppBarLayout.OnOffsetChangedListener appBarLayoutOffsetListener;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private int appBarLayoutOffset = 0;

    public static final String EXTRA_DATE ="date";

    //Store Date of Monday of current week
    private LocalDate date;

    private MainFragment currentFragment;

    private MaterialCab cab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = prefs.getBoolean(SettingsFragment.PREF_DARK, false);
        filterImportant = prefs.getBoolean(SettingsFragment.PREF_FILTER, false);

        int color = prefs.getInt(SettingsFragment.PREF_COLOR, ContextCompat.getColor(this, R.color.colorDefaultAccent));
        boolean whiteIndicator = prefs.getBoolean(SettingsFragment.PREF_WHITE_TAB_INDICATOR, false);
        StudentInformation studentInformation = new StudentInformation(prefs.getString(SettingsFragment.PREF_YEAR, "5"), prefs.getString(SettingsFragment.PREF_CLASS, "a"));

        //Theming
        this.setTheme(Theming.getTheme(darkTheme, color));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionToolBar);
        setSupportActionBar(toolbar);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFragment != null && currentFragment.hasAnnouncement())
                    AnnouncementFragment.newInstance(currentFragment.getAnnouncement()).show(getSupportFragmentManager(), AnnouncementFragment.TAG);
            }
        });

        appBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        appBarLayoutOffsetListener = new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                MainActivity.this.appBarLayoutOffset = verticalOffset;
                if(currentFragment != null)
                    currentFragment.setSwipeToRefreshEnabled(verticalOffset == 0);
            }
        };
        appBarLayout.addOnOffsetChangedListener(appBarLayoutOffsetListener);

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_DATE)) {
            date = new DateTime(getIntent().getExtras().getLong(EXTRA_DATE)).toLocalDate();
            canGoBack = true;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setContentInsetsRelative((int)getResources().getDimension(R.dimen.default_content_inset), toolbar.getContentInsetEnd());
        }
        else
            date = SchoolWeek.next();

        setupViewPager(date, studentInformation, whiteIndicator, color, filterImportant);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            setupTaskDescription();

        cab = new MaterialCab(this, R.id.cab_stub);
        cab.setMenu(R.menu.menu_cab_main);
        cab.setTitleRes(R.string.main_cab_title);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(SettingsFragment.PREF_PREVIOUSLY_STARTED, false);
        if(!previouslyStarted) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void setupViewPager(LocalDate date, StudentInformation studentInformation, boolean whiteIndicator, @ColorInt int indicatorColor, boolean filterImportant)
    {
        final Pair<LocalDate, Integer> pair = MainActivity.getDate(date);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), pair.first, studentInformation, filterImportant);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(whiteIndicator ? 0xFFFFFFFF : indicatorColor);
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_DATE))
            TabLayoutHelper.setContentInsetStart(tabLayout, (int)getResources().getDimension(R.dimen.default_content_inset));

        viewPager.setCurrentItem(pair.second);
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                onPageSelected(pair.second);
            }
        });
    }

    private static Pair<LocalDate, Integer> getDate(LocalDate date)
    {
        int index = date.getDayOfWeek() - DateTimeConstants.MONDAY;
        date = date.minusDays(index);
        int dayIndex = Math.max(0, Math.min(index, 5));

        return new Pair<LocalDate, Integer>(date, dayIndex);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupTaskDescription()
    {
        TypedArray a = obtainStyledAttributes(new int[]{R.attr.colorPrimary});
        int primaryColor = a.getColor(0, 0);
        a.recycle();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_task);
        ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(getString(R.string.app_name), bitmap, primaryColor);
        this.setTaskDescription(description);
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
                LocalDate date;
                if(currentFragment != null && currentFragment.getDate() != null)
                    date = currentFragment.getDate();
                else
                    date = LocalDate.now();

                DatePickerFragment.newInstance(date, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        LocalDate date = SchoolWeek.next(new LocalDate(year, monthOfYear + 1, dayOfMonth));

                        if(date.getWeekOfWeekyear() != MainActivity.this.date.getWeekOfWeekyear()) {
                            Intent intent = new Intent(MainActivity.this.getApplicationContext(), MainActivity.class);
                            intent.putExtra(MainActivity.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }).show(getSupportFragmentManager(), DatePickerFragment.TAG);
                break;

            case R.id.action_about:
                AboutLibs.start(this, darkTheme);
                break;

            case android.R.id.home:
                if(canGoBack)
                    this.onBackPressed();
                    return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        pagerAdapter.destroy();
        if(appBarLayout != null)
            appBarLayout.removeOnOffsetChangedListener(appBarLayoutOffsetListener);

        if(viewPager != null)
            viewPager.removeOnPageChangeListener(this);

        if(floatingActionButton != null)
            floatingActionButton.setOnClickListener(null);

        super.onDestroy();
    }

    @Override
    public void onPageSelected(int position) {
        if(pagerAdapter == null || floatingActionButton == null)
            return;

        currentFragment = pagerAdapter.getFragment(position);
        if(currentFragment != null) {
            setCabVisibility(false);
            setFabVisibility(currentFragment.hasAnnouncement());
            currentFragment.setSwipeToRefreshEnabled(appBarLayoutOffset == 0);

            if(currentFragment.getAdapter() != null)
                currentFragment.getAdapter().clearSelection(false);
        }
    }

    public void setTabInset(boolean wide) {
        @DimenRes int res = (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_DATE)) || wide ? R.dimen.default_content_inset : R.dimen.default_content_inset_small;
        TabLayoutHelper.setContentInsetStart(tabLayout, (int) getResources().getDimension(res));
    }

    @Override
    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    @Override
    public void setFabVisibility(boolean visible) {
        if(floatingActionButton != null) {
            if(visible) {
                floatingActionButton.setEnabled(true);
                floatingActionButton.show();
            } else {
                floatingActionButton.hide();
                floatingActionButton.setEnabled(false);
            }
        }

    }

    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
    }

    @Override
    public void setAppBarExpanded(boolean expanded) {
        if(appBarLayout != null)
            appBarLayout.setExpanded(expanded);
    }

    public void setCabVisibility(boolean visible) {
        if(cab != null) {
            if (visible) {
                setAppBarExpanded(true);
                cab.start(this);
                setTabInset(true);
                setFabVisibility(false);
            } else {
                cab.finish();
                setTabInset(false);
                if(currentFragment != null)
                    setFabVisibility(currentFragment.hasAnnouncement());
            }
        }
    }

    @Override
    public boolean onCabItemClicked(MenuItem item) {
        if(item == null || currentFragment == null || currentFragment.getAdapter() == null)
            return false;

        Substitute substitute = currentFragment.getAdapter().getSelected();
        if(substitute == null)
            return false;

        switch(item.getItemId()) {
            case R.id.action_share:
                try {
                    SubstituteShareHelper.makeShareIntent(substitute, getApplicationContext()).send();
                }
                catch (PendingIntent.CanceledException canceled) {
                    Toast.makeText(getApplicationContext(), getText(R.string.failed), Toast.LENGTH_SHORT).show();
                }
                return true;
        }

        return false;
    }

    @Override
    public boolean onCabFinished(MaterialCab cab) {
        if(currentFragment != null && currentFragment.getAdapter() != null)
            currentFragment.getAdapter().clearSelection(true);

        return true;
    }

//region Unused interface methods
    @Override
    public void onPageScrollStateChanged(int state) {}
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public boolean onCabCreated(MaterialCab cab, Menu menu) {
        return true;
    }
    //endregion
}

//https://code.google.com/p/android/issues/detail?id=78062
