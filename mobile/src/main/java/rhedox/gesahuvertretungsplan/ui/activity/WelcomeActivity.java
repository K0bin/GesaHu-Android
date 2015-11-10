package rhedox.gesahuvertretungsplan.ui.activity;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.fragment.SettingsFragment;

public class WelcomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private PagerAdapter pagerAdapter;

    private ViewPager viewPager;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionToolBar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if(position >= pagerAdapter.getCount() - 1)
            floatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_white_24dp));
        else
            floatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_forward_white_24dp));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        if(viewPager.getCurrentItem() < pagerAdapter.getCount() - 1)
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(SettingsFragment.PREF_PREVIOUSLY_STARTED, true);
            edit.apply();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return IntroductionFragment.newInstance(R.layout.fragment_welcome);

                case 1:
                    return IntroductionFragment.newInstance(R.layout.fragment_notify);

                case 2:
                    return SettingsFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    public static class IntroductionFragment extends Fragment {

        @LayoutRes
        private int layoutId = -1;

        public static final String ARG_LAYOUT = "arg_layout";

        public static IntroductionFragment newInstance(@LayoutRes int layoutId) {
            IntroductionFragment fragment = new IntroductionFragment();
            Bundle bundle = new Bundle(1);
            bundle.putInt(ARG_LAYOUT, layoutId);
            fragment.setArguments(bundle);
            return fragment;
        }

        public IntroductionFragment() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT))
                layoutId = getArguments().getInt(ARG_LAYOUT);

            View rootView = inflater.inflate(layoutId != -1 ? layoutId : R.layout.fragment_welcome, container, false);
            return rootView;
        }
    }

    public static class InitialSettingsFragment extends Fragment {

    }

}
