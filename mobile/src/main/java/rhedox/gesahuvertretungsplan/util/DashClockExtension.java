package rhedox.gesahuvertretungsplan.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.ExtensionData;

import org.joda.time.LocalDate;

import java.util.List;

import rhedox.gesahuvertretungsplan.net.OnDownloadedListener;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.net.SubstitutesList;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.ui.MainActivity;

/**
 * Created by Robin on 19.04.2015.
 */
public class DashClockExtension extends com.google.android.apps.dashclock.api.DashClockExtension implements OnDownloadedListener {
    @Override
    protected void onUpdateData(int reason) {
        SubstitutesList list = new SubstitutesList();

        LocalDate date = SchoolWeek.next();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");
        StudentInformation information = new StudentInformation(schoolYear, schoolClass);

        list.load(getBaseContext(), date, information, this);
    }

    @Override
    public void onDownloaded(List<Substitute> substitutes) {
        if (substitutes != null) {
            int count = 0;

            for (int i = 0; i < substitutes.size(); i++) {
                if (substitutes.get(i).getIsImportant())
                    count++;
            }
            if (count > 0) {
                publishUpdate(new ExtensionData()
                        .visible(true)
                        .icon(R.drawable.icon_notification)
                        .status(count + " Stunden")
                        .expandedTitle("Gesahu Vertretungsplan")
                        .expandedBody(count + " Vertretungsstunden")
                        .clickIntent(new Intent(getBaseContext(), MainActivity.class)));
            }
            else
                publishUpdate(new ExtensionData()
                .visible(false));
        }
    }

    @Override
    public void onDownloadFailed(int error) {

    }

}
