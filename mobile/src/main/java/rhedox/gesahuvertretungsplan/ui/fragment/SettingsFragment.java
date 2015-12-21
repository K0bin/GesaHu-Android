package rhedox.gesahuvertretungsplan.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;

import rhedox.gesahuvertretungsplan.App;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.DividerItemDecoration;
import rhedox.gesahuvertretungsplan.ui.PreferencesDividerItemDecoration;
import rhedox.gesahuvertretungsplan.ui.activity.WelcomeActivity;

/**
 * Created by Robin on 18.10.2014.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String PREF_YEAR ="pref_year";
    public static final String PREF_CLASS ="pref_class";
    public static final String PREF_DARK ="pref_dark";
    public static final String PREF_WIDGET_DARK ="pref_widget_dark";
    public static final String PREF_COLOR ="pref_color";
    public static final String PREF_FILTER ="pref_filter";
    public static final String PREF_WHITE_TAB_INDICATOR ="pref_white_tab_indicator";
    public static final String PREF_NOTIFICATION_TIME = "pref_notification_time_new";
    public static final String PREF_NOTIFICATION = "pref_notification";
    public static final String PREF_PREVIOUSLY_STARTED = "pref_previously_started";

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().addItemDecoration(new PreferencesDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        int margin = (int)getContext().getResources().getDimension(R.dimen.small_margin);
        int marginBottom = (int)getContext().getResources().getDimension(R.dimen.list_fab_bottom);
        getListView().setPadding(margin,0, margin, getActivity() instanceof WelcomeActivity ? marginBottom : margin);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //LeakCanary
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        if(refWatcher != null)
            refWatcher.watch(this);
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }
}
