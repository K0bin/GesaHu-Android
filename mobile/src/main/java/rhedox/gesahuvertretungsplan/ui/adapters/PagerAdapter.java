package rhedox.gesahuvertretungsplan.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;

import java.util.Locale;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.ui.fragment.MainFragment;

/**
 * Created by Robin on 29.08.2015.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private LocalDate date;

    private MainFragment[] fragments;

    public PagerAdapter(FragmentManager manager, LocalDate date) {
        super(manager);

        this.date = date;

        this.fragments = new MainFragment[getCount()];
    }

    @Override
    public Fragment getItem(int position) {
        LocalDate fragmentDate = date.withFieldAdded(DurationFieldType.days(), (position + 1) - date.getDayOfWeek());

        MainFragment fragment = MainFragment.newInstance(fragmentDate);
        fragments[position] = fragment;
        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return date.withFieldAdded(DurationFieldType.days(), (position + 1) - date.getDayOfWeek()).toString("EEE dd.MM.yy", Locale.GERMANY);
    }

    public static String makeFragmentName(int viewPagerId, int fragmentPosition) {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }

    public void destroy() {
        for(int i = 0; i<this.fragments.length; i++)
            this.fragments[i] = null;

        this.fragments = null;
    }

    public MainFragment getFragment(FragmentManager manager, int position) {
        if(position < 0 || position >= fragments.length)
            throw new IllegalArgumentException("position");

        MainFragment mainFragment = fragments[position];
        if(mainFragment != null)
            return mainFragment;
        else {
            Fragment fragment = manager.findFragmentByTag(makeFragmentName(R.id.viewPager, position));
            if(fragment != null && fragment instanceof MainFragment) {
                mainFragment = (MainFragment)fragment;
                fragments[position] = mainFragment;
                return  mainFragment;
            }
            else
                return null;
        }
    }
}
