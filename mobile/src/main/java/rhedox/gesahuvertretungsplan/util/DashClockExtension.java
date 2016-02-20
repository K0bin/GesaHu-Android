package rhedox.gesahuvertretungsplan.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

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
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.GesahuiApi;
import rhedox.gesahuvertretungsplan.net.SubstitutesListConverterFactory;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

/**
 * Created by Robin on 19.04.2015.
 */
public class DashClockExtension extends com.google.android.apps.dashclock.api.DashClockExtension implements Callback<SubstitutesList> {

    @NonNull private GesahuiApi gesahui;

    @Override
    protected void onUpdateData(int reason) {

        LocalDate date = SchoolWeek.next();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");
        boolean specialMode = prefs.getBoolean(PreferenceFragment.PREF_SPECIAL_MODE, false);
        StudentInformation information = new StudentInformation(schoolYear, schoolClass);

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

        int count = SubstitutesList.countImportant(substitutes);

        if (count > 0) {
            publishUpdate(new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.ic_notification)
                    .status(count + " " + getString(R.string.hours))
                    .expandedTitle(getString(R.string.app_name))
                    .expandedBody(count + " " + getString(R.string.lessons))
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
