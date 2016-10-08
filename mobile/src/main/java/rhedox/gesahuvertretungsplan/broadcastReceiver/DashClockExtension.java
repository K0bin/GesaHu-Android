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
import retrofit2.Retrofit;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver;
import rhedox.gesahuvertretungsplan.model.old.Substitute_old;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.model.old.SubstitutesList_old;
import rhedox.gesahuvertretungsplan.model.old.GesaHuiHtml;
import rhedox.gesahuvertretungsplan.model.old.SubstitutesListConverterFactory;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;

/**
 * Created by Robin on 19.04.2015.
 */
public class DashClockExtension extends com.google.android.apps.dashclock.api.DashClockExtension implements Callback<SubstitutesList_old> {

    private GesaHuiHtml gesahui;

    @Override
    protected void onUpdateData(int reason) {

        LocalDate date = SchoolWeek.next();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");
        Student information = new Student(schoolYear, schoolClass);

        //Init retro fit for pulling the data
        //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gesahui.de")
                .addConverterFactory(new SubstitutesListConverterFactory(new AbbreviationResolver(getApplicationContext()), information))
                        //.client(client)
                .build();

        gesahui = retrofit.create(GesaHuiHtml.class);

        Call<SubstitutesList_old> call = gesahui.getSubstitutesList(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<SubstitutesList_old> call, Response<SubstitutesList_old> response) {

        if(response == null || response.body() == null)
            return;

        List<Substitute_old> substitutes = response.body().getSubstitutes();
        if(substitutes == null)
            return;

        List<Substitute_old> important = SubstitutesList_old.filterImportant(substitutes);
        int count = important.size();

        String body = "";
        for(Substitute_old substitute : important) {
            String title = "";

            switch(substitute.getKind()) {
                case Substitute_old.KIND_SUBSTITUTE:
                    title = getString(R.string.substitute);
                    break;

                case Substitute_old.KIND_ROOM_CHANGE:
                    title = getString(R.string.roomchange);
                    break;

                case Substitute_old.KIND_DROPPED:
                    title = getString(R.string.dropped);
                    break;

                case Substitute_old.KIND_TEST:
                    title = getString(R.string.test);
                    break;
            }

            if(!"".equals(body))
                body += System.getProperty("line.separator");

            body += String.format(getString(R.string.notification_summary), title, substitute.getLesson());
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
    public void onFailure(Call<SubstitutesList_old> call, Throwable t) {

    }
}
