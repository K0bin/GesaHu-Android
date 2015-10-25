package rhedox.gesahuvertretungsplan.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;

import java.util.Locale;

import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.ui.fragment.MainFragment;

/**
 * Created by Robin on 29.08.2015.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private StudentInformation studentInformation;
    private LocalDate date;

    private boolean filterImportant;

    private Fragment[] fragments;

    public PagerAdapter(FragmentManager manager, LocalDate date, StudentInformation information, boolean filterImportant) {
        super(manager);

        this.date = date;
        this.studentInformation = information;
        this.filterImportant = filterImportant;

        this.fragments = new MainFragment[getCount()];
    }

    @Override
    public Fragment getItem(int position) {
        LocalDate fragmentDate = date.withFieldAdded(DurationFieldType.days(), (position + 1) - date.getDayOfWeek());

        MainFragment fragment = MainFragment.newInstance(studentInformation, fragmentDate, filterImportant);
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

    public String getFragmentTag(int viewPagerId, int fragmentPosition) {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }

    public void destroy() {
        for(int i = 0; i<this.fragments.length;i++)
            this.fragments[i] = null;

        this.fragments = null;
    }

    public MainFragment getFragment(int position) {
        if(position < 0 || position >= fragments.length)
            throw new IllegalArgumentException("position");

        if(fragments[position] instanceof MainFragment)
            return (MainFragment)fragments[position];
        else
            return null;
    }
}