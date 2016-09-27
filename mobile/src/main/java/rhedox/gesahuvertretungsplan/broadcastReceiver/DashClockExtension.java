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
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.model.GesahuiApi;
import rhedox.gesahuvertretungsplan.model.SubstitutesListConverterFactory;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

/**
 * Created by Robin on 19.04.2015.
 */
public class DashClockExtension extends com.google.android.apps.dashclock.api.DashClockExtension implements Callback<SubstitutesList> {

    private GesahuiApi gesahui;

    @Override
    protected void onUpdateData(int reason) {

        LocalDate date = SchoolWeek.next();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");
        boolean specialMode = prefs.getBoolean(PreferenceFragment.PREF_SPECIAL_MODE, false);
        Student information = new Student(schoolYear, schoolClass);

        //Init retro fit for pulling the data
        //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gesahui.de")
                .addConverterFactory(new SubstitutesListConverterFactory(new ShortNameResolver(getApplicationContext(), specialMode), information))
                        //.client(client)
                .build();

        gesahui = retrofit.create(GesahuiApi.class);

        Call<SubstitutesList> call = gesahui.getSubstitutesList(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<SubstitutesList> call, Response<SubstitutesList> response) {

        if(response == null || response.body() == null)
            return;

        List<Substitute> substitutes = response.body().getSubstitutes();
        if(substitutes == null)
            return;

        List<Substitute> important = SubstitutesList.filterImportant(substitutes);
        int count = important.size();

        String body = "";
        for(Substitute substitute : important) {
            String title = "";

            switch(substitute.getKind()) {
                case Substitute.KIND_SUBSTITUTE:
                    title = getString(R.string.substitute);
                    break;

                case Substitute.KIND_ROOM_CHANGE:
                    title = getString(R.string.roomchange);
                    break;

                case Substitute.KIND_DROPPED:
                    title = getString(R.string.dropped);
                    break;

                case Substitute.KIND_TEST:
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
    public void onFailure(Call<SubstitutesList> call, Throwable t) {

    }
}
