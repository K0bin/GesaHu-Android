package rhedox.gesahuvertretungsplan.broadcastReceiver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.ExtensionData;

import org.joda.time.LocalDate;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.GesaHuiApi;
import rhedox.gesahuvertretungsplan.model.QueryDate;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;

/**
 * Created by Robin on 19.04.2015.
 */
public class DashClockExtension extends com.google.android.apps.dashclock.api.DashClockExtension implements Callback<SubstitutesList> {

    private GesaHuiApi gesahui;

    @Override
    protected void onUpdateData(int reason) {

        LocalDate date = SchoolWeek.next();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");
        Student information = new Student(schoolYear, schoolClass);

        //Init retro fit for pulling the data
        gesahui = GesaHuiApi.Companion.create(getApplicationContext());

        Call<SubstitutesList> call = gesahui.substitutes(new QueryDate(date));
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<SubstitutesList> call, Response<SubstitutesList> response) {

        if(response == null || response.body() == null)
            return;

        List<Substitute> substitutes = response.body().getSubstitutes();

        List<Substitute> important = SubstitutesList.filterRelevant(substitutes, true);
        int count = important.size();

        String body = "";
        for(Substitute substitute : important) {
            String title = "";

            switch(substitute.getKind()) {
                case Substitute:
                    title = getString(R.string.substitute);
                    break;

                case RoomChange:
                    title = getString(R.string.roomchange);
                    break;

                case Dropped:
                    title = getString(R.string.dropped);
                    break;

                case Test:
                    title = getString(R.string.test);
                    break;
            }

            if(!"".equals(body))
                body += System.getProperty("line.separator");

            body += String.format(getString(R.string.notification_summary), title, substitute.getLessonText());
        }

        if (count > 0) {
            publishUpdate(new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.ic_notification)
                    .status(Integer.toString(count))
                    .expandedTitle(getString(R.string.app_name))
                    .expandedBody(body)
                    .clickIntent(new Intent(this, MainActivity.class)));
        }
        else
            publishUpdate(new ExtensionData()
                    .visible(false));

    }

    @Override
    public void onFailure(Call<SubstitutesList> call, Throwable t) {

    }
}
