package rhedox.gesahuvertretungsplan.ui.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.view.View;
//import android.widget.DatePicker;

import com.afollestad.materialcab.MaterialCab;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.ui.adapters.PagerAdapter;
import rhedox.gesahuvertretungsplan.ui.fragment.AnnouncementFragment;
import rhedox.gesahuvertretungsplan.ui.fragment.DatePickerFragment;
import rhedox.gesahuvertretungsplan.ui.fragment.MainFragment;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;
import rhedox.gesahuvertretungsplan.util.SubstituteShareHelper;
import rhedox.gesahuvertretungsplan.util.TabLayoutHelper;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, MaterialCab.Callback, MainFragment.MaterialActivity {
    private boolean sortImportant;
    private boolean specialMode;
	private boolean isAmoledBlackEnabled;

    private boolean canGoBack = false;

    @BindView(R.id.coordinator) CoordinatorLayout coordinatorLayout;

    private PagerAdapter pagerAdapter;

    @BindView(R.id.fab) FloatingActionButton floatingActionButton;

    @BindView(R.id.appbarLayout) AppBarLayout appBarLayout;
    private AppBarLayout.OnOffsetChangedListener appBarLayoutOffsetListener;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;

    @BindView(R.id.toolbar) Toolbar toolbar;
    private Unbinder unbinder;

    public static final String EXTRA_DATE ="date";
    public static final String EXTRA_WIDGET ="widget";

    //Store Date of Monday of current week
    private LocalDate date;

    private MainFragment currentFragment;

    private MaterialCab cab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        sortImportant = prefs.getBoolean(PreferenceFragment.PREF_SORT, false);
        specialMode = prefs.getBoolean(PreferenceFragment.PREF_SPECIAL_MODE, false);

        boolean isWhiteIndicatorEnabled = prefs.getBoolean(PreferenceFragment.PREF_WHITE_TAB_INDICATOR, false);
        isAmoledBlackEnabled = prefs.getBoolean(PreferenceFragment.PREF_AMOLED, false);

        TypedArray typedArray = getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent});
        int color = typedArray.getColor(0, 0xFFFFFFFF);
        typedArray.recycle();

        //Initialize UI
        setTheme(R.style.GesahuTheme);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if(isAmoledBlackEnabled)
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        //Use AppBarLayout with SwipeRefreshLayout
        appBarLayoutOffsetListener = new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(currentFragment != null)
                    currentFragment.setSwipeToRefreshEnabled(verticalOffset == 0);
            }
        };
        appBarLayout.addOnOffsetChangedListener(appBarLayoutOffsetListener);

        //Add back button and tab bar inset
        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey(EXTRA_DATE)) {
            date = new DateTime(getIntent().getExtras().getLong(EXTRA_DATE)).toLocalDate();

            if(!extras.containsKey(EXTRA_WIDGET) || !extras.getBoolean(EXTRA_WIDGET)) {
                canGoBack = true;
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                toolbar.setContentInsetsRelative((int) getResources().getDimension(R.dimen.default_content_inset), toolbar.getContentInsetEnd());
            }
        }
        else
            date = SchoolWeek.next();

        setupViewPager(date, isWhiteIndicatorEnabled ? 0xFFFFFFFF : color, savedInstanceState != null);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setupTaskDescription();

        if (savedInstanceState != null) {
            // Restore the CAB state, save a reference to cab.
            cab = MaterialCab.restoreState(savedInstanceState, this, this);
        } else {
            cab = new MaterialCab(this, R.id.cab_stub);
            cab.setMenu(R.menu.menu_cab_main);
            cab.setTitleRes(R.string.main_cab_title);
        }
    }

    private void setupViewPager(LocalDate date, @ColorInt int indicatorColor, final boolean isRestored)
    {
        final Pair<LocalDate, Integer> pair = MainActivity.getDate(date);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), pair.first);

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(indicatorColor);
        if(canGoBack)
            TabLayoutHelper.setContentInsetStart(tabLayout, (int)getResources().getDimension(R.dimen.default_content_inset));

        if(!isRestored)
            viewPager.setCurrentItem(pair.second);

        //shitty workaround for calling onPageSelected on first page
        //WHY DO YOU DO THIS ANDROID? WHY?!
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                if (viewPager.getCurrentItem() == 0 || currentFragment == null)
                    onPageSelected(viewPager.getCurrentItem());
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

    @SuppressWarnings("unused")
    @OnClick(R.id.fab)
    public void showAnnouncement(View view) {
        if(currentFragment != null && currentFragment.getSubstitutesList() != null && currentFragment.getSubstitutesList().hasAnnouncement())
            AnnouncementFragment.newInstance(currentFragment.getSubstitutesList().getAnnouncement()).show(getSupportFragmentManager(), AnnouncementFragment.TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false);
        if(!previouslyStarted) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
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
                Intent intent = new Intent(this, PreferenceActivity.class);
                startActivity(intent);
                break;

            case R.id.action_load:
                LocalDate pickerDate;
                if(currentFragment != null && currentFragment.getDate() != null)
                    pickerDate = currentFragment.getDate();
                else if(this.date != null)
                    pickerDate = this.date;
                else
                    pickerDate = LocalDate.now();

                DatePickerFragment.newInstance(pickerDate).show(getSupportFragmentManager(), DatePickerFragment.TAG);
                break;

            case R.id.action_about:
                AboutLibs.start(this, isAmoledBlackEnabled);
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
        unbinder.unbind();

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If the CAB isn't null, save it's state for restoration in onCreate()
        if (cab != null)
            cab.saveState(outState);
    }

    @Override
    public void onPageSelected(int position) {
        if(pagerAdapter == null || floatingActionButton == null)
            return;

        currentFragment = pagerAdapter.getFragment(getSupportFragmentManager(), position);

        if(currentFragment != null)
            currentFragment.onDisplay();

        updateUI();
    }


    //Called when the displayed substitutes of the visible fragment change or a substitute gets selected
    public void updateUI() {
        updateFabVisibility();
        updateCabVisibility();
        updateTabInset();

        //Expand app bar when there are no substitutes
        if(currentFragment != null && (currentFragment.getSubstitutesList() == null || !currentFragment.getSubstitutesList().hasSubstitutes()))
            expandAppBar();
    }

    //Shows a specific date
    public void showDate(LocalDate date) {
        if(date.getWeekOfWeekyear() != this.date.getWeekOfWeekyear()) {
            //Launch a new activity with that week
            Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            //Same week => just switch to day-tab
            int index = date.getDayOfWeek() - DateTimeConstants.MONDAY;
            int dayIndex = Math.max(0, Math.min(index, 5));
            if(viewPager != null)
                viewPager.setCurrentItem(dayIndex);
        }
    }


    @Override
    public CoordinatorLayout getCoordinatorLayout() {
        return coordinatorLayout;
    }


    private void updateFabVisibility() {
        if(floatingActionButton != null) {
            boolean isVisible = false;

            //Show fab only if there is an announcement and no substitute is selected
            if(currentFragment != null)
                isVisible = currentFragment.getSubstitutesList() != null && currentFragment.getSubstitutesList().hasAnnouncement() && (currentFragment.getAdapter() == null || currentFragment.getAdapter().getSelectedIndex() == -1);

            if(isVisible) {
                floatingActionButton.setEnabled(true);
                floatingActionButton.show();
            } else {
                floatingActionButton.hide();
                floatingActionButton.setEnabled(false);
            }
        }
    }
    private void updateCabVisibility() {
        if(cab != null) {

            //Show cab if a substitute is selected and it's not visible yet (pls no stackoverflow)
            if(currentFragment != null && currentFragment.getAdapter() != null && currentFragment.getAdapter().getSelectedIndex() != -1) {
                if(!cab.isActive()) {
                    cab.start(this);
                    expandAppBar();
                }

            } else {
                if(cab.isActive())
                    cab.finish();
            }
        }
    }
    private void updateTabInset() {
        //Inset the tabs if we can go back or a substitute is selected (back button is visible)
        @DimenRes int res = cab != null && cab.isActive() || canGoBack ? R.dimen.default_content_inset : R.dimen.default_content_inset_small;
        TabLayoutHelper.setContentInsetStart(tabLayout, (int) getResources().getDimension(res));

    }
    private void expandAppBar() {
        if(appBarLayout != null)
            appBarLayout.setExpanded(true, true);
    }

    public MainFragment getVisibleFragment() {
        return currentFragment;
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
                    LocalDate date;
                    if(currentFragment.getSubstitutesList() != null)
                        date = currentFragment.getSubstitutesList().getDate();
                    else
                        date = null;

                    startActivity(SubstituteShareHelper.makeShareIntent(this, date, substitute));
                return true;
        }

        return false;
    }

    @Override
    public boolean onCabFinished(MaterialCab cab) {
        if(currentFragment != null && currentFragment.getAdapter() != null)
            currentFragment.getAdapter().clearSelection();

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