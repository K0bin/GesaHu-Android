package rhedox.gesahuvertretungsplan.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.model.old.SubstitutesList_old;
import rhedox.gesahuvertretungsplan.model.old.GesaHuiHtml;
import rhedox.gesahuvertretungsplan.model.old.SubstitutesListConverterFactory;
import rhedox.gesahuvertretungsplan.provider.CounterWidgetProvider;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

/**
 * Created by Robin on 23.07.2015.
 */
public class CounterWidgetService extends Service implements Callback<SubstitutesList_old> {
    private boolean darkTheme;
    private boolean amoled;

    private int[] allWidgetIds;
    private LocalDate date;

    public static final String EXTRA_DATE = "extra_date";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        darkTheme = prefs.getBoolean(PreferenceFragment.PREF_WIDGET_DARK, false);
        amoled = prefs.getBoolean(PreferenceFragment.PREF_AMOLED, false);
        Student student = new Student(prefs.getString(PreferenceFragment.PREF_YEAR, ""), prefs.getString(PreferenceFragment.PREF_CLASS, ""));

        date = new DateTime(intent.getLongExtra(EXTRA_DATE, 0l)).toLocalDate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gesahui.de")
                .addConverterFactory(new SubstitutesListConverterFactory(new AbbreviationResolver(getApplicationContext()), student))
                .build();

        GesaHuiHtml gesahui = retrofit.create(GesaHuiHtml.class);
        Call<SubstitutesList_old> call = gesahui.getSubstitutesList(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
        call.enqueue(this);

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onResponse(Call<SubstitutesList_old> call, Response<SubstitutesList_old> response) {
        if(response == null || !response.isSuccessful() || response.body() == null)
            return;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        for(int appWidgetId : allWidgetIds) {
            RemoteViews remoteViews = CounterWidgetProvider.makeAppWidget(getApplicationContext(), darkTheme, amoled, date, SubstitutesList_old.countImportant(response.body().getSubstitutes()));

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onFailure(Call<SubstitutesList_old> call, Throwable t) {

    }
}
