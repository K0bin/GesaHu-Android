package rhedox.gesahuvertretungsplan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.ExtensionData;

import java.util.List;

/**
 * Created by Robin on 19.04.2015.
 */
public class DashClockExtension extends com.google.android.apps.dashclock.api.DashClockExtension implements OnDownloadedListener {
    @Override
    protected void onUpdateData(int reason) {
        ReplacementsList list = new ReplacementsList();

        Date date = SchoolWeek.next();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");
        StudentInformation information = new StudentInformation(schoolYear, schoolClass);

        list.load(getBaseContext(), date, information, this);
    }

    @Override
    public void onDownloaded(Context context, List<Replacement> replacements) {
        if (replacements != null) {
            int count = 0;

            for (int i = 0; i < replacements.size(); i++) {
                if (replacements.get(i).getIsImportant())
                    count++;
            }
            if (count > 0) {
                publishUpdate(new ExtensionData()
                        .visible(true)
                        .icon(R.drawable.icon_notification)
                        .status(count + " Stunden")
                        .expandedTitle("Gesahu Vertretungsplan")
                        .expandedBody(count + " Vertretungsstunden")
                        .clickIntent(new Intent(context, MainActivity.class)));
            }
            else
                publishUpdate(new ExtensionData()
                .visible(false));
        }
    }

}
