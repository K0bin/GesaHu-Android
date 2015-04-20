package rhedox.gesahuvertretungsplan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.ExtensionData;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Robin on 19.04.2015.
 */
public class DashClockExtension extends com.google.android.apps.dashclock.api.DashClockExtension implements OnDownloadedListener{
    @Override
    protected void onUpdateData(int reason) {
        ReplacementsList list = new ReplacementsList();

        Calendar calendar = SchoolWeek.next();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");

        list.load(getBaseContext(), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR), schoolYear, schoolClass, this);
    }

    @Override
    public void onDownloaded(Context context, List<Replacement> replacements) {
        if(replacements.size() > 0) {
            int count = 0;

            for(int i = 0; i < replacements.size(); i++) {
                if(replacements.get(i).getImportant())
                    count++;
            }

            publishUpdate(new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.icon_notification)
                    .status(count + " Stunden")
                    .expandedTitle("Gesahu Vertretungsplan")
                    .expandedBody(count + " Vertretungsstunden")
                    .clickIntent(new Intent(context, MainActivity.class)));
        }

    }
}
