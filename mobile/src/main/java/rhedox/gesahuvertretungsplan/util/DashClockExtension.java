package rhedox.gesahuvertretungsplan.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.Response;
import com.google.android.apps.dashclock.api.ExtensionData;

import org.joda.time.LocalDate;

import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.net.SubstituteRequest;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.VolleySingleton;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;

/**
 * Created by Robin on 19.04.2015.
 */
public class DashClockExtension extends com.google.android.apps.dashclock.api.DashClockExtension implements Response.Listener<SubstitutesList> {
    @Override
    protected void onUpdateData(int reason) {

        LocalDate date = SchoolWeek.next();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");
        StudentInformation information = new StudentInformation(schoolYear, schoolClass);

        VolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(new SubstituteRequest(this, date, information, this, null));
    }

    @Override
    public void onResponse(SubstitutesList response) {
        if(response == null || response.getSubstitutes() == null)
            return;

        List<Substitute> substitutes = response.getSubstitutes();
        int count = SubstitutesList.countImportant(substitutes);

        if (count > 0) {
            publishUpdate(new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.icon_notification)
                    .status(count + " " + getString(R.string.hours))
                    .expandedTitle(getString(R.string.app_name))
                    .expandedBody(count + " " + getString(R.string.lessons))
                    .clickIntent(new Intent(this, MainActivity.class)));
        }
        else
            publishUpdate(new ExtensionData()
                    .visible(false));
    }
}
